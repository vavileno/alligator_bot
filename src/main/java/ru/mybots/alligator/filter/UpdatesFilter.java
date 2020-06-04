package ru.mybots.alligator.filter;

import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Service;

@Service
public interface UpdatesFilter {
    boolean filter(Update update);
}
