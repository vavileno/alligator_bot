package ru.mybots.alligator.filter;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mybots.alligator.Alligator;

@Service
public class AlligatorUpdatesFilter implements UpdatesFilter {

    @Autowired
    private Alligator alligator;

    @Override
    public boolean filter(Update update) {
        Message m = update.callbackQuery() == null ? update.message() : update.callbackQuery().message();

        // Accept only text and inline queries
        if(update.callbackQuery() != null) {
            return true;
        }
        else {
            if(m == null) {
                return false;
            }
        }

        // Accept commands only from chats without current games
        if(!alligator.chatSet().contains(m.chat().id())) {
            if (m.text() == null || !m.text().startsWith("/")) {
                return false;
            }
        }

        if(m.text() != null && m.text().trim().contains(" ")) {
            return false;
        }

        return true;
    }
}
