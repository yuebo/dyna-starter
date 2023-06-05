package com.github.yuebo;

import com.github.yuebo.dyna.backup.SystemInit;
import com.github.yuebo.dyna.config.JsonConfigProperties;
import com.github.yuebo.dyna.service.JDBCService;
import com.github.yuebo.dyna.service.MysqlJDBCService;
import com.github.yuebo.dyna.service.MysqlTableGenerator;
import com.github.yuebo.dyna.service.TableGenerator;
import com.github.yuebo.dyna.utils.ClasspathViewLoader;
import org.activiti.engine.*;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ComponentScan({"com.github.yuebo.dyna","com.github.yuebo.elfinder"})
@EnableCaching
@EnableAsync
@EnableConfigurationProperties
public class DynaAutoConfiguration {
    @Value("${dyna.init.data:false}")
    boolean initData;
    @Value("${dyna.init.view:true}")
    boolean initView;
    @Value("${dyna.data.files:user.json;message.json}")
    String dataFiles;
    @Value("${dyna.jdbc.autoUpdate:true}")
    boolean autoUpdate;

    @Bean
    JDBCService jdbcService(@Autowired NamedParameterJdbcTemplate jdbcTemplate,@Autowired TableGenerator tableGenerator){
        MysqlJDBCService mysqlJDBCService=new MysqlJDBCService();
        mysqlJDBCService.setJdbcTemplate(jdbcTemplate);
        mysqlJDBCService.setGenerator(tableGenerator);
        mysqlJDBCService.setAutoUpdate(autoUpdate);
        return mysqlJDBCService;
    }
    @Bean NamedParameterJdbcTemplate namedParameterJdbcTemplate(@Autowired DataSource dataSource){
        return new NamedParameterJdbcTemplate(dataSource);
    }
    @Bean
    TableGenerator tableGenerator(@Autowired NamedParameterJdbcTemplate jdbcTemplate){
        MysqlTableGenerator mysqlTableGenerator= new MysqlTableGenerator();
        mysqlTableGenerator.setJdbcTemplate(jdbcTemplate);
        return mysqlTableGenerator;
    }
    @Bean
    ClasspathViewLoader viewLoader(@Autowired JsonConfigProperties jsonConfigProperties){
        ClasspathViewLoader classpathViewLoader=new ClasspathViewLoader();
        classpathViewLoader.setPath(jsonConfigProperties.getPath());
        return classpathViewLoader;
    }

    @Bean
    SpringProcessEngineConfiguration springProcessEngineConfiguration(@Autowired DataSource dataSource, @Autowired PlatformTransactionManager transactionManager){
        SpringProcessEngineConfiguration springProcessEngineConfiguration=new SpringProcessEngineConfiguration();
        springProcessEngineConfiguration.setDataSource(dataSource);
        springProcessEngineConfiguration.setTransactionManager(transactionManager);
        springProcessEngineConfiguration.setDatabaseType("mysql");
        springProcessEngineConfiguration.setDatabaseSchemaUpdate("true");
        springProcessEngineConfiguration.setActivityFontName("宋体");
        springProcessEngineConfiguration.setLabelFontName("宋体");
        springProcessEngineConfiguration.setActivityFontName("宋体");
        List<SessionFactory> list=new ArrayList();
        springProcessEngineConfiguration.setCustomSessionFactories(list);

        return springProcessEngineConfiguration;
    }

    @Bean
    ProcessEngineFactoryBean engineFactoryBean(@Autowired SpringProcessEngineConfiguration springProcessEngineConfiguration ){
        ProcessEngineFactoryBean processEngineFactoryBean=new ProcessEngineFactoryBean();
        processEngineFactoryBean.setProcessEngineConfiguration(springProcessEngineConfiguration);
        return processEngineFactoryBean;
    }

    @Bean
    ProcessEngine processEngine(@Autowired ProcessEngineFactoryBean processEngineFactoryBean) throws Exception {
        return processEngineFactoryBean.getObject();
    }

    @Bean
    RepositoryService repositoryService(@Autowired ProcessEngine processEngine){
        return processEngine.getRepositoryService();
    }

    @Bean
    HistoryService historyService(@Autowired ProcessEngine processEngine){
        return processEngine.getHistoryService();
    }

    @Bean
    FormService formService(@Autowired ProcessEngine processEngine){
        return processEngine.getFormService();
    }

    @Bean
    TaskService taskService(@Autowired ProcessEngine processEngine){
        return processEngine.getTaskService();
    }

    @Bean
    ManagementService managementService(@Autowired ProcessEngine processEngine){
        return processEngine.getManagementService();
    }

    @Bean
    RuntimeService runtimeService(@Autowired ProcessEngine processEngine){
        return processEngine.getRuntimeService();
    }
    @Bean
    IdentityService identityService(@Autowired ProcessEngine processEngine){
        return processEngine.getIdentityService();
    }


    @Bean
    @ConditionalOnProperty(prefix = "dyna",value = "mode",havingValue = "dev", matchIfMissing =true)
    SystemInit systemInit(@Autowired JDBCService jdbcService, @Autowired ClasspathViewLoader loader) throws IOException {
        SystemInit systemInit=new SystemInit();
        systemInit.setInitData(initData);
        systemInit.setInitView(initView);
        systemInit.setDataFiles(Arrays.asList(dataFiles.split(";")));
        systemInit.setViewLoader(loader);
        systemInit.setJdbcService(jdbcService);
        systemInit.init();
        return systemInit;
    }

}
