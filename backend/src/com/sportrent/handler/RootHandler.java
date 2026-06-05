package com.sportrent.handler;
import com.sun.net.httpserver.*;
import com.sportrent.service.Http;
import java.io.IOException;

public class RootHandler implements HttpHandler {
    public void handle(HttpExchange ex) throws IOException {
        if (Http.cors(ex)) return;
        Http.json(ex, 200, "{\"app\":\"SportRent\",\"status\":\"ok\"}");
    }
}
