package ru.mybots.alligator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mybots.alligator.exception.AlligatorApplicationException;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class GameProcessor {

    private Map<Long, Game> games = new HashMap<>(100);

    private Set<Long> chatSet = new HashSet<>();

    @Autowired
    private AppRepository repo;

    @PostConstruct
    public void init() {
        try {
            games = repo.loadGames();
        } catch (AlligatorApplicationException e) {
            e.printStackTrace();
        }
    }

    public void start(Long chatId, Long leadId) throws AlligatorApplicationException {
        Game g = games.get(chatId);

        //TODO: в каком случае допустим запуск новой игры
        if(g == null) {
            Word word = repo.nextWord(g);
            g = Game.create(chatId, leadId, word);
            games.put(chatId, g);
            repo.insertGame(g);

            chatSet.add(chatId);
        }
    }

    public boolean tryWord(Long chatId, Long userId, String tryWord) throws AlligatorApplicationException {
        Game g = games.get(chatId);
        if(g != null && g.isActive()) {
            if(g.getWord().getText().equalsIgnoreCase(tryWord.trim())) {
                g.setWord(null);
                g.setLeadId(null);
                g.setActive(0);
                repo.updateGame(g);
                return true;
            }
        }
        return false;
    }

    public boolean wannaBeLeader(Long chatId, Long leadId) throws AlligatorApplicationException {
        Game g = games.get(chatId);
        if(g != null && g.isActive()) {
            if(g.getLeadId() == null) {
                Word word = repo.nextWord(g);
                g.setLeadId(leadId);
                g.setWord(word);
                repo.updateGame(g);
                return true;
            }
        }
        return false;
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
        }
        return Game.NO_GAME;
    }

    public void end(Long chatId) throws AlligatorApplicationException {
        Game g = games.get(chatId);
        if(g != null && g.isActive()) {
            g.setActive(0);
            games.remove(chatId);
        }
        repo.updateGame(g);
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

    public Set<Long> chatSet() {
        return chatSet;
    }
}
