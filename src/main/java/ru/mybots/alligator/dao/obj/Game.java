package ru.mybots.alligator.dao.obj;

import java.util.Date;

public class Game {

    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;

    public static final long GAME_TIMEOUT_MINUTES = 30;

    public static class Messages {
        public static final String NOT_LEADER = "Ведущий не ты";
        public static final String NO_GAME = "Нет игры";
        public static final String PLEASE_RESTART = "Игра устарела. Запусти новую командой /startgame";
        public static final String ALREADY_ACTIVE = "Игра уже идёт";
        public static final String EMPTY = "";
    }

    private Word word;

    private Long gameId;
    private Long leadId;
    private String leadTitle;
    private final Long chatId;
    private Long lastOrd;
    private boolean active;
    private Date startDate;
    private Date lastMoveDate;
    private Long winnerId;
    private String winnerTitle;

    private Game(Long chatId) {
        this.chatId = chatId;
    }

    public static Game createInstance(Long gameId, long chatId, Long leadId, String leadTitle, Long lastOrd, boolean active,
                                      Word word, Date startDate, Date lastMoveDate, Long winnerId, String winnerTitle) {
        Game game = new Game(chatId);
        game.setGameId(gameId);
        game.setLeadId(leadId);
        game.setLeadTitle(leadTitle);
        game.setActive(active);
        game.setLastOrd(lastOrd);
        game.setWord(word);
        game.setStartDate(startDate);
        game.setLastMoveDate(lastMoveDate);
        game.setWinnerId(winnerId);
        game.setWinnerTitle(winnerTitle);
        return game;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public Long getLeadId() {
        return leadId;
    }

    public void setLeadId(Long leadId) {
        this.leadId = leadId;
    }

    public Long getChatId() {
        return chatId;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isGameActive() {
        long timeSinceLastMove = new Date().getTime() - getLastMoveDate().getTime();
        return this.active && timeSinceLastMove < Game.GAME_TIMEOUT_MINUTES * 60 * 1000;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getLastOrd() {
        return lastOrd;
    }

    public void setLastOrd(Long lastOrd) {
        this.lastOrd = lastOrd;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getLastMoveDate() {
        return lastMoveDate;
    }

    public void setLastMoveDate(Date lastMoveDate) {
        this.lastMoveDate = lastMoveDate;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    public String getLeadTitle() {
        return leadTitle;
    }

    public void setLeadTitle(String leadTitle) {
        this.leadTitle = leadTitle;
    }

    public String getWinnerTitle() {
        return winnerTitle;
    }

    public void setWinnerTitle(String winnerTitle) {
        this.winnerTitle = winnerTitle;
    }
}
