package com.github.yuebo.dyna;

import com.github.yuebo.dyna.config.DynaConfigProperties;
import com.github.yuebo.dyna.core.MenuProvider;
import com.github.yuebo.dyna.core.PermissionProvider;
import com.github.yuebo.dyna.utils.FormViewUtils;
import com.github.yuebo.dyna.provider.DefaultMessageProvider;
import com.github.yuebo.dyna.provider.SpringBeanProvider;
import com.github.yuebo.dyna.security.ErrorPageInterceptor;
import com.github.yuebo.dyna.security.SecurityInterceptor;
import com.github.yuebo.dyna.security.SessionStoreToken;
import com.github.yuebo.dyna.security.TokenInterceptor;
import com.github.yuebo.dyna.service.JDBCService;
import org.apache.velocity.tools.generic.EscapeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

import java.util.HashMap;


@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    EscapeTool escapeTool;

    @Autowired
    MenuProvider menuProvider;
    @Autowired
    DefaultMessageProvider defaultMessageProvider;
    @Autowired
    SpringBeanProvider springBeanProvider;

    @Autowired
    FormViewUtils formViewUtils;
    @Autowired
    SecurityInterceptor securityInterceptor;
    @Autowired
    TokenInterceptor tokenInterceptor;
    @Autowired
    ErrorPageInterceptor errorPageInterceptor;

    @Autowired
    DynaConfigProperties dynaConfigProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(securityInterceptor);
        registry.addInterceptor(tokenInterceptor);
        registry.addInterceptor(errorPageInterceptor);
        super.addInterceptors(registry);
    }
    @Bean
    SecurityInterceptor securityInterceptor(@Autowired FormViewUtils formViewUtils, @Autowired JDBCService jdbcService, @Autowired PermissionProvider permissionProvider){
        SecurityInterceptor securityInterceptor= new SecurityInterceptor();
        securityInterceptor.setJdbcService(jdbcService);
        securityInterceptor.setPermissionProvider(permissionProvider);
        securityInterceptor.setFormViewUtils(formViewUtils);
        return securityInterceptor;
    }

    @Bean
    TokenInterceptor tokenInterceptor(@Autowired FormViewUtils formViewUtils){
        TokenInterceptor tokenInterceptor= new TokenInterceptor();
        tokenInterceptor.setFormViewUtils(formViewUtils);
        tokenInterceptor.setSessionStoreToken(sessionStoreToken());
        return tokenInterceptor;
    }
    @Bean
    ErrorPageInterceptor errorPageInterceptor(){
        return new ErrorPageInterceptor();
    }

    @SuppressWarnings("deprecation")
    @Bean
    public VelocityConfigurer velocityConfigurer(ApplicationContext context) {
        VelocityConfigurer config = new VelocityConfigurer();
        config.setConfigLocation(new ClassPathResource("velocity.properties"));

        return config;
    }

    @SuppressWarnings("deprecation")
    @Bean
    public VelocityViewResolver velocityResolver() {
        VelocityViewResolver resolver = new VelocityViewResolver();
        resolver.setCache(false);
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".vm");
        resolver.setContentType("text/html;charset=UTF-8");
        resolver.setExposeSpringMacroHelpers(true);
//        resolver.setExposeRequestAttributes(true);
        resolver.setRequestContextAttribute("requestContext");
        resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 20);
        HashMap<String,Object> attributes=new HashMap();
        attributes.put("esc",escapeTool);
        attributes.put("menu",menuProvider);
        attributes.put("provider",springBeanProvider);
        attributes.put("msg",defaultMessageProvider);
        attributes.put("util",formViewUtils);
        attributes.put("app",dynaConfigProperties);
        resolver.setAttributesMap(attributes);
        return resolver;
    }
    @Bean
    public EscapeTool escapeTool(){
        return new EscapeTool();
    }

    @Bean
    SessionStoreToken sessionStoreToken(){
        SessionStoreToken sessionStoreToken=new SessionStoreToken();
        sessionStoreToken.setTokenSize(10);
        return sessionStoreToken;
    }
}
