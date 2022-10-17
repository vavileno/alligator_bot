package ru.mybots.alligator.processor;

import com.pengrad.telegrambot.model.Update;

public interface UpdatesProcessor {
    ProcessResult process(Update update);
}
