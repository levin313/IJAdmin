//package com.levin.common.conf;
//
//import com.levin.common.security.UserRealm;
//import org.apache.shiro.mgt.SecurityManager;
//import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
//import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
//import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//
///**
// * Shiro配置Bean
// *
// * @author Levin
// */
//@Configuration
//public class ShiroConf {
//
//    @Bean
//    public UserRealm myShiroRealm() {
//        return new UserRealm();
//    }
//
//    @Bean
//    public SecurityManager securityManager() {
//        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
//        securityManager.setRealm(myShiroRealm());
//        return securityManager;
//    }
//
//    @Bean
//    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
//        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
//        shiroFilterFactoryBean.setSecurityManager(securityManager);
//        //拦截器.
//        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
//        filterChainDefinitionMap.put("/static/**", "anon");
//        filterChainDefinitionMap.put("/common/**", "anon");
//        filterChainDefinitionMap.put("/sys/logout", "logout");
//        filterChainDefinitionMap.put("/**", "authc");
//        shiroFilterFactoryBean.setLoginUrl("/sys/login");
//        shiroFilterFactoryBean.setSuccessUrl("/sys/index");
//
//        //未授权界面;
//        shiroFilterFactoryBean.setUnauthorizedUrl("/error/403");
//        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
//        return shiroFilterFactoryBean;
//    }
//
//    @Bean
//    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
//        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
//        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
//        return authorizationAttributeSourceAdvisor;
//    }
//}
