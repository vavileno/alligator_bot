package ru.mybots.alligator.processor;

public class ProcessResult {

    public static final int MESSAGE = 1;
    public static final int ANSWER_CALLBACK_QUERY = 2;

    private int resultType;

    private Object content;

    public ProcessResult(int resultType, Object content) {
        this.resultType = resultType;
        this.content = content;
    }

    public int getResultType() {
        return resultType;
    }

    public void setResultType(int resultType) {
        this.resultType = resultType;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
