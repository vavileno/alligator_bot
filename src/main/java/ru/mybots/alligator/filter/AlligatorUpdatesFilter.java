package ru.mybots.alligator.filter;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mybots.alligator.service.GameService;

@Service
public class AlligatorUpdatesFilter implements UpdatesFilter {

    @Autowired
    private GameService gameService;

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
//        if(!gameService.chatSetWithGames().contains(m.chat().id())) {
//            if (m.text() == null || !m.text().startsWith("/")) {
//                return false;
//            }
//        }

        // Do not accept messages with spaces
        return m.text() == null || !m.text().trim().contains(" ");
    }
}
