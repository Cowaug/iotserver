package vn.edu.hcmut.iotserver.configs;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@MapperScan("vn.edu.hcmut.iotserver")
@ComponentScan(basePackages = { "vn.edu.hcmut.iotserver" })
@EnableTransactionManagement
public class AppContext {
    @Autowired
    private ResourcePatternResolver resourceLoader;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        URI jdbUri = null;
        try {
            jdbUri = new URI(System.getenv("DB_URL"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://" + jdbUri.getHost() + ":" + jdbUri.getPort() + jdbUri.getPath());
        dataSource.setUsername(jdbUri.getUserInfo().split(":")[0]);
        dataSource.setPassword(jdbUri.getUserInfo().split(":")[1]);

        return dataSource;
    }

    @Bean("sqlSession")
    public SqlSessionFactory sessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        Resource resource = new ClassPathResource("SqlMapConfig.xml");
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setConfigLocation(resource);
//        sessionFactory.setMapperLocations(resourceLoader.getResources("/src/main/resources/mapper/*.xml"));
        return sessionFactory.getObject();
    }

    @Bean
    SqlSessionTemplate sqlSessionTemplate() throws Exception {

        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sessionFactory());

        return sqlSessionTemplate;

    }

    @Bean
    public DataSourceTransactionManager transactionManager() {

        return new DataSourceTransactionManager(dataSource());

    }
}
