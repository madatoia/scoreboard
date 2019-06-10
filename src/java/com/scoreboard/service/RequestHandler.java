package com.scoreboard.service;

import com.scoreboard.persistence.ScoreKeeper;
import com.scoreboard.persistence.SessionManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class RequestHandler implements HttpHandler {

    private static final String PATH_SEPARATOR = "/";
    private static final String LOGIN = "login";
    private static final String HIGHSCORELIST = "highscorelist";

    private final SessionManager sessionManager;
    private final ScoreKeeper scoreKeeper;

    public RequestHandler(final SessionManager sessionManager, final ScoreKeeper scoreKeeper) {
        this.sessionManager = sessionManager;
        this.scoreKeeper = scoreKeeper;
    }

    @Override
    public void handle(final HttpExchange httpExchange) throws IOException {
        System.out.println("Serving the request: " + httpExchange.getRequestURI());

        final StringBuilder response = new StringBuilder();
        int responseCode = 200;
        try {
            handleRequest(httpExchange, response);
        } catch (IllegalArgumentException | InterruptedException e) {
            response.append(e.getMessage());
            responseCode = 400;
        }
        writeResponse(httpExchange, response.toString(), responseCode);
    }

    private void handleRequest(final HttpExchange httpExchange, final StringBuilder response) throws InterruptedException {
        String responseBody;
        switch (httpExchange.getRequestMethod()) {
            case "GET": {
                responseBody = solveGetRequest(httpExchange.getRequestURI().getPath());
                response.append(responseBody);
                break;
            }
            case "POST": {
                solvePostRequest(httpExchange);
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid request");
        }
    }

    private void solvePostRequest(final HttpExchange httpExchange) throws IllegalArgumentException {
        final String[] splitRequest = httpExchange.getRequestURI().getPath().split(PATH_SEPARATOR);

        if (splitRequest[2].startsWith("score") && httpExchange.getRequestURI().getQuery().startsWith("sessionkey=")) {
            final int userId = sessionManager.getUserForSession(httpExchange.getRequestURI().getQuery().split("=")[1]);
            scoreKeeper.updateScoreBoard(splitRequest[1], userId, httpExchange.getRequestBody());
        } else {
            throw new IllegalArgumentException("Invalid request");
        }
    }

    private String solveGetRequest(final String request) throws IllegalArgumentException, InterruptedException {
        final String[] splitRequest = request.split(PATH_SEPARATOR);

        if (splitRequest.length == 3) {
            if (LOGIN.equals(splitRequest[2])) {
                return sessionManager.getSessionIdForUser(splitRequest[1]);
            } else if (HIGHSCORELIST.equals(splitRequest[2])) {
                return scoreKeeper.getHighScoreBoardForLevel(splitRequest[1]);
            }
        }
        throw new IllegalArgumentException("Invalid request");
    }

    private void writeResponse(final HttpExchange httpExchange, final String response, final int responseCode) throws IOException {
        httpExchange.sendResponseHeaders(responseCode, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
