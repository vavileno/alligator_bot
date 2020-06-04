package ru.mybots.alligator.dao.obj;

public class Word {

    private Long id;

    private String text;

    private Long ord;

    public Word(Long id, String text, Long ord) {
        this.id = id;
        this.text = text;
        this.ord = ord;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getOrd() {
        return ord;
    }

    public void setOrd(Long ord) {
        this.ord = ord;
    }
}
