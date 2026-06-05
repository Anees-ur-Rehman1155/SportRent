package com.sportrent.handler;
import com.sun.net.httpserver.*;
import com.sportrent.service.*;
import java.io.IOException;
import java.util.*;

public class AuthHandler implements HttpHandler {
    private final StorageService storage;
    public AuthHandler(StorageService s) { this.storage = s; }

    public void handle(HttpExchange ex) throws IOException {
        if (Http.cors(ex)) return;
        String path = ex.getRequestURI().getPath();
        try {
            if (path.endsWith("/login")) login(ex);
            else if (path.endsWith("/register")) register(ex);
            else Http.error(ex, 404, "Not found");
        } catch (Exception e) { Http.error(ex, 400, e.getMessage()); }
    }

    private void login(HttpExchange ex) throws IOException {
        Map<String,Object> body = Json.obj(Http.body(ex));
        String email = (String) body.get("email"), pw = (String) body.get("password");
        List<Map<String,Object>> users = storage.readAll("users.json");
        for (Map<String,Object> u : users) {
            if (email.equals(u.get("email")) && pw.equals(u.get("password"))) {
                respondAuth(ex, u); return;
            }
        }
        Http.error(ex, 401, "Invalid email or password");
    }

    private void register(HttpExchange ex) throws IOException {
        Map<String,Object> body = Json.obj(Http.body(ex));
        String email = (String) body.get("email");
        List<Map<String,Object>> users = storage.readAll("users.json");
        for (Map<String,Object> u : users) {
            if (email.equals(u.get("email"))) { Http.error(ex, 409, "Email already in use"); return; }
        }
        Map<String,Object> u = new LinkedHashMap<>();
        u.put("id", "u" + System.currentTimeMillis());
        u.put("name", body.get("name"));
        u.put("email", email);
        u.put("password", body.get("password"));
        u.put("role", "CUSTOMER");
        users.add(u);
        storage.writeAll("users.json", users);
        respondAuth(ex, u);
    }

    private void respondAuth(HttpExchange ex, Map<String,Object> u) throws IOException {
        Map<String,Object> pub = new LinkedHashMap<>();
        pub.put("id", u.get("id")); pub.put("name", u.get("name"));
        pub.put("email", u.get("email")); pub.put("role", u.get("role"));
        Map<String,Object> resp = new LinkedHashMap<>();
        resp.put("token", "tok-" + u.get("id")); resp.put("user", pub);
        Http.json(ex, 200, Json.stringify(resp));
    }
}
