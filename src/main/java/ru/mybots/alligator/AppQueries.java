package ru.mybots.alligator;

public enum AppQueries {

    ALL_GAMES("SELECT * FROM GAME");

    private String sql;

    AppQueries(String sql) {
        this.sql = sql;
    }

    public String sql() {
        return sql;
    }
}
