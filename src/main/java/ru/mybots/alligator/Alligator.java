package ru.mybots.alligator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mybots.alligator.dao.AppRepository;
import ru.mybots.alligator.exception.AlligatorApplicationException;
import ru.mybots.alligator.dao.obj.Game;
import ru.mybots.alligator.dao.obj.Word;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
    Game processor
 */

@Service
public class Alligator {

    // Games cache
    private Map<Long, Game> games = new HashMap<>(100);

    private final Set<Long> chatSet = new HashSet<>();

    @Autowired
    private AppRepository repo;

    @PostConstruct
    public void init() {
        try {
            games = repo.loadGames();
            games.forEach((k,v) -> {
                chatSet.add(k);
            });
        } catch (AlligatorApplicationException e) {
            e.printStackTrace();
        }
    }

    public String start(Long chatId, Long leadId) throws AlligatorApplicationException {
//        Game g = games.get(chatId);

        return initGame(chatId, leadId);
        //TODO: в каком случае допустим запуск новой игры
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
    }

    public boolean gameActive(Long chatId) {
        Game g = games.get(chatId);
        if(g == null) { return false; }
        long timeSinceLastMove = new Date().getTime() - g.getLastMoveDate().getTime();
        return g.isActive() && timeSinceLastMove < Game.GAME_TIMEOUT_MINUTES * 60 * 1000;
    }

    private String initGame(Long chatId, Long leadId) throws AlligatorApplicationException {
//        long randomOrd = Math.round(Math.random()*10000);
        Date now = new Date();
        Game g = Game.createInstance(chatId, leadId, 1L, Game.ACTIVE, null, now, now);
        Word word = repo.nextWord(g);
        g.setWord(word);
        g.setLastOrd(word.getOrd());
        repo.insertGame(g);
        games.put(chatId, g);
        chatSet.add(chatId);
        return word.getText();
    }

    public boolean wannaBeLeader(Long chatId, Long userId) throws AlligatorApplicationException {
        Game g = games.get(chatId);
        if(g == null) {
            g = repo.lastGame(chatId);
            if(g == null) {
                initGame(chatId, userId);
                g = games.get(chatId);
            }
        }
        // TODO: What if last game from db but not existing in games cache is already active
        if(!g.isActive()) {
            Word word = repo.nextWord(g);
            g.setActive(Game.ACTIVE);
            g.setLeadId(userId);
            g.setLastOrd(word.getOrd());
            g.setWord(word);
            repo.updateGame(g);
            return true;
        }
        return false;
    }

    public String showWord(Long chatId, Long userId) {
        Game g = games.get(chatId);
        if(g != null && g.isActive()) {
            if(!g.getLeadId().equals(userId)) {
                return Game.NOT_LEADER;
            }
            return g.getWord() != null ? g.getWord().getText() : Game.PLEASE_RESTART;
        }
        return Game.NO_GAME;
    }

    public String nextWord(Long chatId, Long userId) throws AlligatorApplicationException {
        Game g = games.get(chatId);
        if(g != null && g.isActive()) {
            if(!g.getLeadId().equals(userId)) {
                return Game.NOT_LEADER;
            }
            Word word = repo.nextWord(g);
            g.setWord(word);
            repo.updateGame(g);
            return word.getText();
        }
        return Game.NO_GAME;
    }

    public boolean tryWord(Long chatId, Long userId, String tryWord) throws AlligatorApplicationException {
        Game g = games.get(chatId);
        if(g != null && g.isActive() && !userId.equals(g.getLeadId())) {
            if(g.getWord().getText().equalsIgnoreCase(tryWord.trim())) {
                g.setActive(Game.INACTIVE);
                g.setLeadId(null);
                repo.updateGame(g);
                return true;
            }
        }
        return false;
    }

    public void end(Long chatId) throws AlligatorApplicationException {
        Game g = games.get(chatId);
        if(g != null && g.isActive()) {
            g.setActive(Game.INACTIVE);
            games.remove(chatId);
            chatSet.remove(chatId);
        }
    }



    public Set<Long> chatSet() {
        return chatSet;
    }


}
