package ru.mybots.alligator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@SpringBootApplication
@Configuration
public class AlligatorApplication implements CommandLineRunner {

	@Autowired
	private AppRepository repo;

	public static void main(String[] args) {
		SpringApplication.run(AlligatorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		repo.test();
	}

	private void resumeGame() {
		
	}

}



