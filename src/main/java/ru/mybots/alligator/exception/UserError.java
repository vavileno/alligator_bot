package ru.mybots.alligator.exception;

public enum UserError {

    FAIL("Something's gone wrong");


    private final String errmsg;

    UserError(String errmsg) {
        this.errmsg = errmsg;
    }

    public String errmsg() {
        return errmsg;
    }
}
