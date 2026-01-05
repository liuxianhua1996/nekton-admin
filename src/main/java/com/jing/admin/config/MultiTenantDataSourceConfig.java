package com.jing.admin.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.jing.admin.core.tenant.TenantDynamicDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @author lxh
 * @date 2025/10/30
 **/
@Configuration
public class MultiTenantDataSourceConfig {
    @Value("${app.datasource.common.url:jdbc:postgresql://localhost:5432/common_db}")
    private String commonDbUrl;

    @Value("${app.datasource.common.username:postgres}")
    private String commonDbUsername;

    @Value("${app.datasource.common.password:postgres}")
    private String commonDbPassword;

    @Value("${app.datasource.common.max-pool-size:20}")
    private int maxPoolSize;
    @Autowired
    private MybatisPlusInterceptor mybatisPlusInterceptor;
    @Autowired
    GlobalConfig globalConfig;

    @Bean
    public DataSource commonDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(commonDbUrl);
        dataSource.setUsername(commonDbUsername);
        dataSource.setPassword(commonDbPassword);
        dataSource.setMaximumPoolSize(maxPoolSize);
        dataSource.setMinimumIdle(5);
        return dataSource;
    }
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(TenantDynamicDataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);  // 使用动态数据源

        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        mybatisConfiguration.setMapUnderscoreToCamelCase(true);
        mybatisConfiguration.setDefaultFetchSize(100);
        mybatisConfiguration.setLogImpl(StdOutImpl.class);
        mybatisConfiguration.setCacheEnabled(true);

        sessionFactory.setConfiguration(mybatisConfiguration);
        sessionFactory.setTypeAliasesPackage("com.jing.admin.model.domain");
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/*.xml"));
        sessionFactory.setGlobalConfig(globalConfig);
        return sessionFactory.getObject();
    }

    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public DataSourceTransactionManager transactionManager(TenantDynamicDataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
