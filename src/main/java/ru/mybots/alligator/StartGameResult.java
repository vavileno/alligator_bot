package ru.mybots.alligator;

public class StartGameResult {
    private boolean newStarted;
    private String resultMsg;

    public StartGameResult(boolean success, String resultMsg) {
        this.newStarted = success;
        this.resultMsg = resultMsg;
    }

    public boolean isNewStarted() {
        return newStarted;
    }

    public void setNewStarted(boolean newStarted) {
        this.newStarted = newStarted;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }
}
