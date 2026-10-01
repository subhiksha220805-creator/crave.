package com.example.demowithswiggy.service;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.stereotype.Component;

/** Repairs the FK left behind when the old `user` table was renamed to `app_users`. */
@Component
@Order(0)
public class LegacyOrderForeignKeyRepair implements ApplicationRunner {
    private final JdbcTemplate jdbc;

    public LegacyOrderForeignKeyRepair(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        String product = jdbc.execute((ConnectionCallback<String>) connection ->
                connection.getMetaData().getDatabaseProductName());
        if (product == null || !product.toLowerCase().contains("mysql")) return;

        Integer tableCount = jdbc.queryForObject(
                "select count(*) from information_schema.tables where table_schema = database() and table_name = 'app_users'",
                Integer.class);
        if (tableCount == null || tableCount == 0) return;

        List<String> staleKeys = jdbc.queryForList(
                "select constraint_name from information_schema.key_column_usage "
                        + "where table_schema = database() and table_name = 'food_order' "
                        + "and column_name = 'customer_id' and referenced_table_name = 'user'",
                String.class);
        if (staleKeys.isEmpty()) return;

        Long orphanCount = jdbc.queryForObject(
                "select count(*) from food_order o left join app_users u on u.id = o.customer_id "
                        + "where o.customer_id is not null and u.id is null",
                Long.class);
        if (orphanCount != null && orphanCount > 0) {
            throw new IllegalStateException("Cannot update the legacy food_order customer foreign key: "
                    + orphanCount + " existing order(s) refer to missing app_users rows.");
        }

        for (String key : staleKeys) {
            jdbc.execute("alter table food_order drop foreign key `" + key.replace("`", "``") + "`");
        }
        jdbc.execute("alter table food_order add constraint fk_food_order_customer_app_users "
                + "foreign key (customer_id) references app_users(id)");
    }
}
