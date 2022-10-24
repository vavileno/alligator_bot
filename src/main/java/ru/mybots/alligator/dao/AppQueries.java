package ru.mybots.alligator.dao;

public enum AppQueries {

    ALL_GAMES("SELECT g.game_id, g.chat_id, g.lead_id, g.lead_title, g.last_ord, g.active, w.word_id, w.word_text, " +
            "g.start_date, g.last_move_date, g.winner_id, g.winner_title " +
            "FROM GAME g, WORD w " +
            "WHERE w.word_id = g.last_ord and g.active = true"),

    INSERT_GAME("INSERT INTO GAME(chat_id, lead_id, lead_title, last_ord, active, start_date, last_move_date) values(?,?,?,?,?,?,?)"),

    UPDATE_GAME("UPDATE GAME set chat_id = ?, lead_id = ?, lead_title = ?, last_ord = ?, active = ?, winner_id = ?, winner_title = ? " +
            "WHERE game_id = ?"),

    NEXT_WORD( "SELECT word_id, word_text FROM WORD WHERE word_id > ? ORDER BY word_id LIMIT 1"),

    LAST_GAME("SELECT g.game_id, g.chat_id, g.lead_id, g.lead_title, g.active, g.last_ord, g.start_date, g.last_move_date, " +
            "g.winner_id, g.winner_title, " +
            "w.word_id, w.word_text " +
            "FROM GAME g, WORD w " +
            "WHERE w.word_id = g.last_ord and chat_id = ?"),

    DELETE_GAME("DELETE FROM GAME WHERE game_id = ?");

    private String sql;

    AppQueries(String sql) {
        this.sql = sql;
    }

    public String sql() {
        return sql;
    }
}
