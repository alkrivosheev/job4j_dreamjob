package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;
import java.util.Properties;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;
    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        try (var connection = sql2o.open()) {
            connection.createQuery("TRUNCATE TABLE users").executeUpdate();
        }
    }

    @Test
    public void whenSaveUserThenGetSame() {
        var user = sql2oUserRepository.save(new User("test@mail.com", "name", "password")).get();
        var savedUser = sql2oUserRepository.findByEmailAndPassword(user.getEmail(), user.getPassword()).get();
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenSaveSeveralUsersThenFindByEmailAndPassword() {
        var user1 = sql2oUserRepository.save(new User("user1@mail.com", "name1", "password1")).get();
        var user2 = sql2oUserRepository.save(new User("user2@mail.com", "name2", "password2")).get();

        var result = sql2oUserRepository.findByEmailAndPassword(user1.getEmail(), user1.getPassword());
        assertThat(result).isPresent().get().usingRecursiveComparison().isEqualTo(user1);

        result = sql2oUserRepository.findByEmailAndPassword(user2.getEmail(), user2.getPassword());
        assertThat(result).isPresent().get().usingRecursiveComparison().isEqualTo(user2);
    }

    @Test
    public void whenFindByInvalidEmailAndPasswordThenEmpty() {
        sql2oUserRepository.save(new User("valid@mail.com", "name", "password"));
        var result = sql2oUserRepository.findByEmailAndPassword("invalid@mail.com", "wrong");
        assertThat(result).isEmpty();
    }

    @Test
    public void whenSaveUserWithExistingEmailThenExceptionThrown() {
        String email = "duplicate@mail.com";
        var user1 = new User(email, "name1", "password1");
        var user2 = new User(email, "name2", "password2");

        sql2oUserRepository.save(user1);
        var result = sql2oUserRepository.save(user2);
        assertThat(result).isEmpty();
    }
}