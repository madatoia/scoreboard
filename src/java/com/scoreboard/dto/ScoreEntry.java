package com.scoreboard.dto;

/**
 * POJO containing a user and its highest score
 */
public class ScoreEntry implements Comparable {

    private Integer score;
    private Integer userId;

    public ScoreEntry(final Integer userId, final Integer score) {
        this.score = score;
        this.userId = userId;
    }

    /**
     * Entities are compared based on score first and then by user id
     */
    @Override
    public int compareTo(final Object o) {
        if (o instanceof ScoreEntry) {
            if (score.equals(((ScoreEntry) o).score)) {
                return userId.compareTo(((ScoreEntry) o).userId);
            }
            return score.compareTo(((ScoreEntry) o).score);
        }
        return 0;
    }

    public String toString() {
        return userId + "=" + score;
    }

}
