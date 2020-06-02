package ru.mybots.alligator.exception;

public class AlligatorApplicationException extends  Exception {

    private AlligatorError error;

    public AlligatorApplicationException(AlligatorError error) {
        super(UserError.FAIL.errmsg());
        this.error = error;
    }

    public AlligatorError getError() {
        return error;
    }
}
