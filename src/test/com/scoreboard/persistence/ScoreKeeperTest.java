package com.scoreboard.persistence;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class ScoreKeeperTest {

    private ScoreKeeper scoreKeeperTestee;

    @Test
    public void updateScoreBoardAndCheckIfSmallestElementIsRemoved() throws InterruptedException {

        //given
        scoreKeeperTestee = new ScoreKeeper();
        scoreKeeperTestee.updateScoreBoard("1", 1, IOUtils.toInputStream("5"));
        scoreKeeperTestee.updateScoreBoard("1", 2, IOUtils.toInputStream("6"));
        scoreKeeperTestee.updateScoreBoard("1", 3, IOUtils.toInputStream("4"));
        scoreKeeperTestee.updateScoreBoard("1", 4, IOUtils.toInputStream("4"));
        scoreKeeperTestee.updateScoreBoard("1", 5, IOUtils.toInputStream("7"));
        scoreKeeperTestee.updateScoreBoard("1", 6, IOUtils.toInputStream("7"));
        scoreKeeperTestee.updateScoreBoard("1", 7, IOUtils.toInputStream("7"));
        scoreKeeperTestee.updateScoreBoard("1", 8, IOUtils.toInputStream("7"));
        scoreKeeperTestee.updateScoreBoard("1", 9, IOUtils.toInputStream("7"));
        scoreKeeperTestee.updateScoreBoard("1", 10, IOUtils.toInputStream("7"));
        scoreKeeperTestee.updateScoreBoard("1", 11, IOUtils.toInputStream("7"));
        scoreKeeperTestee.updateScoreBoard("1", 12, IOUtils.toInputStream("7"));
        scoreKeeperTestee.updateScoreBoard("1", 13, IOUtils.toInputStream("7"));
        scoreKeeperTestee.updateScoreBoard("1", 14, IOUtils.toInputStream("7"));
        scoreKeeperTestee.updateScoreBoard("1", 15, IOUtils.toInputStream("7"));
        scoreKeeperTestee.updateScoreBoard("1", 16, IOUtils.toInputStream("10"));

        //when
        String scoreboard = scoreKeeperTestee.getHighScoreBoardForLevel("1");

        //then only the top 15 elements are reported (userId=3, score=4 not present)
        Assert.assertEquals("16=10,15=7,14=7,13=7,12=7,11=7,10=7,9=7,8=7,7=7,6=7,5=7,2=6,1=5,4=4", scoreboard);
    }

    @Test
    public void checkIfMultipleScoresCanBeAddedForAUser() throws InterruptedException {

        //given
        scoreKeeperTestee = new ScoreKeeper();
        scoreKeeperTestee.updateScoreBoard("3", 1, IOUtils.toInputStream("5"));
        scoreKeeperTestee.updateScoreBoard("3", 1, IOUtils.toInputStream("10"));
        //when
        String scoreboard = scoreKeeperTestee.getHighScoreBoardForLevel("3");

        //then the first score is reported
        Assert.assertEquals("1=10,1=5", scoreboard);
    }

    @Test
    public void getScoreboardWhenNoEntries() {
        //given empty scoreboard
        scoreKeeperTestee = new ScoreKeeper();

        //when
        String scoreboard = scoreKeeperTestee.getHighScoreBoardForLevel("4");

        //then the first score is reported
        Assert.assertEquals("", scoreboard);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateScoreBoardWithNegativeLevel() {
        //given
        scoreKeeperTestee = new ScoreKeeper();
        //when
        scoreKeeperTestee.updateScoreBoard("-1", 1, IOUtils.toInputStream("5"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getScoreboardForLevelWithNegativeLevel() {
        //given
        scoreKeeperTestee = new ScoreKeeper();

        // when
        scoreKeeperTestee.getHighScoreBoardForLevel("-1");
    }

    @Test(expected = NumberFormatException.class)
    public void getScoreboardForLevelWithInvalidNumber() {
        //given
        scoreKeeperTestee = new ScoreKeeper();

        //when
        scoreKeeperTestee.getHighScoreBoardForLevel("abc");
    }
}