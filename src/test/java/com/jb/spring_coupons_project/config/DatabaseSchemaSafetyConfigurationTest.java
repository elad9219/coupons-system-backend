package com.jb.spring_coupons_project.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DatabaseSchemaSafetyConfigurationTest {

    private static final String POSTGRES_URL =
            "jdbc:postgresql://localhost:55432/coupons_local";

    @Test
    void allowsValidationForPostgreSQL() {
        assertDoesNotThrow(() ->
                DatabaseSchemaSafetyConfiguration.validateSchemaSettings(
                        "validate", null, POSTGRES_URL, false
                )
        );
    }

    @Test
    void allowsUpdateForPostgreSQL() {
        assertDoesNotThrow(() ->
                DatabaseSchemaSafetyConfiguration.validateSchemaSettings(
                        "update", null, POSTGRES_URL, false
                )
        );
    }

    @Test
    void rejectsCreateWithoutExplicitDemoOptIn() {
        assertThrows(IllegalStateException.class, () ->
                DatabaseSchemaSafetyConfiguration.validateSchemaSettings(
                        "create", null, POSTGRES_URL, false
                )
        );
    }

    @Test
    void allowsCreateWithExplicitDemoOptIn() {
        assertDoesNotThrow(() ->
                DatabaseSchemaSafetyConfiguration.validateSchemaSettings(
                        "create", null, POSTGRES_URL, true
                )
        );
    }

    @Test
    void rejectsCreateDropWithoutOptIn() {
        assertThrows(IllegalStateException.class, () ->
                DatabaseSchemaSafetyConfiguration.validateSchemaSettings(
                        "create-drop", null, POSTGRES_URL, false
                )
        );
    }

    @Test
    void rejectsCreateDropEvenWithDemoOptIn() {
        assertThrows(IllegalStateException.class, () ->
                DatabaseSchemaSafetyConfiguration.validateSchemaSettings(
                        "create-drop", null, POSTGRES_URL, true
                )
        );
    }

    @Test
    void rejectsDropEvenWithDemoOptIn() {
        assertThrows(IllegalStateException.class, () ->
                DatabaseSchemaSafetyConfiguration.validateSchemaSettings(
                        "drop", null, POSTGRES_URL, true
                )
        );
    }

    @Test
    void rejectsDestructiveNativeHibernateSetting() {
        assertThrows(IllegalStateException.class, () ->
                DatabaseSchemaSafetyConfiguration.validateSchemaSettings(
                        "validate", "create", POSTGRES_URL, false
                )
        );
    }

    @Test
    void rejectsConflictingHibernateSettingsEvenWithDemoOptIn() {
        assertThrows(IllegalStateException.class, () ->
                DatabaseSchemaSafetyConfiguration.validateSchemaSettings(
                        "create", "create-drop", POSTGRES_URL, true
                )
        );
    }

    @Test
    void rejectsCreateWhenDatabaseUrlIsMissing() {
        assertThrows(IllegalStateException.class, () ->
                DatabaseSchemaSafetyConfiguration.validateSchemaSettings(
                        "create", null, null, true
                )
        );
    }

    @Test
    void rejectsCreateForOtherDatabaseTypesDespiteOptIn() {
        assertThrows(IllegalStateException.class, () ->
                DatabaseSchemaSafetyConfiguration.validateSchemaSettings(
                        "create",
                        null,
                        "jdbc:h2:file:./persistent-database",
                        true
                )
        );
    }

    @Test
    void permitsCreateDropForInMemoryH2Tests() {
        assertDoesNotThrow(() ->
                DatabaseSchemaSafetyConfiguration.validateSchemaSettings(
                        "create-drop",
                        null,
                        "jdbc:h2:mem:coupons_context_test",
                        false
                )
        );
    }
}