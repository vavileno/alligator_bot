package ru.mybots.alligator.dao;

public enum AppQueries {

    ALL_GAMES("SELECT g.game_id, g.chat_id, g.lead_id, g.last_ord, g.active, w.id, w.text FROM GAME g, WORD w WHERE w.ord = g.last_ord"),

    INSERT_GAME("INSERT INTO GAME(chat_id, lead_id, last_ord, active) values(?,?,?,?)"),

    UPDATE_GAME("UPDATE GAME set chat_id = ?, lead_id = ?, last_ord = ?, active = ? where game_id = ?"),

    NEXT_WORD( "SELECT id, text FROM WORD WHERE ord > ? ORDER BY ord LIMIT 1"),

    LAST_GAME("SELECT g.game_id, g.chat_id, g.lead_id, g.last_ord, w.id, w.text FROM GAME g, WORD w where w.ord = g.last_ord and chat_id = ?"),

    DELETE_GAME("DELETE FROM GAME WHERE game_id = ?");

    private String sql;

    AppQueries(String sql) {
        this.sql = sql;
    }

    public String sql() {
        return sql;
    }
}
