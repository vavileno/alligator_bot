package ru.mybots.alligator.dao;

public enum AppQueries {

    ALL_GAMES("SELECT g.game_id, g.chat_id, g.lead_id, g.last_ord, g.active, w.word_id, w.word_text, w.word_ord, g.start_date, g.last_move_date FROM GAME g, WORD w WHERE w.word_ord = g.last_ord"),

    INSERT_GAME("INSERT INTO GAME(chat_id, lead_id, last_ord, active, start_date, last_move_date) values(?,?,?,?,?,?)"),

    UPDATE_GAME("UPDATE GAME set chat_id = ?, lead_id = ?, last_ord = ?, active = ? where game_id = ?"),

    NEXT_WORD( "SELECT word_id, word_text, word_ord FROM WORD WHERE word_ord > ? ORDER BY word_ord LIMIT 1"),

    LAST_GAME("SELECT g.game_id, g.chat_id, g.lead_id, g.last_ord, w.word_id, w.word_text FROM GAME g, WORD w where w.word_ord = g.last_ord and chat_id = ?"),

    DELETE_GAME("DELETE FROM GAME WHERE game_id = ?");

    private String sql;

    AppQueries(String sql) {
        this.sql = sql;
    }

    public String sql() {
        return sql;
    }
}
