package ru.mybots.alligator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import ru.mybots.alligator.dao.AppRepository;

import java.io.FileNotFoundException;

/*
	App initialization at BotService @Postconstruct method
 */
@SpringBootApplication
@Configuration
public class AlligatorApplication implements CommandLineRunner {

	@Autowired
	private AppRepository repo;

	public static void main(String[] args) throws FileNotFoundException {
		SpringApplication.run(AlligatorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		repo.test();
	}

}



