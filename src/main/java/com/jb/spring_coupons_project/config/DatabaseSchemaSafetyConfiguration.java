
package com.jb.spring_coupons_project.config;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Locale;
import java.util.Set;

@Configuration
public class DatabaseSchemaSafetyConfiguration {

    private static final Set<String> DESTRUCTIVE_STRATEGIES =
            Set.of("create", "create-drop", "drop");

    @Bean
    public static BeanFactoryPostProcessor databaseSchemaSafetyPostProcessor(
            Environment environment) {

        return beanFactory -> {
            String ddlAuto = environment.getProperty(
                    "spring.jpa.hibernate.ddl-auto"
            );

            String hibernateDdlAuto = environment.getProperty(
                    "spring.jpa.properties.hibernate.hbm2ddl.auto"
            );

            String datasourceUrl = environment.getProperty(
                    "spring.datasource.url"
            );

            boolean demoResetEnabled = Boolean.parseBoolean(
                    environment.getProperty(
                            "COUPONS_DEMO_RESET_ON_STARTUP",
                            "false"
                    )
            );

            boolean publicDemoEnabled = Boolean.parseBoolean(
                    environment.getProperty(
                            "COUPONS_PUBLIC_DEMO",
                            "false"
                    )
            );

            validateSchemaSettings(
                    ddlAuto,
                    hibernateDdlAuto,
                    datasourceUrl,
                    demoResetEnabled
            );

            validatePublicDemoSettings(
                    ddlAuto,
                    hibernateDdlAuto,
                    datasourceUrl,
                    demoResetEnabled,
                    publicDemoEnabled
            );
        };
    }

    static void validateSchemaSettings(
            String ddlAuto,
            String hibernateDdlAuto,
            String datasourceUrl,
            boolean demoResetEnabled) {

        if (isInMemoryH2(datasourceUrl)) {
            return;
        }

        String springStrategy = normalize(ddlAuto);
        String hibernateStrategy = normalize(hibernateDdlAuto);

        boolean destructive =
                isDestructive(springStrategy) ||
                        isDestructive(hibernateStrategy);

        if (!destructive) {
            return;
        }

        if (isIntentionalDemoReset(
                springStrategy,
                hibernateStrategy,
                datasourceUrl,
                demoResetEnabled
        )) {
            return;
        }

        throw new IllegalStateException(
                "Destructive database schema changes are blocked. " +
                        "For a dedicated, disposable PostgreSQL demo database, " +
                        "use ddl-auto=create and explicitly set " +
                        "COUPONS_DEMO_RESET_ON_STARTUP=true. " +
                        "Never enable this for a database containing " +
                        "data that must be preserved."
        );
    }

    static void validatePublicDemoSettings(
            String ddlAuto,
            String hibernateDdlAuto,
            String datasourceUrl,
            boolean demoResetEnabled,
            boolean publicDemoEnabled) {

        if (!publicDemoEnabled) {
            return;
        }

        if (!isIntentionalDemoReset(
                normalize(ddlAuto),
                normalize(hibernateDdlAuto),
                datasourceUrl,
                demoResetEnabled
        )) {
            throw new IllegalStateException(
                    "Public demo requires a dedicated, disposable " +
                            "PostgreSQL database with ddl-auto=create " +
                            "and COUPONS_DEMO_RESET_ON_STARTUP=true. " +
                            "Do not enable public demo mode on a database " +
                            "containing real or persistent user data."
            );
        }
    }

    private static boolean isIntentionalDemoReset(
            String springStrategy,
            String hibernateStrategy,
            String datasourceUrl,
            boolean demoResetEnabled) {

        return demoResetEnabled &&
                isPostgreSql(datasourceUrl) &&
                "create".equals(springStrategy) &&
                (hibernateStrategy == null ||
                        hibernateStrategy.isEmpty() ||
                        "create".equals(hibernateStrategy));
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }

        return value.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean isDestructive(String strategy) {
        return strategy != null &&
                DESTRUCTIVE_STRATEGIES.contains(strategy);
    }

    private static boolean isInMemoryH2(String url) {
        return url != null &&
                url.trim().toLowerCase(Locale.ROOT)
                        .startsWith("jdbc:h2:mem:");
    }

    private static boolean isPostgreSql(String url) {
        return url != null &&
                url.trim().toLowerCase(Locale.ROOT)
                        .startsWith("jdbc:postgresql:");
    }
}
