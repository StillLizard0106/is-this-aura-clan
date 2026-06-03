package com.is_this_aura_clan.CanteenQ.bootstrap;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PostgresRoleConstraintFixRunner implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresRoleConstraintFixRunner.class);
    private final DataSource dataSource;

    public PostgresRoleConstraintFixRunner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        try (Connection connection = dataSource.getConnection()) {
            String productName = connection.getMetaData().getDatabaseProductName();
            if (productName == null || !productName.toLowerCase().contains("postgres")) {
                return;
            }

            try (Statement statement = connection.createStatement()) {
                LOGGER.info("Applying PostgreSQL users.role cleanup and constraint repair if needed");
                statement.executeUpdate("UPDATE users SET role = upper(role) WHERE role IS NOT NULL");
                statement.executeUpdate("ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check");
                statement.executeUpdate("ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role IN ('STUDENT', 'ADMIN', 'STAFF'))");
            }
        } catch (SQLException exception) {
            LOGGER.warn("Unable to repair PostgreSQL user role constraint on startup: {}", exception.getMessage());
        }
    }
}
