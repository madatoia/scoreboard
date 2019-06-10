package com.scoreboard.it;

import com.scoreboard.RequestController;
import com.scoreboard.app.Main;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MainUpdateScoreStressTest {

    private static final int MAX_THREAD_COUNT = 1000;
    private static final int LEVEL_ID = 11;
    private static final String USER_ID = "1234";
    private final RequestController requestController = new RequestController();

    @Test
    public void testUpdateScoreBoard() throws Exception {
        //given
        final String[] args = new String[0];
        final List<UpdateBoardThread> updateBoardThreads = new ArrayList<>();

        //when
        Main.main(args);
        final String sessionId = requestController.sendGetLogin(USER_ID);
        runMultiplePostRequestsForSameSession(updateBoardThreads, sessionId);

        //then
        checkResponseCodeOK(updateBoardThreads);
    }

    private void runMultiplePostRequestsForSameSession(final List<UpdateBoardThread> updateBoardThreads, final String sessionId) throws InterruptedException {
        createUpdateBoardThreads(updateBoardThreads, sessionId);
        executePostScoreInEachThread(updateBoardThreads);
        joinThreads(updateBoardThreads);
    }

    /**
     * Verifies that all threads ran successful requests
     *
     * @param updateBoardThreads the threads
     */
    private void checkResponseCodeOK(final List<UpdateBoardThread> updateBoardThreads) {
        for(final UpdateBoardThread thread : updateBoardThreads ){
            Assert.assertEquals(200, thread.responseCode);
        }
    }

    private void joinThreads(final List<UpdateBoardThread> updateBoardThreads) throws InterruptedException {
        for(final UpdateBoardThread thread : updateBoardThreads ){
            thread.join();
        }
    }

    private void executePostScoreInEachThread(final List<UpdateBoardThread> updateBoardThreads) {
        for(final UpdateBoardThread thread : updateBoardThreads ){
            thread.start();
        }
    }

    private void createUpdateBoardThreads(final List<UpdateBoardThread> updateBoardThreads, final String sessionId) {
        for(int i=0; i < MAX_THREAD_COUNT; i++ ){
            updateBoardThreads.add(new UpdateBoardThread(LEVEL_ID, sessionId));
        }
    }

    /**
     * Class that does a post score request for the provided session and stores the response code
     */
    private class UpdateBoardThread extends Thread {

        final int level;
        final String sessionId;
        int responseCode;

        UpdateBoardThread(final int level, final String sessionId) {
            this.level = level;
            this.sessionId = sessionId;
        }

        public void run(){
            try {
                final RequestController requestController = new RequestController();
                responseCode = requestController.sendPost(level, sessionId);

            } catch (Exception e) {
                System.out.println("Request Failed: " +  e.getMessage());
            }
        }
    }
}