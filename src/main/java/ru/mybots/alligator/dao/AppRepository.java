package ru.mybots.alligator.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.mybots.alligator.exception.AlligatorApplicationException;
import ru.mybots.alligator.exception.AlligatorError;
import ru.mybots.alligator.dao.obj.Game;
import ru.mybots.alligator.dao.obj.Word;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Repository
public class AppRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<Long, Game> loadGames() throws AlligatorApplicationException {
        List<Game> queryResult = jdbcTemplate.query(AppQueries.ALL_GAMES.sql(), new RowMapper<Game>() {
            @Override
            public Game mapRow(ResultSet resultSet, int i) throws SQLException {
                Game g = Game.createInstance(
                    resultSet.getLong("chat_id"),
                    resultSet.getLong("lead_id"),
                    resultSet.getLong("last_ord"),
                    resultSet.getBoolean("active"),
                    new Word(
                            resultSet.getLong("word_id"),
                            resultSet.getString("word_text"),
                            resultSet.getLong("word_ord")
                    ),
                    resultSet.getTimestamp("start_date", Calendar.getInstance(TimeZone.getTimeZone("UTC"))),
                    resultSet.getTimestamp("last_move_date", Calendar.getInstance(TimeZone.getTimeZone("UTC")))
                );
                g.setGameId(resultSet.getLong("game_id"));
                return g;
            }
        });

        Map<Long, Game> result = new HashMap<>();
        queryResult.forEach(item -> {
            result.put(item.getChatId(), item);
        });
        return result;
    }

    public void insertGame(Game g) throws AlligatorApplicationException {
        int result = jdbcTemplate.update(AppQueries.INSERT_GAME.sql(),
                g.getChatId(), g.getLeadId(), g.getWord().getId(), Game.ACTIVE, g.getStartDate(), g.getLastMoveDate());
        if(result != 1) {
            throw new AlligatorApplicationException(AlligatorError.DB_FAILED_INSERT);
        }
    }

    public void updateGame(Game g) throws AlligatorApplicationException {
        int result = jdbcTemplate.update(AppQueries.UPDATE_GAME.sql(),
                g.getChatId(), g.getLeadId(), g.getLastOrd(), g.getActive(), g.getWinnerId(), g.getGameId());
        if(result != 1) {
            throw new AlligatorApplicationException(AlligatorError.DB_FAILED_UPDATE);
        }
    }

//TODO: shuffling word order
    public Word nextWord(Game g) throws AlligatorApplicationException {
        List<Word> queryResult = jdbcTemplate.query(AppQueries.NEXT_WORD.sql(), new RowMapper<Word>() {
            @Override
            public Word mapRow(ResultSet resultSet, int i) throws SQLException {
                Word g = new Word(
                        resultSet.getLong("word_id"),
                        resultSet.getString("word_text"),
                        resultSet.getLong("word_ord")
                );
                return g;
            }
        }, g.getLastOrd());

        if(queryResult.isEmpty()) {
            queryResult = jdbcTemplate.query(AppQueries.NEXT_WORD.sql(), new RowMapper<Word>() {
                @Override
                public Word mapRow(ResultSet resultSet, int i) throws SQLException {
                    Word g = new Word(
                            resultSet.getLong("word_id"),
                            resultSet.getString("word_text"),
                            resultSet.getLong("word_ord")
                    );
                    return g;
                }
            }, 0);
            if(queryResult.isEmpty()) {
                throw new AlligatorApplicationException(AlligatorError.DB_FAILED_NEXT_WORD);
            }
        }
        return queryResult.get(0);
    }

    public Game lastGame(Long chatId) throws AlligatorApplicationException {
        List<Game> queryResult = jdbcTemplate.query(AppQueries.LAST_GAME.sql(), new RowMapper<Game>() {
            @Override
            public Game mapRow(ResultSet resultSet, int i) throws SQLException {
                Game g = Game.createInstance(
                        resultSet.getLong("chat_id"),
                        resultSet.getLong("lead_id"),
                        resultSet.getLong("last_ord"),
                        resultSet.getBoolean("active"),
                        new Word(
                                resultSet.getLong("word_id"),
                                resultSet.getString("word_text"),
                                resultSet.getLong("word_ord")
                        ),
                        resultSet.getTimestamp("start_date", Calendar.getInstance(TimeZone.getTimeZone("UTC"))),
                        resultSet.getTimestamp("last_move_date", Calendar.getInstance(TimeZone.getTimeZone("UTC")))
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

    public void test() {
        List<Object> l = jdbcTemplate.query("SELECT * from RATINGS", new RowMapper<Object>() {
            @Override
            public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("name");
            }
        });
        int i=0;
        //Print read records:
        l.forEach(System.out::println);
    }

}
