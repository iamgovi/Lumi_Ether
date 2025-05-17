package com.spidrox;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;

import org.jboss.logging.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import io.quarkus.runtime.Startup;


@Startup
@ApplicationScoped
public class DockerStartupManager {
	private static final Logger LOGGER = Logger.getLogger(DockerStartupManager.class);

    @PostConstruct
    public void startContainers() {
        try {
            LOGGER.info("🚀 Starting Docker setup...");

            // Remove existing containers if they exist
            removeIfExists("container", "postgres-age");
            removeIfExists("container", "pgadmin_container");

            // Remove network if exists
            removeIfExists("network", "postgres_network");

            // Create network
            runCommand(List.of("docker", "network", "create", "postgres_network"));

            // Start Postgres AGE
            runCommand(List.of(
                    "docker", "run", "-d",
                    "--name", "postgres-age",
                    "--network", "postgres_network",
                    "-e", "POSTGRES_USER=admin",
                    "-e", "POSTGRES_PASSWORD=adminpassword",
                    "-e", "POSTGRES_DB=postgres",
                    "-v", "pgdata:/var/lib/postgresql/data",
                    "-p", "5432:5432",
                    "apache/age"
            ));

            // Start PGAdmin
            runCommand(List.of(
                    "docker", "run", "-d",
                    "--name", "pgadmin_container",
                    "--network", "postgres_network",
                    "-e", "PGADMIN_DEFAULT_EMAIL=admin@admin.com",
                    "-e", "PGADMIN_DEFAULT_PASSWORD=admin",
                    "-v", "pgadmin_data:/var/lib/pgadmin",
                    "-p", "5050:80",
                    "dpage/pgadmin4:latest"
            ));

            LOGGER.info("✅ Docker containers started successfully.");

        } catch (Exception e) {
            LOGGER.error("❌ Failed to start Docker containers: ", e);
        }
    }

    private void removeIfExists(String type, String name) throws Exception {
        // type: "container" or "network"
        ProcessBuilder pb = new ProcessBuilder("docker", type, "rm", "-f", name);
        if (type.equals("network")) {
            pb = new ProcessBuilder("docker", type, "rm", name);
        }
        pb.redirectErrorStream(true);
        Process process = pb.start();
        process.waitFor(); // Ignore failure if it doesn't exist
    }


    private void runCommand(List<String> command) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                LOGGER.info(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Command failed: " + String.join(" ", command));
        }
    }
    @GET
    public String hello() {
        return "Hello, from DockerStartupManager";
    }
}
