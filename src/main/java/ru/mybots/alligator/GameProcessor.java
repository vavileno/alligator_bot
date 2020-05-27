package ru.mybots.alligator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class GameProcessor {

    private Map<Long, Game> games = new HashMap<>(100);

    @Autowired
    private AppRepository repo;

    @PostConstruct
    public void init() {
        games = repo.loadGames();
    }

    public void start(Long chatId, Long leadId) {
        Game g = games.get(chatId);
        if(g == null) {
            g = Game.create(chatId, leadId, null);
            games.put(chatId, g);
        }
        nextWord();
    }

    public void imLeader(Long chatId, Long leadId) {

    }

    public void nextWord() {

    }

    public void end(Long chatId) {
        Game g = games.get(chatId);
        if(g != null) {
            g.setLeadId(null);
        }
    }
}
