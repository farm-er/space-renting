package com.oussama.space_renting.controller;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;



class SchemaSpyTest extends AbstractControllerTest {

    @DisplayName( "Creating schema documentation with schema spy")
    @Test
    @SneakyThrows
    void schemaSpy() throws IOException {

        assertThat(POSTGRES.isRunning())
                .as("Postgres container must be running.")
                .isTrue();

        final Path buildFolderPath = Path.of("docs").toAbsolutePath();
        Files.createDirectories(buildFolderPath);

        try (var files = Files.list(buildFolderPath)) {
            files.forEach(file -> {
                try {
                    if (Files.isDirectory(file)) {
                        // recursively delete directories
                        deleteRecursively(file);
                    } else {
                        Files.deleteIfExists(file);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        @Cleanup final var schemaSpy =
                new GenericContainer<>(DockerImageName.parse("schemaspy/schemaspy:7.0.2"))
                        .withNetworkAliases("schemaspy")
                        .withNetwork(NETWORK)
                        .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("SchemaSpy")))
                        .withCreateContainerCmdModifier(cmd -> cmd.withEntrypoint(""))
                        .withFileSystemBind(
                                buildFolderPath.toString(),
                                Path.of("/output").toString(),
                                BindMode.READ_WRITE
                        ).withCommand("sleep", "500000");

        schemaSpy.start();


        Container.ExecResult result = null;
        try {
            result = schemaSpy.execInContainer(
                    "java", "-jar", "./usr/local/lib/schemaspy/schemaspy-app.jar",
                    "-t", "pgsql",
                    "-host", "postgres",
                    "-port", "5432",
                    "-u", POSTGRES.getUsername(),
                    "-p", POSTGRES.getPassword(),
                    "-db", POSTGRES.getDatabaseName(),
                    "-o", "/output",
                    "-dp", "/drivers_inc",
                    "-debug"
            );
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        schemaSpy.close();

        System.out.println("Exit code: " + result.getExitCode());
        System.out.println("STDOUT: " + result.getStdout());
        System.out.println("STDERR: " + result.getStderr());

        System.out.println("Files in docs directory:");
        Files.list(buildFolderPath).forEach(System.out::println);

    }

    private void deleteRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (var stream = Files.list(path)) {
                for (Path entry : stream.toList()) {
                    deleteRecursively(entry);
                }
            }
        }
        Files.deleteIfExists(path);
    }
}