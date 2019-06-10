package com.scoreboard.persistence;

import com.scoreboard.dto.ScoreEntry;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that keeps the score board. A user can retrieve the scoreboard for a given level and can post a score.
 */
public class ScoreKeeper {

    private static final Map<Integer, PriorityQueue<ScoreEntry>> scoreBoard = new HashMap<>();

    /**
     * Retrieves the high score board for the provided level.
     *
     * @param levelInStringFormat level id provided as string
     * @return the top 15 scores for the provided level, in csv format
     */
    public String getHighScoreBoardForLevel(final String levelInStringFormat) {
        final Integer level = parseLevelFromString(levelInStringFormat);

        return format(scoreBoard.getOrDefault(level, new PriorityQueue<>()));
    }

    /**
     * Updates the scoreboard for the provided level with the score and user received as parameters.
     * If the user had another score for the current level, the best one is being kept.
     * Also the top score list has only 15 entries. A new entry is being added only if the score of this
     * one is better than at least one of the existing scores.
     *
     * @param levelAsString  the level id in string format
     * @param userId         the user that has a new score
     * @param scoreInRequest the score as provided by the request
     */
    public void updateScoreBoard(final String levelAsString, final Integer userId, final InputStream scoreInRequest) {
        final Integer level = parseLevelFromString(levelAsString);
        final Integer score = readScore(scoreInRequest);
        initializeLevelIfNeeded(level);

        final PriorityQueue<ScoreEntry> scoreBoardForLevel = scoreBoard.get(level);

        addScoreForUser(userId, score, scoreBoardForLevel);
        resizeScoreBoard(scoreBoardForLevel);
    }

    private synchronized void initializeLevelIfNeeded(final Integer level) {
        scoreBoard.putIfAbsent(level, new PriorityQueue<>());
    }

    private synchronized void resizeScoreBoard(final PriorityQueue<ScoreEntry> scoreBoardForLevel) {
        if (scoreBoardForLevel.size() > 15) {
            scoreBoardForLevel.poll(); //remove the smallest
        }
    }

    private void addScoreForUser(final Integer userId, final Integer score, final PriorityQueue<ScoreEntry> scoreBoardForLevel) {
        scoreBoardForLevel.add(new ScoreEntry(userId, score));
    }

    private int readScore(final InputStream requestBody) {
        final Scanner bodyReader = new Scanner(requestBody);
        return bodyReader.nextInt();
    }

    /**
     * Sorts the provided scoreEntries in descending order based on the score and then prints the list in a string with elements separated by ","
     *
     * @param scoreEntries the score entries
     * @return the joined list
     */
    private String format(final PriorityQueue<ScoreEntry> scoreEntries) {
        final List<ScoreEntry> entryList = new ArrayList<>(scoreEntries);
        entryList.sort(Collections.reverseOrder());

        return entryList.stream().map(ScoreEntry::toString).collect(Collectors.joining(","));
    }

    /**
     * Parses the provided string into a positive integer.
     *
     * @param levelInStringFormat the level id in string format
     * @return the resulting integer
     * @throws IllegalArgumentException if the provided level id is a negative number
     */
    private Integer parseLevelFromString(final String levelInStringFormat) {
        final Integer level = Integer.parseInt(levelInStringFormat);

        if (level < 0) {
            throw new IllegalArgumentException("Level should be positive");
        }
        return level;
    }
}

