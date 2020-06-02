package ru.mybots.alligator.exception;

public enum AlligatorError {

    DB_FAILED_INSERT(1000, "Failed to insert record to DB"),
    DB_FAILED_NEXT_WORD(1001, "Failed to select next word" ),
    DB_FAILED_LOAD_GAME(1002, "Failed to load all games" ),
    DB_FAILED_GET_CURRENT_WORD(1003, "Failed to get current word"),
    DB_FAILED_GET_CURRENT_GAME(1004, "Failed to get current game"),
    DB_FAILED_UPDATE(1005, "Failed to update DB record");


    private final int code;
    private final String errmsg;

    AlligatorError(int code, String errmsg) {
        this.code = code;
        this.errmsg = errmsg;
    }

    public int code() {
        return code;
    }

    public String errmsg() {
        return errmsg;
    }

}
