package noob.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@ComponentScan(value = "noob",
    excludeFilters = {
        @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = { org.springframework.stereotype.Controller.class }
        )
    })
@PropertySource("classpath:jdbc.properties")
@MapperScan("noob.mapper") //扫描Mapper接口
@EnableTransactionManagement //开启事务管理
public class ApplicationConfiguration {

    // 配置信息对象
    @Autowired
    private PropertiesConfig propertiesConfig;

    /**
     * 配置数据源
     */
    @Bean
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUsername(propertiesConfig.getUsername());
        dataSource.setPassword(propertiesConfig.getPassword());
        dataSource.setUrl(propertiesConfig.getUrl());
        dataSource.setDriverClassName(propertiesConfig.getDriver());
        return dataSource;
    }

    /**
     * 配置 SqlSessionFactory
     */
    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) throws IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setTypeAliasesPackage("noob.entity");

        // 动态获取SqlMapper
        PathMatchingResourcePatternResolver classPathResource = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setConfigLocation(classPathResource.getResource("classpath:mybatis-config.xml"));
        sqlSessionFactoryBean.setMapperLocations(classPathResource.getResources("classpath:mapper/*.xml"));
        return sqlSessionFactoryBean;
    }

    /**
     * 配置事务
     */
    @Bean
    public TransactionManager transactionManager(DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
        return dataSourceTransactionManager;
    }
}
