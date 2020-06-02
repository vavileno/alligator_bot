package ru.mybots.alligator;

public class Game {

    public static final int ACTIVE = 1;
    public static final int DELETED = 0;

    public static String NOT_LEADER = "Ведущий не ты";
    public static String NO_GAME = "Запусти игру командой /startgame";
    public static String PLEASE_RESTART = "Игра устарела. Запусти новую командой /startgame";

    private Word word;

    private Long gameId;
    private Long leadId;
    private final Long chatId;
    private int active;

    private Game(Long chatId) {
        this.chatId = chatId;
    }

    public static Game create(long chatId, Long leadId, Word word) {
        Game g = new Game(chatId);
        g.setLeadId(leadId);
        g.setActive(1);
        g.setWord(word);
        return g;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public String nextWord() {
        return "";
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

}
