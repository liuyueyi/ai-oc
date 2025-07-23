package com.git.hui.offer.web.config.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 表初始化，只有首次启动时，才会执行
 *
 * @author YiHui
 * @date 2022/10/15
 */
@Slf4j
@DependsOn("environment")
@ConditionalOnClass(name = "liquibase.Liquibase")
@Configuration
public class OcDataSourceInitializer implements BeanDefinitionRegistryPostProcessor, PriorityOrdered, EnvironmentAware {
    @Value("${oc.database.name}")
    private String database;

    @Value("${spring.liquibase.enabled:true}")
    private Boolean liquibaseEnable;

    @Value("${spring.liquibase.change-log}")
    private String liquibaseChangeLog;

    private Environment environment;

    public String getDatabase() {
        database = database != null ? database : environment.getProperty("oc.database.name");
        return database;
    }

    public Boolean getLiquibaseEnable() {
        liquibaseEnable = liquibaseEnable != null ? liquibaseEnable : Boolean.parseBoolean(environment.getProperty("spring.liquibase.enabled", "true"));
        return this.liquibaseEnable;
    }

    public String getLiquibaseChangeLog() {
        liquibaseChangeLog = liquibaseChangeLog != null ? liquibaseChangeLog : environment.getProperty("spring.liquibase.change-log");
        return liquibaseChangeLog;
    }

    /**
     * 主要是确保 OcDataSourceInitializer 先初始化实现在库不存在时，进行自动创建，避免JPA启动失败
     *
     * @param registry
     * @throws BeansException
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        log.info("开始初始化数据库表...");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        autoInitDatabase();
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {
        final DataSourceInitializer initializer = new DataSourceInitializer();
        // 设置数据源
        initializer.setDataSource(dataSource);
        boolean enable = needInit(dataSource);
        initializer.setEnabled(enable);
        initializer.setDatabasePopulator(databasePopulator(enable));
        if (!getLiquibaseEnable()) {
            if (!enable) {
                log.info("非Liquibase管理数据库，非首次启动，不执行项目SQL脚本；如有需要，请手动执行更新~");
            } else {
                log.info("非Liquibase管理数据，首次启动，自动执行项目中SQL脚本~");
            }
        }
        return initializer;
    }

    private DatabasePopulator databasePopulator(boolean initEnable) {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        // 下面这种是根据sql文件来进行初始化；改成 liquibase 之后不再使用这种方案，由liquibase来统一管理表结构数据变更
        if (initEnable && !getLiquibaseEnable()) {
            // fixme: 首次启动时, 对于不支持liquibase的数据库，如mariadb，采用主动初始化
            // fixme 这种方式不支持后续动态的数据表结构更新、数据变更
            populator.addScripts(DbChangeSetLoader.loadDbChangeSetResources(getLiquibaseChangeLog()).toArray(new ClassPathResource[]{}));
            populator.setSeparator(";");
            log.info("非Liquibase管理数据库，请手动执行数据库表初始化!");
        }
        return populator;
    }

    /**
     * 检测一下数据库中表是否存在，若存在则不初始化；否则基于 schema-all.sql 进行初始化表
     *
     * @param dataSource
     * @return true 表示需要初始化； false 表示无需初始化
     */
    private boolean needInit(DataSource dataSource) {
        if (autoInitDatabase()) {
            return true;
        }

        // 根据是否存在表来判断是否需要执行sql操作
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        if (!getLiquibaseEnable()) {
            // 非liquibase做数据库版本管理的，根据用户来判断是否有初始化
            List list = jdbcTemplate.queryForList("SELECT table_name FROM information_schema.TABLES where table_name = 'user_info' and table_schema = '" + this.getDatabase() + "';");
            return CollectionUtils.isEmpty(list);
        }

        return false;
    }

    /**
     * 数据库不存在时，尝试创建数据库
     */
    private boolean autoInitDatabase() {
        // 查询失败，可能是数据库不存在，尝试创建数据库之后再次测试
        // 数据库链接
        URI url = URI.create(getConfigOrElse("spring.datasource.url", "spring.dynamic.datasource.master.url").substring(5));
        // 用户名
        String uname = getConfigOrElse("spring.datasource.username", "spring.dynamic.datasource.master.username");
        // 密码
        String pwd = getConfigOrElse("spring.datasource.password", "spring.dynamic.datasource.master.password");
        // 创建连接
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://" + url.getHost() + ":" + url.getPort() +
                "?" + url.getRawQuery(), uname, pwd);
             Statement statement = connection.createStatement()) {
            String database = getDatabase();
            // 查询数据库是否存在
            String dbQuerySql = "select schema_name from information_schema.schemata where schema_name = '" + database + "'";
            ResultSet set = statement.executeQuery(dbQuerySql);
            if (!set.next()) {
                // 不存在时，创建数据库
                String createDb = new StringBuilder("CREATE DATABASE IF NOT EXISTS `").append(database).append("`").toString();
                connection.setAutoCommit(false);
                statement.execute(createDb);
                connection.commit();
                log.info("创建数据库（{}）成功", database);
                if (set.isClosed()) {
                    set.close();
                }
                return true;
            }
            set.close();
            log.info("数据库已存在，无需初始化");
            return false;
        } catch (SQLException e2) {
            throw new RuntimeException(e2);
        }
    }

    private String getConfigOrElse(String mainKey, String slaveKey) {
        String ans = environment.getProperty(mainKey);
        if (ans == null) {
            return environment.getProperty(slaveKey);
        }
        return ans;
    }


}
