package com.scoreboard.app;

import com.scoreboard.persistence.ScoreKeeper;
import com.scoreboard.persistence.SessionManager;
import com.scoreboard.service.RequestHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Main class that starts a http server on localhost:8081 that keeps a high scores list.
 * It accepts the following requests:
 * <ul>
 *     <li>GET: /<userid>/login</li>
 *     <li>POST: /<levelid>/score?sessionkey=<sessionkey></li>
 *     <li>GET: /<levelid>/highscorelist</li>
 * </ul>*/
public class Main {

    private static final String HTTP_HOST = "127.0.0.1";
    private static final int HTTP_PORT = 8081;

    public static void main(final String[] args) {
        try {
            System.out.println("Starting server");
            HttpServer server = HttpServer.create(new InetSocketAddress(HTTP_HOST, HTTP_PORT), 8080);
            server.createContext("/", new RequestHandler(new SessionManager(), new ScoreKeeper()));
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
        } catch (IOException exception) {
            System.out.println("Server stopped because: " + exception.getMessage());
        }
    }
}
