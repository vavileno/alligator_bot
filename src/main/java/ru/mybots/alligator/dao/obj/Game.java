package ru.mybots.alligator.dao.obj;

import java.util.Date;

public class Game {

    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;

    public static final long GAME_TIMEOUT_MINUTES = 30;

    public static final String NOT_LEADER = "Ведущий не ты";
    public static final String NO_GAME = "Нет игры";
    public static final String PLEASE_RESTART = "Игра устарела. Запусти новую командой /startgame";
    public static final String ALREADY_ACTIVE = "Игра уже идёт";

    private Word word;

    private Long gameId;
    private Long leadId;
    private final Long chatId;
    private Long lastOrd;
    private boolean active;
    private Date startDate;
    private Date lastMoveDate;

    private Game(Long chatId) {
        this.chatId = chatId;
    }

    public static Game createInstance(long chatId, Long leadId, Long lastOrd, boolean active, Word word, Date startDate, Date lastMoveDate) {
        Game g = new Game(chatId);
        g.setLeadId(leadId);
        g.setActive(active);
        g.setLastOrd(lastOrd);
        g.setWord(word);
        g.setStartDate(startDate);
        g.setLastMoveDate(lastMoveDate);
        return g;
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
        return this.active;
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
}
