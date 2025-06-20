package com.oussama.space_renting.controller;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.oussama.space_renting.controller.AbstractControllerTest.Initializer;
import java.util.Map;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(initializers = Initializer.class)
class AbstractControllerTest {

    public static final Network NETWORK = Network.newNetwork();

    public static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:15.13")
                    .withNetworkAliases("postgres")
                    .withUsername("postgres")
                    .withPassword("postgres")
                    .withDatabaseName("space_renting_db")
                    .withNetwork(NETWORK);

    static class Initializer implements
            ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext context) {
            Startables.deepStart(POSTGRES).join();

            context.getEnvironment()
                    .getPropertySources()
                    .addFirst(new MapPropertySource(
                            "testcontainers",
                            Map.of(
                                    "spring.datasource.url", POSTGRES.getJdbcUrl(),
                                    "spring.datasource.username", POSTGRES.getUsername(),
                                    "spring.datasource.password", POSTGRES.getPassword()
                            )
                    ));
        }
    }
}
