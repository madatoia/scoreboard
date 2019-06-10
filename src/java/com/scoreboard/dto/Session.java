package com.scoreboard.dto;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.UUID;

/**
 * Session object containing the id, the time of its creation and the user to whom the session belongs
 */
public class Session {
    private static final int SESSION_TIME_IN_MIN = 10;

    private final String sessionId;
    private final Instant timestamp;
    private final Integer userId;

    /**
     * Creates a session based on the user id
     *
     * @param userId the user id. It has to be a positive integer, otherwise IllegalArgumentException is thrown.
     */
    public Session(final Integer userId) {
        if (userId < 0) {
            throw new IllegalArgumentException("Invalid user id");
        }
        sessionId = UUID.randomUUID().toString();
        timestamp = Calendar.getInstance().getTime().toInstant();
        this.userId = userId;
    }

    /**
     * Checks that the session is still valid
     *
     * @return false if the session has expired and false otherwise
     */
    public boolean hasNotExpiredYet() {
        return timestamp.plus(SESSION_TIME_IN_MIN, ChronoUnit.MINUTES).isAfter(Calendar.getInstance().toInstant());
    }

    public String getSessionId() {
        return sessionId;
    }

    public Integer getUserId() {
        return userId;
    }
}