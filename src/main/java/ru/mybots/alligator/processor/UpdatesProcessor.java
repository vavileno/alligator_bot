package ru.mybots.alligator.processor;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;

public interface UpdatesProcessor {
    ProcessResult process(Update update);
}
