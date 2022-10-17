package ru.mybots.alligator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public static final Logger log = LoggerFactory.getLogger(Alligator.class);

    // Games cache
    // TODO always refresh DB copy when change
    private Map<Long, Game> games = new HashMap<>(100);

    //TODO reload from DB when start
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
            log.error(e.getMessage(), e);
        }
    }

    public Game start(Long chatId, Long leadId) throws AlligatorApplicationException {
        Game g = loadGame(chatId);
        if(g == null) {
            g = initGame(chatId, leadId);
        }
        return g;
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

    public boolean hasActiveGame(Long chatId) throws AlligatorApplicationException {
        Game g = loadGame(chatId);
        if(g == null) { return false; }
        return g.isGameActive();
    }


    public boolean wannaBeLeader(Long chatId, Long userId) throws AlligatorApplicationException {
        Game g = loadGame(chatId);
        if(g == null) {
            initGame(chatId, userId);
            g = games.get(chatId);
        }
        if(!g.isGameActive()) {
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

    public String showWord(Long chatId, Long userId) throws AlligatorApplicationException {
        Game g = loadGame(chatId);
        if(g != null && g.isGameActive()) {
            if(!g.getLeadId().equals(userId)) {
                return Game.NOT_LEADER;
            }
            return g.getWord() != null ? g.getWord().getText() : Game.PLEASE_RESTART;
        }
        return Game.NO_GAME;
    }

    public String nextWord(Long chatId, Long userId) throws AlligatorApplicationException {
        Game g = loadGame(chatId);
        if(g != null && g.isGameActive()) {
            if(!g.getLeadId().equals(userId)) {
                return Game.NOT_LEADER;
            }
            Word word = repo.nextWord(g);
            g.setWord(word);
            g.setLastOrd(word.getOrd());
            repo.updateGame(g);
            return word.getText();
        }
        return Game.NO_GAME;
    }

    public boolean tryWord(Long chatId, Long userId, String tryWord) throws AlligatorApplicationException {
        Game g = loadGame(chatId);
        if(g != null && !userId.equals(g.getLeadId())) {
            if(g.getWord().getText().equalsIgnoreCase(tryWord.trim())) {
                g.setActive(Game.INACTIVE);
                g.setWinnerId(userId);
                repo.updateGame(g);
                return true;
            }
        }
        return false;
    }

    public void end(Long chatId) throws AlligatorApplicationException {
        Game g = games.get(chatId);
        if(g != null && g.isGameActive()) {
            g.setActive(Game.INACTIVE);
            games.remove(chatId);
            chatSet.remove(chatId);
        }
    }

    public Set<Long> chatSet() {
        return chatSet;
    }

    private Game loadGame(Long chatId) throws AlligatorApplicationException {
        Game g = games.get(chatId);
        if(g == null) {
            g = repo.lastGame(chatId);
        }
        return g;
    }

    private Game initGame(Long chatId, Long leadId) throws AlligatorApplicationException {
//        long randomOrd = Math.round(Math.random()*10000);
        Date now = new Date();
        Game g = Game.createInstance(chatId, leadId, 1L, Game.ACTIVE, null, now, now);
        Word word = repo.nextWord(g);
        g.setWord(word);
        g.setLastOrd(word.getOrd());
        repo.insertGame(g);
        games.put(chatId, g);
        chatSet.add(chatId);
        return g;
    }

    private String nextWord(Game g) throws AlligatorApplicationException {
        Word word = repo.nextWord(g);
        if(word == null) {

        }
        return word.getText();
    }

}
