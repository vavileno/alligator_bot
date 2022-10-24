package ru.mybots.alligator.processor;

public class GameStatus {
    private boolean gameActive;
    private String statusMsg;

    public GameStatus(boolean gameActive, String statusMsg) {
        this.gameActive = gameActive;
        this.statusMsg = statusMsg;
    }

    public boolean isGameActive() {
        return gameActive;
    }

    public void setGameActive(boolean gameActive) {
        this.gameActive = gameActive;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }
}
