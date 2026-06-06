package com.sportrent.handler;

import com.sun.net.httpserver.*;
import com.sportrent.service.*;
import java.io.IOException;
import java.util.*;

public class UserHandler implements HttpHandler {
    private final StorageService storage;

    public UserHandler(StorageService s) {
        this.storage = s;
    }

    public void handle(HttpExchange ex) throws IOException {
        if (Http.cors(ex)) return;
        String method = ex.getRequestMethod();
        String path = ex.getRequestURI().getPath();

        try {
            List<Map<String, Object>> users = storage.readAll("users.json");

            if ("GET".equals(method)) {
                // GET /api/users
                List<Map<String, Object>> clean = new ArrayList<>();
                for (Map<String, Object> u : users) {
                    Map<String, Object> m = new LinkedHashMap<>(u);
                    m.remove("password");
                    clean.add(m);
                }
                Http.json(ex, 200, Json.stringify(clean));
                return;
            }

            if ("DELETE".equals(method)) {
                // DELETE /api/users/{id}
                String id = path.substring("/api/users/".length());
                boolean found = false;

                // We must use a regular loop or iterator to modify list
                for (Iterator<Map<String, Object>> it = users.iterator(); it.hasNext(); ) {
                    Map<String, Object> u = it.next();
                    if (id.equals(String.valueOf(u.get("id")))) {
                        if ("ADMIN".equals(u.get("role"))) {
                            Http.error(ex, 403, "Cannot delete administrator accounts");
                            return;
                        }
                        it.remove();
                        found = true;
                        break;
                    }
                }

                if (found) {
                    // Note: MySQL FOREIGN KEY constraints will automatically drop associated rentals.
                    // But if there is a cached/in-memory list or we want to run a DELETE query directly:
                    // Since readAll/writeAll handles write-back to users table, we execute it.
                    // To clean up rentals in MySQL since writeAll(users.json) just saves users:
                    // If we need to remove rentals from MySQL first to prevent constraint violations 
                    // when running under strict DB constraints:
                    try (java.sql.Connection conn = DatabaseConnection.getConnection();
                         java.sql.PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
                        pstmt.setString(1, id);
                        pstmt.executeUpdate();
                    }
                    
                    storage.writeAll("users.json", users);
                    Http.json(ex, 200, "{\"success\":true}");
                } else {
                    Http.error(ex, 404, "User not found");
                }
                return;
            }

            Http.error(ex, 405, "Method not allowed");
        } catch (Exception e) {
            Http.error(ex, 400, e.getMessage());
        }
    }
}
