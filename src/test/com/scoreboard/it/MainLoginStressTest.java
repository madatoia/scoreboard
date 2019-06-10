package com.scoreboard.it;

import com.scoreboard.RequestController;
import com.scoreboard.app.Main;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks that 1000 login requests can be handled in parallel
 */
public class MainLoginStressTest {

    private static final int MAX_THREAD_COUNT = 1000;
    private static final String USER_ID = "1234";

    private final RequestController requestController = new RequestController();

    @Test
    public void testLoginOnMultipleThreads() throws Exception {
        //given
        final String[] args = new String[0];
        final List<LoginThread> loginThreads = new ArrayList<>();

        //when
        Main.main(args);
        runMultipleLoginRequestsForSameUser(loginThreads, USER_ID);

        //then
        checkAllThreadsReturnedSameSessionId(loginThreads);
    }


    private void runMultipleLoginRequestsForSameUser(final List<LoginThread> loginThreads, final String userId) throws InterruptedException {
        createLoginThreads(loginThreads, userId);
        executeLoginRequestFromMultipleThreads(loginThreads);
        joinLoginThreads(loginThreads);
    }

    private void checkAllThreadsReturnedSameSessionId(final List<LoginThread> loginThreads) {
        for (final LoginThread thread : loginThreads) {
            Assert.assertEquals(loginThreads.get(0).sessionId, thread.sessionId);
        }
    }

    private void joinLoginThreads(final List<LoginThread> loginThreads) throws InterruptedException {
        for (final LoginThread thread : loginThreads) {
            thread.join();
        }
    }

    private void executeLoginRequestFromMultipleThreads(final List<LoginThread> loginThreads) {
        for (final LoginThread thread : loginThreads) {
            thread.start();
        }
    }

    private void createLoginThreads(final List<LoginThread> loginThreads, final String userId) {
        for (int i = 0; i < MAX_THREAD_COUNT; i++) {
            loginThreads.add(new LoginThread(userId));
        }
    }

    /**
     * Class that does a login request for the provided user and stores the sessionId
     */
    private class LoginThread extends Thread {

        final String userId;
        String sessionId;

        LoginThread(final String userId) {
            this.userId = userId;
        }

        public void run() {
            try {
                sessionId = requestController.sendGetLogin(userId);
            } catch (Exception e) {
                System.out.println("Request Failed: " + e.getMessage());
            }
        }
    }
}