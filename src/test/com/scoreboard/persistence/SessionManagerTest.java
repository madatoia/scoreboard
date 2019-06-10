package com.scoreboard.persistence;

import com.scoreboard.dto.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SessionManagerTest {

    private final SessionManager sessionManagerTestee = new SessionManager();

    @Before
    public void cleanSessions(){
        sessionManagerTestee.sessionQueue.clear();
        sessionManagerTestee.sessionPerUser.clear();
    }

    @Test
    public void getSessionIdForNewUser() throws InterruptedException {
        //given no sessions created before

        //when
        final String sessionId = sessionManagerTestee.getSessionIdForUser("1");

        //then
        Assert.assertNotNull(sessionId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSessionIdForNewUserWithNegativeId() throws InterruptedException {
        //when
        sessionManagerTestee.getSessionIdForUser("-1");

        //then exception is being thrown
    }

    @Test
    public void getSessionIdForExistingUser() throws InterruptedException {
        //given no sessions created before
        final String initialSessionId = sessionManagerTestee.getSessionIdForUser("1");

        //when
        final String newSessionId = sessionManagerTestee.getSessionIdForUser("1");

        //then
        Assert.assertEquals(initialSessionId, newSessionId);
    }

    @Test
    public void getUserForSessionWithExistingUser() throws InterruptedException {
        //given
        final String sessionId = sessionManagerTestee.getSessionIdForUser("1");

        //when
        int userId = sessionManagerTestee.getUserForSession(sessionId);

        Assert.assertEquals(1, userId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUserForSessionWithInvalidSessionId() {
        //given no active sessions

        //when
        sessionManagerTestee.getUserForSession("fakeSessionId");

        //then exception is being thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUserForExpiredSession() throws InterruptedException {
        //given an old session
        final Session mockedSession = Mockito.mock(Session.class);
        Mockito.when(mockedSession.hasNotExpiredYet()).thenReturn(false);
        Mockito.when(mockedSession.getSessionId()).thenReturn("mockId20");
        Mockito.when(mockedSession.getUserId()).thenReturn(20);
        SessionManager.sessionPerUser.put(20, mockedSession);
        SessionManager.sessionQueue.put(mockedSession);

        //when
        int id = sessionManagerTestee.getUserForSession("mockId20");
        System.out.println(id);
        //then exception is being thrown
    }

    @Test
    public void getSessionForUserWithAnotherExpiredSession() throws InterruptedException {
        //given an old session
        final Session mockedSession = Mockito.mock(Session.class);
        Mockito.when(mockedSession.hasNotExpiredYet()).thenReturn(false);
        Mockito.when(mockedSession.getSessionId()).thenReturn("mockId21");
        Mockito.when(mockedSession.getUserId()).thenReturn(21);
        SessionManager.sessionPerUser.put(21, mockedSession);
        SessionManager.sessionQueue.put(mockedSession);

        //when
        final String newSessionId = sessionManagerTestee.getSessionIdForUser("21");

        //then
        Assert.assertNotEquals("mockedId21", newSessionId);
    }
}