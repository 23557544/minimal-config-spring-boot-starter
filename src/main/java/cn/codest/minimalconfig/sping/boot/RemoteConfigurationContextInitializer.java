package cn.codest.minimalconfig.sping.boot;

import cn.codest.minimalconfig.factory.RemoteConfigProviderFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Optional;
import java.util.Properties;

public class RemoteConfigurationContextInitializer extends RemoteConfigSupport implements
        ApplicationContextInitializer<ConfigurableApplicationContext>, EnvironmentPostProcessor, Ordered {

    private static final Integer INIT_ORDER = 0;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        initialize(environment);
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        initialize(applicationContext.getEnvironment());
    }

    protected void initialize(ConfigurableEnvironment environment) {
        // 防止重复初始化
        if (environment.getPropertySources().contains(BOOTSTRAP_PROPERTY_SOURCE_NAME)) {
            return ;
        }

        Properties properties = new RemoteConfigProviderFactory(environment).getProperties();
        if (Optional.ofNullable(properties).isPresent()) {
            addPropertySource(environment, properties);
        }
    }

    @Override
    public int getOrder() {
        return INIT_ORDER;
    }
}
