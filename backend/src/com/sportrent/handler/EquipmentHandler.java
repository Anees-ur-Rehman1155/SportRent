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
        String path = ex.getRequestURI().getPath();
        List<Map<String,Object>> items = storage.readAll("equipment.json");
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
    }
}
