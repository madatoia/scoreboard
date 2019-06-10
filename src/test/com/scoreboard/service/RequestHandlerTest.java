package com.scoreboard.service;

import com.scoreboard.persistence.ScoreKeeper;
import com.scoreboard.persistence.SessionManager;
import com.sun.net.httpserver.HttpExchange;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class RequestHandlerTest {

    private static final String MOCKED_SESSION_KEY = "mockedSessionKey";
    private static final String SCOREBOARD = "1=2,2=3";
    private final SessionManager mockedSessionManager = Mockito.mock(SessionManager.class);
    private final ScoreKeeper mockedScoreKeeper = Mockito.mock(ScoreKeeper.class);
    private final RequestHandler requestHandlerTestee = new RequestHandler(mockedSessionManager, mockedScoreKeeper);

    @Test
    public void handleInvalidRequestGetSession() throws URISyntaxException, IOException {
        //given
        final HttpExchange httpExchange = Mockito.mock(HttpExchange.class);
        Mockito.when(httpExchange.getRequestMethod()).thenReturn("GET");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(new URI("/some/request"));
        Mockito.when(httpExchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());

        //when
        requestHandlerTestee.handle(httpExchange);

        //then 400 response is returned
        Mockito.verify(httpExchange).sendResponseHeaders(Mockito.eq(400), Mockito.anyLong());
    }

    @Test
    public void handleValidRequestGetSession() throws URISyntaxException, IOException, InterruptedException {
        //given
        final HttpExchange httpExchange = Mockito.mock(HttpExchange.class);
        Mockito.when(httpExchange.getRequestMethod()).thenReturn("GET");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(new URI("/1/login"));
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        Mockito.when(httpExchange.getResponseBody()).thenReturn(os);

        Mockito.when(mockedSessionManager.getSessionIdForUser(Mockito.eq("1"))).thenReturn(MOCKED_SESSION_KEY);
        //when
        requestHandlerTestee.handle(httpExchange);

        //then 200 response is returned
        Mockito.verify(httpExchange).sendResponseHeaders(Mockito.eq(200), Mockito.eq((long) MOCKED_SESSION_KEY.length()));
        Mockito.verify(httpExchange, Mockito.times(1)).getResponseBody();
        Assert.assertEquals(MOCKED_SESSION_KEY, os.toString());
    }

    @Test
    public void handleValidRequestGetHighScore() throws URISyntaxException, IOException, InterruptedException {
        //given
        final HttpExchange httpExchange = Mockito.mock(HttpExchange.class);
        Mockito.when(httpExchange.getRequestMethod()).thenReturn("GET");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(new URI("/1/highscorelist"));
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        Mockito.when(httpExchange.getResponseBody()).thenReturn(os);

        Mockito.when(mockedScoreKeeper.getHighScoreBoardForLevel(Mockito.eq("1"))).thenReturn(SCOREBOARD);
        //when
        requestHandlerTestee.handle(httpExchange);

        //then 200 response is returned
        Mockito.verify(httpExchange).sendResponseHeaders(Mockito.eq(200), Mockito.eq((long) SCOREBOARD.length()));
        Mockito.verify(httpExchange, Mockito.times(1)).getResponseBody();
        Assert.assertEquals(SCOREBOARD, os.toString());
    }

    @Test
    public void handleValidRequestPostScore() throws URISyntaxException, InterruptedException, IOException {
        //given
        final HttpExchange httpExchange = Mockito.mock(HttpExchange.class);
        Mockito.when(httpExchange.getRequestMethod()).thenReturn("POST");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(new URI("/2/score?sessionkey=abc-123"));
        Mockito.when(httpExchange.getRequestBody()).thenReturn(Mockito.mock(InputStream.class));
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        Mockito.when(httpExchange.getResponseBody()).thenReturn(os);

        Mockito.when(mockedSessionManager.getUserForSession(Mockito.eq("abc-123"))).thenReturn(2);

        //when
        requestHandlerTestee.handle(httpExchange);

        //then scoreKeeper is being called with the correct parameters
        Mockito.verify(mockedScoreKeeper).updateScoreBoard(Mockito.eq("2"), Mockito.eq(2), Mockito.any());
    }

    @Test
    public void handleInValidRequestPostScore() throws URISyntaxException, IOException {
        //given
        final HttpExchange httpExchange = Mockito.mock(HttpExchange.class);
        Mockito.when(httpExchange.getRequestMethod()).thenReturn("POST");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(new URI("/2/score?something=abc-123"));
        Mockito.when(httpExchange.getRequestBody()).thenReturn(Mockito.mock(InputStream.class));
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        Mockito.when(httpExchange.getResponseBody()).thenReturn(os);

        //when
        requestHandlerTestee.handle(httpExchange);

        //then 400 response is returned
        Mockito.verify(httpExchange).sendResponseHeaders(Mockito.eq(400), Mockito.anyLong());
    }
}