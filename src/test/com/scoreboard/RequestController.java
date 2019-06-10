package com.scoreboard;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class RequestController {

    private static final String USER_AGENT = "Mozilla/5.0";

    public String sendGetLogin(final String userId) throws Exception {
        final String url = "http://localhost:8081/" + userId + "/login";
        final HttpURLConnection con = createConnection(url);
        con.setRequestMethod("GET");

        return createResponse(con);
    }

    public String sendGetHighscoreList(final int levelId) throws Exception {
        final String url = "http://localhost:8081/" + levelId + "/highscorelist";
        final HttpURLConnection con = createConnection(url);
        con.setRequestMethod("GET");

        return createResponse(con);
    }

    public int sendPost(int levelId, String sessionId) throws Exception {

        final Random generator = new Random();
        final String url = "http://localhost:8081/" + levelId + "/score?";
        final String urlParameters = "sessionkey=" + sessionId;

        final HttpURLConnection con = createConnection(url+urlParameters);
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        final String score = "" + generator.nextInt(Integer.MAX_VALUE);

        // Send post request
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(score);
        wr.flush();
        wr.close();

        return con.getResponseCode();
    }

    private HttpURLConnection createConnection(final String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("User-Agent", USER_AGENT);

        return con;
    }

    private String createResponse(final HttpURLConnection con) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}
