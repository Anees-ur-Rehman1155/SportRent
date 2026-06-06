package com.sportrent.service;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/** Loads/saves each entity collection using MySQL database tables. */
public class StorageService {
    
    public StorageService(String dir) {
        // Directory argument is kept for compatibility, not used for file storage now
    }

    public void bootstrap() throws IOException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 1. Create tables if they do not exist
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "id VARCHAR(50) PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL UNIQUE, " +
                    "password VARCHAR(100) NOT NULL, " +
                    "role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER'" +
                    ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS equipment (" +
                    "id VARCHAR(50) PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "sport VARCHAR(50) NOT NULL, " +
                    "price DOUBLE NOT NULL DEFAULT 0.0, " +
                    "emoji VARCHAR(10) NOT NULL, " +
                    "stock INT NOT NULL DEFAULT 0, " +
                    "desc_text TEXT" +
                    ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS rentals (" +
                    "id VARCHAR(50) PRIMARY KEY, " +
                    "userId VARCHAR(50) NOT NULL, " +
                    "userName VARCHAR(100) NOT NULL, " +
                    "equipmentId VARCHAR(50) NOT NULL, " +
                    "equipmentName VARCHAR(100) NOT NULL, " +
                    "equipmentEmoji VARCHAR(10) NOT NULL, " +
                    "quantity INT NOT NULL DEFAULT 1, " +
                    "days INT NOT NULL DEFAULT 1, " +
                    "startDate VARCHAR(20) NOT NULL, " +
                    "total DOUBLE NOT NULL DEFAULT 0.0, " +
                    "status VARCHAR(20) NOT NULL DEFAULT 'PENDING', " +
                    "createdAt VARCHAR(50) NOT NULL, " +
                    "FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (equipmentId) REFERENCES equipment(id) ON DELETE CASCADE" +
                    ")");

            System.out.println("[StorageService] Tables checked/created successfully.");

            // 2. Seed users if empty
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("[StorageService] Seeding default users...");
                    writeAll("users.json", seedUsers());
                }
            }

            // 3. Seed equipment if empty or contains legacy emojis
            boolean needsReseed = false;
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM equipment")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    needsReseed = true;
                }
            }
            if (!needsReseed) {
                try (ResultSet rs = stmt.executeQuery("SELECT emoji FROM equipment")) {
                    while (rs.next()) {
                        String em = rs.getString(1);
                        if (em != null && (em.contains("⚽") || em.contains("👟") || em.contains("🏀") || em.contains("🏏") || 
                                           em.contains("🎾") || em.contains("🏑") || em.contains("🪖") || em.contains("🏐") || 
                                           em.contains("🥅") || em.length() == 1 || em.length() == 2)) {
                            needsReseed = true;
                            break;
                        }
                    }
                }
            }
            if (needsReseed) {
                System.out.println("[StorageService] Re-seeding equipment (clean SVG string keys)...");
                stmt.executeUpdate("DELETE FROM rentals");
                stmt.executeUpdate("DELETE FROM equipment");
                writeAll("equipment.json", seedEquipment());
            }

        } catch (SQLException e) {
            System.err.println("[StorageService] Error during bootstrap: " + e.getMessage());
            throw new IOException(e);
        }
    }

    public synchronized List<Map<String, Object>> readAll(String file) {
        List<Map<String, Object>> out = new ArrayList<>();
        String query = "";
        
        if ("users.json".equals(file)) {
            query = "SELECT id, name, email, password, role FROM users";
        } else if ("equipment.json".equals(file)) {
            query = "SELECT id, name, sport, price, emoji, stock, desc_text FROM equipment";
        } else if ("rentals.json".equals(file)) {
            query = "SELECT id, userId, userName, equipmentId, equipmentName, equipmentEmoji, quantity, days, startDate, total, status, createdAt FROM rentals";
        } else {
            return out; // unsupported
        }

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    String colName = meta.getColumnLabel(i);
                    Object val = rs.getObject(i);
                    // Rename MySQL column name back to frontend expected name
                    if ("desc_text".equals(colName)) {
                        colName = "desc";
                    }
                    row.put(colName, val);
                }
                out.add(row);
            }

        } catch (SQLException e) {
            System.err.println("[StorageService] Error reading database: " + e.getMessage());
        }
        return out;
    }

    public synchronized void writeAll(String file, List<? extends Map<String, Object>> rows) {
        if ("users.json".equals(file)) {
            String sql = "INSERT INTO users (id, name, email, password, role) VALUES (?, ?, ?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE name = VALUES(name), password = VALUES(password), role = VALUES(role)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (Map<String, Object> r : rows) {
                    pstmt.setString(1, String.valueOf(r.get("id")));
                    pstmt.setString(2, String.valueOf(r.get("name")));
                    pstmt.setString(3, String.valueOf(r.get("email")));
                    pstmt.setString(4, String.valueOf(r.get("password")));
                    pstmt.setString(5, String.valueOf(r.get("role")));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            } catch (SQLException e) {
                System.err.println("[StorageService] Error saving users: " + e.getMessage());
            }
        } else if ("equipment.json".equals(file)) {
            String sql = "INSERT INTO equipment (id, name, sport, price, emoji, stock, desc_text) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE name = VALUES(name), sport = VALUES(sport), price = VALUES(price), " +
                         "emoji = VALUES(emoji), stock = VALUES(stock), desc_text = VALUES(desc_text)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (Map<String, Object> r : rows) {
                    pstmt.setString(1, String.valueOf(r.get("id")));
                    pstmt.setString(2, String.valueOf(r.get("name")));
                    pstmt.setString(3, String.valueOf(r.get("sport")));
                    pstmt.setDouble(4, Double.parseDouble(String.valueOf(r.get("price"))));
                    pstmt.setString(5, String.valueOf(r.get("emoji")));
                    pstmt.setInt(6, Integer.parseInt(String.valueOf(r.get("stock"))));
                    pstmt.setString(7, String.valueOf(r.get("desc")));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            } catch (SQLException e) {
                System.err.println("[StorageService] Error saving equipment: " + e.getMessage());
            }
        } else if ("rentals.json".equals(file)) {
            String sql = "INSERT INTO rentals (id, userId, userName, equipmentId, equipmentName, equipmentEmoji, quantity, days, startDate, total, status, createdAt) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE status = VALUES(status)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (Map<String, Object> r : rows) {
                    pstmt.setString(1, String.valueOf(r.get("id")));
                    pstmt.setString(2, String.valueOf(r.get("userId")));
                    pstmt.setString(3, String.valueOf(r.get("userName")));
                    pstmt.setString(4, String.valueOf(r.get("equipmentId")));
                    pstmt.setString(5, String.valueOf(r.get("equipmentName")));
                    pstmt.setString(6, String.valueOf(r.get("equipmentEmoji")));
                    pstmt.setInt(7, Integer.parseInt(String.valueOf(r.get("quantity"))));
                    pstmt.setInt(8, Integer.parseInt(String.valueOf(r.get("days"))));
                    pstmt.setString(9, String.valueOf(r.get("startDate")));
                    pstmt.setDouble(10, Double.parseDouble(String.valueOf(r.get("total"))));
                    pstmt.setString(11, String.valueOf(r.get("status")));
                    pstmt.setString(12, String.valueOf(r.get("createdAt")));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            } catch (SQLException e) {
                System.err.println("[StorageService] Error saving rentals: " + e.getMessage());
            }
        }
    }

    private List<Map<String, Object>> seedUsers() {
        List<Map<String, Object>> u = new ArrayList<>();
        u.add(Map.of("id", "u1", "name", "Demo Customer", "email", "demo@playrent.com", "password", "demo123", "role", "CUSTOMER"));
        u.add(Map.of("id", "a1", "name", "Admin", "email", "admin4451@gmail.com", "password", "admin1155", "role", "ADMIN"));
        u.add(Map.of("id", "s1", "name", "Staff Member", "email", "staff4451@gmail.com", "password", "staff1155", "role", "STAFF"));
        return u;
    }

    private List<Map<String, Object>> seedEquipment() {
        List<Map<String, Object>> e = new ArrayList<>();
        Object[][] data = {
          {"1", "Pro Match Football", "Football", 8.0, "football", 12, "FIFA-quality match ball with hand-stitched panels."},
          {"2", "Football Boots", "Football", 14.0, "boots", 15, "Lightweight firm-ground boots for precise touch."},
          {"3", "Indoor Game Ball", "Basketball", 7.0, "basketball", 18, "Composite leather basketball for indoor/outdoor use."},
          {"4", "Portable Hoop", "Basketball", 25.0, "hoop", 3, "Height-adjustable hoop with weighted base."},
          {"5", "English Willow Bat", "Cricket", 16.0, "bat", 9, "Grade-A English willow, hand-pressed for power."},
          {"6", "Cricket Pads", "Cricket", 11.0, "pads", 8, "Lightweight batting pads with HDF inserts."},
          {"7", "Tour Pro Racket", "Tennis", 12.0, "racket", 11, "Graphite frame, 300g — balanced for control and power."},
          {"8", "Tennis Balls (3-pack)", "Tennis", 5.0, "tennis-ball", 30, "Pressurized championship-grade tennis balls. Tube of 3."},
          {"9", "Composite Field Stick", "Hockey", 11.0, "hockey-stick", 14, "70% carbon composite stick with low-bow profile."},
          {"10", "Hockey Helmet", "Hockey", 16.0, "helmet", 8, "Vented helmet with quick-release chin strap."},
          {"11", "Tournament Volleyball", "Volleyball", 6.0, "volleyball", 15, "FIVB-approved 18-panel microfiber volleyball."},
          {"12", "Volleyball Net Pro", "Volleyball", 20.0, "net", 4, "Official 9.5m tournament net with antenna kit."}
        };
        for (Object[] d : data) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", d[0]); m.put("name", d[1]); m.put("sport", d[2]);
            m.put("price", d[3]); m.put("emoji", d[4]); m.put("stock", d[5]); m.put("desc", d[6]);
            e.add(m);
        }
        return e;
    }
}
