package com.jing.admin.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @author lxh
 * @date 2025/10/30
 **/
@Configuration
public class MultiDataSourceConfig {
    @Value("${app.datasource.common.url:jdbc:postgresql://localhost:5432/common_db}")
    private String commonDbUrl;

    @Value("${app.datasource.common.username:postgres}")
    private String commonDbUsername;

    @Value("${app.datasource.common.password:postgres}")
    private String commonDbPassword;

    @Value("${app.datasource.common.max-pool-size:20}")
    private int maxPoolSize;

    @Bean
    @Primary
    public DataSource commonDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(commonDbUrl);
        dataSource.setUsername(commonDbUsername);
        dataSource.setPassword(commonDbPassword);
        dataSource.setMaximumPoolSize(maxPoolSize);
        dataSource.setMinimumIdle(5);
        return dataSource;
    }
}
