package ru.mybots.alligator.service;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mybots.alligator.dao.AppRepository;
import ru.mybots.alligator.dao.obj.Game;
import ru.mybots.alligator.dao.obj.Word;
import ru.mybots.alligator.exception.AlligatorApplicationException;

import java.util.Date;

@Service
public class GameService {

    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private AppRepository repo;

    public Game initGame(Chat chat, User lead) throws AlligatorApplicationException {
//        long randomOrd = Math.round(Math.random()*10000);
        Date now = new Date();
        String userTitle = StringUtils.defaultString(lead.firstName()) + " " + StringUtils.defaultString(lead.lastName());
        Game game = Game.createInstance(null, chat.id(), lead.id(), userTitle, 1L,
                                            Game.ACTIVE, null, now, now, null, null);
        Word word = repo.nextWord(game);
        game.setWord(word);
        game.setLastOrd(word.getId());
        repo.insertGame(chat, game);
        return game;
    }

    public Game loadGame(Long chatId) throws AlligatorApplicationException {
        return repo.lastGame(chatId);
    }

    public String nextWord(Chat chat, Game game) throws AlligatorApplicationException {
        Word word = repo.nextWord(game);
        game.setWord(word);
        game.setLastOrd(word.getId());
        repo.updateGame(chat, game);
        return word.getText();
    }

    public void finish(Chat chat, Game game, User user) throws AlligatorApplicationException {
        String userTitle = StringUtils.defaultString(user.firstName()) + " " + StringUtils.defaultString(user.lastName());
        game.setActive(Game.INACTIVE);
        game.setWinnerId(user.id());
        game.setWinnerTitle(userTitle);
        repo.updateGame(chat, game);
    }
}
