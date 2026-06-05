package com.sportrent;

import com.sun.net.httpserver.*;
import com.sportrent.handler.*;
import com.sportrent.service.StorageService;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws Exception {
        StorageService storage = new StorageService("data");
        storage.bootstrap();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/auth/", new AuthHandler(storage));
        server.createContext("/api/equipment", new EquipmentHandler(storage));
        server.createContext("/api/rentals", new RentalHandler(storage));
        server.createContext("/", new RootHandler());
        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(8));
        server.start();
        System.out.println("\n  SportRent backend running on http://localhost:8080");
        System.out.println("  Data stored in: " + new java.io.File("data").getAbsolutePath());
        System.out.println("  Press Ctrl+C to stop.\n");
    }
}
