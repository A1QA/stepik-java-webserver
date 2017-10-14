package main;

import server.Server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Server started");
        new Server().join();
    }
}
