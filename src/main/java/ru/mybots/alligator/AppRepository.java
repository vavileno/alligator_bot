package ru.mybots.alligator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AppRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<Long, Game> loadGames() {
        List<Game> queryResult = jdbcTemplate.query(AppQueries.ALL_GAMES.sql(), new RowMapper<Game>() {
            @Override
            public Game mapRow(ResultSet resultSet, int i) throws SQLException {
                resultSet.getLong("chat_id");
                resultSet.getLong("lead_id");
                resultSet.getLong("word_id");

                Game g = Game.create(
                    resultSet.getLong("chat_id"),
                    resultSet.getLong("lead_id"),
                    resultSet.getLong("word_id")
                );

                return null;
            }
        });

        Map<Long, Game> result = new HashMap<>();
        queryResult.stream().forEach(item -> {
            result.put(item.getChatId(), item);
        });
        return result;
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
