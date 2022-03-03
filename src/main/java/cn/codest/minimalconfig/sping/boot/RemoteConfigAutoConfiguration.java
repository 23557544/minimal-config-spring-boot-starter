package cn.codest.minimalconfig.sping.boot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RemoteConfigAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SpringValueAnnotationProcessor springValueAnnotationProcessor() {
        return new SpringValueAnnotationProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public RefreshConfigExecutor refreshConfigExecutor() {
        return new RefreshConfigExecutor();
    }

}
