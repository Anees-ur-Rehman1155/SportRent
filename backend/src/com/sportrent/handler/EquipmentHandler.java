package com.sportrent.handler;
import com.sun.net.httpserver.*;
import com.sportrent.service.*;
import java.io.IOException;
import java.util.*;

public class EquipmentHandler implements HttpHandler {
    private final StorageService storage;
    public EquipmentHandler(StorageService s) { this.storage = s; }

    public void handle(HttpExchange ex) throws IOException {
        if (Http.cors(ex)) return;
        String method = ex.getRequestMethod();
        String path = ex.getRequestURI().getPath();
        List<Map<String,Object>> items = storage.readAll("equipment.json");

        if ("GET".equals(method)) {
            // /api/equipment           -> list
            // /api/equipment/{id}      -> single
            if (path.equals("/api/equipment") || path.equals("/api/equipment/")) {
                Http.json(ex, 200, Json.stringify(items)); return;
            }
            String id = path.substring("/api/equipment/".length());
            for (Map<String,Object> it : items) {
                if (id.equals(String.valueOf(it.get("id")))) { Http.json(ex, 200, Json.stringify(it)); return; }
            }
            Http.error(ex, 404, "Equipment not found");
            return;
        }

        if ("PUT".equals(method)) {
            String id = path.substring("/api/equipment/".length());
            try {
                Map<String,Object> patch = Json.obj(Http.body(ex));
                for (Map<String,Object> it : items) {
                    if (id.equals(String.valueOf(it.get("id")))) {
                        if (patch.containsKey("price")) {
                            it.put("price", Double.parseDouble(String.valueOf(patch.get("price"))));
                        }
                        if (patch.containsKey("stock")) {
                            it.put("stock", Integer.parseInt(String.valueOf(patch.get("stock"))));
                        }
                        storage.writeAll("equipment.json", items);
                        Http.json(ex, 200, Json.stringify(it));
                        return;
                    }
                }
                Http.error(ex, 404, "Equipment not found");
            } catch (Exception e) {
                Http.error(ex, 400, e.getMessage());
            }
            return;
        }

        Http.error(ex, 405, "Method not allowed");
    }
}
