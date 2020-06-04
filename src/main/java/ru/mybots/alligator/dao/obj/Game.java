package ru.mybots.alligator.dao.obj;

public class Game {

    public static final int ACTIVE = 1;
    public static final int INACTIVE = 0;

    public static final String NOT_LEADER = "Ведущий не ты";
    public static final String NO_GAME = "Нет игры";
    public static final String PLEASE_RESTART = "Игра устарела. Запусти новую командой /startgame";
    public static final String ALREADY_ACTIVE = "Игра уже идёт";

    private Word word;

    private Long gameId;
    private Long leadId;
    private final Long chatId;
    private Long lastOrd;
    private int active;

    private Game(Long chatId) {
        this.chatId = chatId;
    }

    public static Game create(long chatId, Long leadId, Long lastOrd, Integer active, Word word) {
        Game g = new Game(chatId);
        g.setLeadId(leadId);
        g.setActive(active);
        g.setLastOrd(lastOrd);
        g.setWord(word);
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

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active == 1;
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
}
