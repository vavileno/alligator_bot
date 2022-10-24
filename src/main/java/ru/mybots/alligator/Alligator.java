package ru.mybots.alligator;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mybots.alligator.dao.obj.Game;
import ru.mybots.alligator.exception.AlligatorApplicationException;
import ru.mybots.alligator.processor.GameStatus;
import ru.mybots.alligator.service.GameService;

/*
    Game processor
 */
@Service
public class Alligator {

    public static final Logger log = LoggerFactory.getLogger(Alligator.class);

    @Autowired
    private GameService gameService;

    public StartGameResult startGame(Chat chat, User lead) throws AlligatorApplicationException {
        Game game = gameService.loadGame(chat.id());
        if(game == null) {
            game = gameService.initGame(chat, lead);
            return new StartGameResult(true, String.format(StringConstants.SOMEBODY_PROPOSES_A_WORD, game.getLeadTitle()));
        }
        else {
            return new StartGameResult(false, Game.Messages.ALREADY_ACTIVE + "\n"
                    + String.format(StringConstants.SOMEBODY_PROPOSES_A_WORD, game.getLeadTitle()));
        }
    }

    public GameStatus statusMessage(Long chatId) throws AlligatorApplicationException {
        Game game = gameService.loadGame(chatId);
        if(game == null || !game.isGameActive()) {
            return new GameStatus(false, StringConstants.NO_GAME);
        }
        else {
            //TODO: game lasts for X minutes
            //TODO: game was timed out
            return new GameStatus(true, Game.Messages.ALREADY_ACTIVE + "\n"
                    + String.format(StringConstants.SOMEBODY_PROPOSES_A_WORD, game.getLeadTitle()));
        }
    }

    public boolean hasActiveGame(Long chatId) throws AlligatorApplicationException {
        Game game = gameService.loadGame(chatId);
        if(game == null) { return false; }
        return game.isGameActive();
    }

    public boolean wannaBeLeader(Chat chat, User lead) throws AlligatorApplicationException {
        Game game = gameService.loadGame(chat.id());
        if(game == null || !game.isGameActive()) {
            gameService.initGame(chat, lead);
            return true;
        }
        return false;
    }

    public String showWord(Long chatId, Long userId) throws AlligatorApplicationException {
        Game game = gameService.loadGame(chatId);
        if(game != null && game.isGameActive()) {
            if(!game.getLeadId().equals(userId)) {
                return Game.Messages.NOT_LEADER;
            }
            return game.getWord() != null ? game.getWord().getText() : Game.Messages.PLEASE_RESTART;
        }
        return Game.Messages.NO_GAME;
    }

    public String nextWord(Chat chat, Long userId) throws AlligatorApplicationException {
        Game game = gameService.loadGame(chat.id());
        if(game != null && game.isGameActive()) {
            if(!game.getLeadId().equals(userId)) {
                return Game.Messages.NOT_LEADER;
            }
            return gameService.nextWord(chat, game);
        }
        return Game.Messages.NO_GAME;
    }

    public boolean tryWord(Chat chat, User user, String tryWord) throws AlligatorApplicationException {
        Game game = gameService.loadGame(chat.id());
        if(game != null && !user.id().equals(game.getLeadId())) {
            if(game.getWord().getText().equalsIgnoreCase(tryWord.trim())) {
                gameService.finish(chat, game, user);
                return true;
            }
        }
        return false;
    }

    public String getUserTitle(User user) {
        return StringUtils.defaultString(user.firstName()) + " " + StringUtils.defaultString(user.lastName());
    }

    public String somebodyProposeWord(User user) {
        return String.format( StringConstants.SOMEBODY_PROPOSES_A_WORD, getUserTitle(user) );
    }

}


//        if(g == null) {
//            g = repo.lastGame(chatId, leadId);
//
//            // This chat plays for the first time
//            if(g == null) {
//
//            }
//            // This chat already played before and creates new game
//            else {
//                Word word = repo.nextWord(g);
//                g = Game.create(chatId, leadId, word.getOrd(), Game.ACTIVE, word);
//                games.put(chatId, g);
//                repo.updateGame(g);
//                chatSet.add(chatId);
//                return word.getText();
//            }
//
//        }
//        else {
//            return g.getWord().getText();
//        }