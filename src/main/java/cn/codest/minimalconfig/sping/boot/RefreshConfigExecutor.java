package cn.codest.minimalconfig.sping.boot;

import cn.codest.minimalconfig.factory.RemoteConfigProviderFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.Properties;

public class RefreshConfigExecutor extends RemoteConfigSupport implements BeanFactoryAware, EnvironmentAware {

    private ConfigurableEnvironment environment;

    private ConfigurableBeanFactory beanFactory;

    private SimpleTypeConverter typeConverter;

    private RemoteConfigProviderFactory providerFactory;

    public void execute() {
        Properties current = providerFactory.getProperties();
        // 更新Environment
        addPropertySource(this.environment, current);
        // 更新bean实例的成员变量属性值
        updateField(beanFactory, environment, typeConverter, current);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
        this.typeConverter = new SimpleTypeConverter();
        this.typeConverter.setConversionService(this.beanFactory.getConversionService());
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
        this.providerFactory = new RemoteConfigProviderFactory(this.environment);
    }
}
