package com.scoreboard.it;

import com.scoreboard.RequestController;
import com.scoreboard.app.Main;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class IntegrationTest {

    private static final int MAX_THREAD_COUNT = 40;
    private static final int LEVEL_ID = 10;

    private RequestController requestController = new RequestController();

    @Test
    public void testMultipleUsersWithLoginAndUpdateScoreBoard() throws Exception {
        //given
        final String[] args = new String[0];
        final List<ClientThread> clientThreads = new ArrayList<>();

        //when
        Main.main(args);
        runMultipleClientRequests(clientThreads, LEVEL_ID);

        //then
        checkResponseCodeOK(clientThreads);
        checkHighScoreBoard(LEVEL_ID);
    }

    /**
     * Verifies that the highscore board contains only 15 entries for the provided level
     *
     * @param levelId the level for which results were posted
     */
    private void checkHighScoreBoard(final int levelId) throws Exception {
        final String highScoreList = requestController.sendGetHighscoreList(levelId);
        final String[] entries = highScoreList.split(",");

        Assert.assertEquals(15, entries.length);
    }

    /**
     * Verifies that all threads ran successful requests
     *
     * @param clientThreads the threads
     */
    private void checkResponseCodeOK(final List<ClientThread> clientThreads) {
        for (final ClientThread thread : clientThreads) {
            Assert.assertEquals(200, thread.responseCode);
        }
    }

    private void runMultipleClientRequests(final List<ClientThread> clientThreads, final int levelId) throws InterruptedException {
        createClientThreads(clientThreads, levelId);
        executeLoginAndPostScoreInEachThread(clientThreads);
        joinThreads(clientThreads);
    }

    private void joinThreads(final List<ClientThread> clientThreads) throws InterruptedException {
        for(final ClientThread thread : clientThreads ){
            thread.join();
        }
    }

    private void executeLoginAndPostScoreInEachThread(final List<ClientThread> clientThreads) {
        for(final ClientThread thread : clientThreads ){
            thread.start();
        }
    }

    private void createClientThreads(final List<ClientThread> clientThreads, final int levelId) {
        for(int i=0; i < MAX_THREAD_COUNT; i++ ){
            clientThreads.add(new ClientThread(levelId, ""+i));
        }
    }


    /**
     * Class that does a login and then posts a for the provided userId and stores the response code
     */
    private class ClientThread extends Thread {

        final int level;
        final String userId;
        int responseCode;

        ClientThread(final int level, final String userId) {
            this.level = level;
            this.userId = userId;
        }

        public void run() {
            try {
                final RequestController requestController = new RequestController();
                final String sessionId = requestController.sendGetLogin(userId);
                responseCode = requestController.sendPost(level, sessionId);

            } catch (Exception e) {
                System.out.println("Request Failed: " + e.getMessage());
            }
        }
    }
}