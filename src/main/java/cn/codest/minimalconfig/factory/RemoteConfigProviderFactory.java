package cn.codest.minimalconfig.factory;

import cn.codest.minimalconfig.provider.GiteeConfigProvider;
import cn.codest.minimalconfig.provider.RemoteConfigProvider;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.util.Properties;

public class RemoteConfigProviderFactory {

    private ConfigurableEnvironment environment;

    private static final String PROVIDER = "codest.config.provider";

    private static final String GITEE_URL = "codest.config.gitee.url";

    private static final String GITEE_TOKEN = "codest.config.gitee.token";

    public RemoteConfigProviderFactory(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    public Properties getProperties() {
        if (StringUtils.hasLength(environment.getProperty(PROVIDER))) {
            return loadProvider();
        } else if (StringUtils.hasLength(environment.getProperty(GITEE_URL)) && StringUtils.hasLength(environment.getProperty(GITEE_TOKEN))) {
            return new GiteeConfigProvider(environment.getProperty(GITEE_URL), environment.getProperty(GITEE_TOKEN)).load();
        } else {
            return null;
        }
    }

    public Properties loadProvider() {
        try {
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(environment.getProperty(PROVIDER));
            RemoteConfigProvider provider = (RemoteConfigProvider) clazz.getDeclaredConstructor(null).newInstance(null);
            return provider.load();
        } catch (Throwable e) {
            System.err.println(String.format("加载自定义配置类发生错误[%s]", environment.getProperty(PROVIDER)));
            e.printStackTrace();
        }

        return null;
    }

}
