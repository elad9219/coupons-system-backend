package com.jb.spring_coupons_project.security;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@Order(2)
@RequiredArgsConstructor
public class PasswordHashMigration implements CommandLineRunner {

    private static final Logger log =
            LoggerFactory.getLogger(PasswordHashMigration.class);

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) {
        int companies = migrate(
                "companies",
                "id"
        );

        int customers = migrate(
                "customers",
                "customer_id"
        );

        log.info(
                "Password migration completed: {} companies, {} customers updated",
                companies,
                customers
        );
    }

    private int migrate(String tableName, String idColumn) {
        // Both SQL identifiers are fixed by application code.
        List<Map<String, Object>> rows =
                jdbcTemplate.queryForList(
                        "SELECT " + idColumn + ", password FROM " + tableName
                );

        int updated = 0;

        for (Map<String, Object> row : rows) {
            int id = ((Number) row.get(idColumn)).intValue();
            String storedPassword = (String) row.get("password");

            if (PasswordCodec.isEncoded(storedPassword)) {
                continue;
            }

            if (storedPassword == null || storedPassword.isBlank()) {
                throw new IllegalStateException(
                        "Account " + id + " in " + tableName +
                                " has an empty legacy password"
                );
            }

            String encodedPassword =
                    PasswordCodec.encode(storedPassword);

            int affectedRows = jdbcTemplate.update(
                    "UPDATE " + tableName +
                            " SET password = ? WHERE " + idColumn + " = ?",
                    encodedPassword,
                    id
            );

            if (affectedRows != 1) {
                throw new IllegalStateException(
                        "Password migration failed for account " + id +
                                " in " + tableName
                );
            }

            updated++;
        }

        return updated;
    }
}
