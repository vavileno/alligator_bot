package ru.mybots.alligator;

public enum AppQueries {

    ALL_GAMES("SELECT * FROM GAME"),
    INSERT_GAME("INSERT INTO GAME(chat_id, lead_id, active) values(?,?,?)"),
    UPDATE_GAME("UPDATE GAME set chat_id = ?, lead_id = ?, word_id = ?, active = ? where game_id = ?"),
    NEXT_WORD( "SELECT id, text FROM WORD ORDER BY id DESC LIMIT 1"),
    CURRENT_GAME("select game_id, chat_id, lead_id, word_id FROM GAME where chat_id = ? and ACTIVE = 1");

    private String sql;

    AppQueries(String sql) {
        this.sql = sql;
    }

    public String sql() {
        return sql;
    }
}
