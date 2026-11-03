
package com.jb.spring_coupons_project.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PublicDemoConfigurationTest {

    private static final String POSTGRES_URL =
            "jdbc:postgresql://localhost:55433/coupons_demo";

    @Test
    void publicDemoAllowsExplicitResetConfiguration() {
        assertDoesNotThrow(() ->
                validate(
                        "create", null,
                        POSTGRES_URL, true, true
                )
        );
    }

    @Test
    void privateEnvironmentDoesNotRequireDemoReset() {
        assertDoesNotThrow(() ->
                validate(
                        "validate", null,
                        POSTGRES_URL, false, false
                )
        );
    }

    @Test
    void publicDemoRejectsMissingResetOptIn() {
        assertThrows(IllegalStateException.class, () ->
                validate(
                        "create", null,
                        POSTGRES_URL, false, true
                )
        );
    }

    @Test
    void publicDemoRejectsValidateStrategy() {
        assertThrows(IllegalStateException.class, () ->
                validate(
                        "validate", null,
                        POSTGRES_URL, true, true
                )
        );
    }

    @Test
    void publicDemoRejectsCreateDrop() {
        assertThrows(IllegalStateException.class, () ->
                validate(
                        "create-drop", null,
                        POSTGRES_URL, true, true
                )
        );
    }

    @Test
    void publicDemoRejectsInMemoryDatabase() {
        assertThrows(IllegalStateException.class, () ->
                validate(
                        "create", null,
                        "jdbc:h2:mem:test", true, true
                )
        );
    }

    @Test
    void publicDemoRejectsMissingDatabaseUrl() {
        assertThrows(IllegalStateException.class, () ->
                validate(
                        "create", null,
                        null, true, true
                )
        );
    }

    @Test
    void publicDemoRejectsConflictingHibernateStrategy() {
        assertThrows(IllegalStateException.class, () ->
                validate(
                        "create", "create-drop",
                        POSTGRES_URL, true, true
                )
        );
    }

    private static void validate(
            String ddlAuto,
            String hibernateDdlAuto,
            String datasourceUrl,
            boolean demoResetEnabled,
            boolean publicDemoEnabled) {

        DatabaseSchemaSafetyConfiguration.validateSchemaSettings(
                ddlAuto,
                hibernateDdlAuto,
                datasourceUrl,
                demoResetEnabled
        );

        DatabaseSchemaSafetyConfiguration.validatePublicDemoSettings(
                ddlAuto,
                hibernateDdlAuto,
                datasourceUrl,
                demoResetEnabled,
                publicDemoEnabled
        );
    }
}
