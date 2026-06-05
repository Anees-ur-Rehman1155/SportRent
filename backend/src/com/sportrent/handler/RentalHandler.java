package com.sportrent.handler;
import com.sun.net.httpserver.*;
import com.sportrent.service.*;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class RentalHandler implements HttpHandler {
    private final StorageService storage;
    public RentalHandler(StorageService s) { this.storage = s; }

    public void handle(HttpExchange ex) throws IOException {
        if (Http.cors(ex)) return;
        String method = ex.getRequestMethod();
        String path = ex.getRequestURI().getPath();
        try {
            List<Map<String,Object>> rentals = storage.readAll("rentals.json");
            if ("GET".equals(method)) { Http.json(ex, 200, Json.stringify(rentals)); return; }
            if ("POST".equals(method)) {
                Map<String,Object> body = Json.obj(Http.body(ex));
                List<Map<String,Object>> equipment = storage.readAll("equipment.json");
                Map<String,Object> eq = null;
                for (Map<String,Object> e : equipment) if (String.valueOf(body.get("equipmentId")).equals(String.valueOf(e.get("id")))) { eq = e; break; }
                Map<String,Object> r = new LinkedHashMap<>(body);
                r.put("id", "r" + System.currentTimeMillis());
                r.put("status", "PENDING");
                r.put("createdAt", Instant.now().toString());
                if (eq != null) { r.put("equipmentName", eq.get("name")); r.put("equipmentEmoji", eq.get("emoji")); }
                rentals.add(0, r);
                storage.writeAll("rentals.json", rentals);
                Http.json(ex, 201, Json.stringify(r));
                return;
            }
            if ("PUT".equals(method)) {
                String id = path.substring("/api/rentals/".length());
                Map<String,Object> patch = Json.obj(Http.body(ex));
                for (Map<String,Object> r : rentals) {
                    if (id.equals(String.valueOf(r.get("id")))) {
                        r.putAll(patch);
                        storage.writeAll("rentals.json", rentals);
                        Http.json(ex, 200, Json.stringify(r)); return;
                    }
                }
                Http.error(ex, 404, "Rental not found"); return;
            }
            Http.error(ex, 405, "Method not allowed");
        } catch (Exception e) { Http.error(ex, 400, e.getMessage()); }
    }
}
