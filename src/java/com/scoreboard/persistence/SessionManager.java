package com.scoreboard.persistence;

import com.scoreboard.dto.Session;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class containing the users and their sessions. Via this class is possible to obtain a session id for a user and get
 * the user id for a specific sessionId.
 */
public class SessionManager {

    static final LinkedBlockingQueue<Session> sessionQueue = new LinkedBlockingQueue<>();
    static final Map<Integer, Session> sessionPerUser = new ConcurrentHashMap<>();

    public String getSessionIdForUser(final String userIdAsString)throws InterruptedException {
        final Integer userId = Integer.parseInt(userIdAsString);
        cleanUpExpiredSessions();

        final Session newSession = new Session(userId);
        synchronized (this){
            if (!sessionPerUser.containsKey(userId)) {
                sessionPerUser.put(userId, newSession);
                sessionQueue.put(newSession);
            }
        }
        return sessionPerUser.get(userId).getSessionId();
    }

    public int getUserForSession(final String sessionId) {
        cleanUpExpiredSessions();

        for (Map.Entry<Integer, Session> session : sessionPerUser.entrySet()){
            if(sessionId.equals(session.getValue().getSessionId())){
                return session.getKey();
            }
        }
        throw new IllegalArgumentException("Invalid sessionId");
    }

    private void cleanUpExpiredSessions() {
        while (!sessionQueue.isEmpty()) {
            final Session peek = sessionQueue.peek();
            //since the session queue is sorted it's enough to remove all sessions until we find one younger than SESSION_TIME_IN_MIN
            if (peek.hasNotExpiredYet()) {
                return;
            }
            sessionPerUser.remove(peek.getUserId());
            sessionQueue.poll();
        }
    }
}
