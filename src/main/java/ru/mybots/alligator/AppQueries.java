package ru.mybots.alligator;

public enum AppQueries {

    ALL_GAMES("SELECT g.game_id, g.chat_id, g.lead_id, g.word_id, g.active, w.text FROM GAME g, WORD w WHERE w.id = g.word_id"),
    INSERT_GAME("INSERT INTO GAME(chat_id, lead_id, word_id, active) values(?,?,?,?)"),
    UPDATE_GAME("UPDATE GAME set chat_id = ?, lead_id = ?, word_id = ?, active = ? where game_id = ?"),
    NEXT_WORD( "SELECT id, text FROM WORD ORDER BY id DESC LIMIT 1"),
    CURRENT_GAME("SELECT game_id, chat_id, lead_id, word_id FROM GAME where chat_id = ? and ACTIVE = 1"),
    DELETE_GAME("DELETE FROM GAME WHERE game_id = ?");

    private String sql;

    AppQueries(String sql) {
        this.sql = sql;
    }

    public String sql() {
        return sql;
    }
}
