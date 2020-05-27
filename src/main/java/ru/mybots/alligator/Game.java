package ru.mybots.alligator;

public class Game {

    private Long wordId;
    private Long leadId;
    private final Long chatId;

    private Game(Long chatId) {
        this.chatId = chatId;
    }

    public static Game create(long chatId, Long leadId, Long wordId) {
        Game g = new Game(chatId);
        g.setLeadId(leadId);
        g.setWordId(wordId);
        return g;
    }

    public String nextWord() {
        return "";
    }

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
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



    public String tryWord(String w) {
        return "";
    }

}
