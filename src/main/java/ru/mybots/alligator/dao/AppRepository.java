package ru.mybots.alligator.dao;

import com.pengrad.telegrambot.model.Chat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.mybots.alligator.exception.AlligatorApplicationException;
import ru.mybots.alligator.exception.AlligatorError;
import ru.mybots.alligator.dao.obj.Game;
import ru.mybots.alligator.dao.obj.Word;


import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

@Repository
public class AppRepository {

    private static final Logger log = LoggerFactory.getLogger(AppRepository.class);

    // Games cache
    // TODO always refresh DB copy when change
    private Map<Long, Game> games = new HashMap<>(100);
    private final Set<Long> chatSetWithGames = new HashSet<>();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        try {
            games = loadActiveGames();
            games.forEach((k,v) -> {
                chatSetWithGames.add(k);
            });
        } catch (AlligatorApplicationException e) {
            log.error(e.getMessage(), e);
        }
    }

    public Map<Long, Game> loadActiveGames() throws AlligatorApplicationException {
        List<Game> queryResult = jdbcTemplate.query(AppQueries.ALL_GAMES.sql(), new RowMapper<Game>() {
            @Override
            public Game mapRow(ResultSet resultSet, int i) throws SQLException {
                Game g = Game.createInstance(
                    resultSet.getLong("game_id"),
                    resultSet.getLong("chat_id"),
                    resultSet.getLong("lead_id"),
                    resultSet.getString("lead_title"),
                    resultSet.getLong("last_ord"),
                    resultSet.getBoolean("active"),
                    new Word(
                            resultSet.getLong("word_id"),
                            resultSet.getString("word_text")
                    ),
                    resultSet.getTimestamp("start_date", Calendar.getInstance(TimeZone.getTimeZone("UTC"))),
                    resultSet.getTimestamp("last_move_date", Calendar.getInstance(TimeZone.getTimeZone("UTC"))),
                    resultSet.getLong("winner_id"),
                    resultSet.getString("winner_title")
                );
                g.setGameId(resultSet.getLong("game_id"));
                return g;
            }
        });

        Map<Long, Game> result = new HashMap<>();
        queryResult.stream().filter(Game::isGameActive)
                .forEach(item -> {
                    result.put(item.getChatId(), item);
        });
        return result;
    }

    public void insertGame(Chat chat, Game game) throws AlligatorApplicationException {
        int result = jdbcTemplate.update(AppQueries.INSERT_GAME.sql(),
                game.getChatId(), game.getLeadId(), game.getLeadTitle(), game.getWord().getId(), Game.ACTIVE, game.getStartDate(), game.getLastMoveDate());
        if(result != 1) {
            throw new AlligatorApplicationException(AlligatorError.DB_FAILED_INSERT);
        }
        games.put(chat.id(), game);
        chatSetWithGames.add(chat.id());
    }

    public void updateGame(Chat chat, Game game) throws AlligatorApplicationException {
        updateGameInternal(game);
        games.put(chat.id(), game);
    }

    private void updateGameInternal(Game game) throws AlligatorApplicationException {
        int result = jdbcTemplate.update(AppQueries.UPDATE_GAME.sql(),
                game.getChatId(), game.getLeadId(), game.getLeadTitle(), game.getLastOrd(), game.getActive(), game.getWinnerId(), game.getWinnerTitle(), game.getGameId());
        if(result != 1) {
            throw new AlligatorApplicationException(AlligatorError.DB_FAILED_UPDATE);
        }
    }

//TODO: shuffling word order
    public Word nextWord(Game g) throws AlligatorApplicationException {
        List<Word> queryResult = jdbcTemplate.query(AppQueries.NEXT_WORD.sql(), new RowMapper<Word>() {
            @Override
            public Word mapRow(ResultSet resultSet, int i) throws SQLException {
                Word w = new Word(
                        resultSet.getLong("word_id"),
                        resultSet.getString("word_text")
                );
                return w;
            }
        }, g.getLastOrd());

        if(queryResult.isEmpty()) {
            queryResult = jdbcTemplate.query(AppQueries.NEXT_WORD.sql(), new RowMapper<Word>() {
                @Override
                public Word mapRow(ResultSet resultSet, int i) throws SQLException {
                    Word w = new Word(
                            resultSet.getLong("word_id"),
                            resultSet.getString("word_text")
                    );
                    return w;
                }
            }, 0);
            if(queryResult.isEmpty()) {
                throw new AlligatorApplicationException(AlligatorError.DB_FAILED_NEXT_WORD);
            }
        }
        return queryResult.get(0);
    }

    public Game lastGame(Long chatId) throws AlligatorApplicationException {
        Game game = games.get(chatId);
        if(game == null) {
            game = lastGameInternal(chatId);
            if(game == null || !game.isGameActive()) {
                game = null;
            }
        }
        return game;
    }

    private Game lastGameInternal(Long chatId) throws AlligatorApplicationException {
        List<Game> queryResult = jdbcTemplate.query(AppQueries.LAST_GAME.sql(), new RowMapper<Game>() {
            @Override
            public Game mapRow(ResultSet resultSet, int i) throws SQLException {
                Game g = Game.createInstance(
                        resultSet.getLong("game_id"),
                        resultSet.getLong("chat_id"),
                        resultSet.getLong("lead_id"),
                        resultSet.getString("lead_title"),
                        resultSet.getLong("last_ord"),
                        resultSet.getBoolean("active"),
                        new Word(
                                resultSet.getLong("word_id"),
                                resultSet.getString("word_text")
                        ),
                        resultSet.getTimestamp("start_date", Calendar.getInstance(TimeZone.getTimeZone("UTC"))),
                        resultSet.getTimestamp("last_move_date", Calendar.getInstance(TimeZone.getTimeZone("UTC"))),
                        resultSet.getLong("winner_id"),
                        resultSet.getString("winner_title")
                );
                g.setGameId(resultSet.getLong("game_id"));
                return g;
            }
        }, chatId);

        if(queryResult.isEmpty()) {
            return null;
        }
        return queryResult.get(0);
    }

    public void deleteGame(Game g) throws AlligatorApplicationException {
        int result = jdbcTemplate.update(AppQueries.DELETE_GAME.sql(),
                g.getChatId(), g.getLeadId(), g.getWord().getId(), g.getActive(), g.getGameId());
        if(result != 1) {
            throw new AlligatorApplicationException(AlligatorError.DB_FAILED_DELETE);
        }
    }
}

//    public void test() {
//        List<Object> l = jdbcTemplate.query("SELECT * from RATINGS", new RowMapper<Object>() {
//            @Override
//            public Object mapRow(ResultSet resultSet, int i) throws SQLException {
//                return resultSet.getString("name");
//            }
//        });
//        int i=0;
//        //Print read records:
//        l.forEach(System.out::println);
//    }