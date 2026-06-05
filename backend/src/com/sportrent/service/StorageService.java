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

            // 3. Seed equipment if empty
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM equipment")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("[StorageService] Seeding default equipment...");
                    writeAll("equipment.json", seedEquipment());
                }
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
          {"1", "Pro Football", "Football", 8.0, "⚽", 12, "FIFA-quality match ball with hand-stitched panels for true flight and grip in all conditions."},
          {"2", "Basketball Official", "Basketball", 10.0, "🏀", 8, "Indoor/outdoor composite leather basketball with deep channels for ultimate grip."},
          {"3", "Cricket Bat — Willow", "Cricket", 15.0, "🏏", 5, "Grade-A English willow bat, hand-pressed for explosive power and clean strike."},
          {"4", "Tennis Racket Pro", "Tennis", 12.0, "🎾", 10, "Graphite frame, 300g, 100 sq in head — balanced for control and power."},
          {"5", "Hockey Stick Carbon", "Hockey", 18.0, "🏑", 6, "70% carbon composite field hockey stick with low-bow profile for drag flicks."},
          {"6", "Volleyball Match", "Volleyball", 9.0, "🏐", 7, "FIVB-approved 18-panel microfiber volleyball with soft touch and true flight."},
          {"7", "Football Boots", "Football", 14.0, "👟", 15, "Lightweight firm-ground boots with textured upper for precise touch and lockdown fit."},
          {"8", "Cricket Pads", "Cricket", 11.0, "🛡️", 9, "Lightweight batting pads with HDF inserts and contoured knee roll for fast running."},
          {"9", "Tennis Balls (3-pack)", "Tennis", 5.0, "🎾", 30, "Pressurized championship-grade tennis balls. Sold per tube of 3."},
          {"10", "Basketball Hoop Portable", "Basketball", 25.0, "🏀", 3, "Height-adjustable portable hoop with weighted base. Great for driveways and gym days."},
          {"11", "Hockey Helmet", "Hockey", 16.0, "🪖", 8, "Vented helmet with quick-release chin strap. CE certified protection."},
          {"12", "Volleyball Net Pro", "Volleyball", 20.0, "🥅", 4, "Official 9.5m tournament net with steel cable and antenna kit."}
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
