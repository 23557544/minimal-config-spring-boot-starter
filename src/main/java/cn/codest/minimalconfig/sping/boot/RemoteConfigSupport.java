package cn.codest.minimalconfig.sping.boot;

import cn.codest.minimalconfig.env.RemoteArgs;
import cn.codest.minimalconfig.env.RemotePropertySource;
import cn.codest.minimalconfig.property.SpringValueProperty;
import cn.codest.minimalconfig.property.SpringValuePropertyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class RemoteConfigSupport {

    private static final Logger log = LoggerFactory.getLogger(RemoteConfigSupport.class);

    public static final String BOOTSTRAP_PROPERTY_SOURCE_NAME = "remoteConfiguration";

    /**
     * 向环境变量中增加PropertySource
     * @param environment
     * @param properties
     */
    protected void addPropertySource(ConfigurableEnvironment environment, @NonNull Properties properties) {
        RemoteArgs remoteArgs = new RemoteArgs(properties);
        RemotePropertySource remotePropertySource = new RemotePropertySource(BOOTSTRAP_PROPERTY_SOURCE_NAME, remoteArgs);

        environment.getPropertySources().addFirst(remotePropertySource);
    }

    /**
     * 更新实例化Bean中的成员变量属性值
     * @param beanFactory
     * @param environment
     * @param typeConverter 类型转换器，将配置值String转换为属性对应的类型值
     * @param properties    配置中心读取的最新配置项
     */
    protected void updateField(ConfigurableBeanFactory beanFactory, ConfigurableEnvironment environment, TypeConverter typeConverter, Properties properties) {
        // 找到发生变化的配置项
        Set<String> changed = findChanges(properties);

        if (CollectionUtils.isEmpty(changed)) {
            return ;
        }

        PropertySourcesPropertyResolver resolver = new PropertySourcesPropertyResolver(environment.getPropertySources());
        Scope scope;
        Object typeValue;

        // 遍历所有发生改变的配置项
        for (String key : changed) {
            // 获取配置项对应的bean实例和field
            for (SpringValueProperty springValueProperty : SpringValuePropertyStore.getInstance().get(key)) {
                // 获取bean的scope
                scope = beanFactory.getRegisteredScope(beanFactory.getMergedBeanDefinition(springValueProperty.getBeanName()).getScope());
                // 解析@Value表达式，通过environment获取属性值，并根据SpEL表达式进行运算
                typeValue = beanFactory.getBeanExpressionResolver().evaluate(resolver.resolvePlaceholders(springValueProperty.getExpress()), new BeanExpressionContext(beanFactory, scope));

                springValueProperty.getField().setAccessible(Boolean.TRUE);
                try {
                    // 根据Field类型进行转换
                    // 转换后赋值
                    springValueProperty.getField().set(springValueProperty.getBean(), typeConverter.convertIfNecessary(typeValue, springValueProperty.getField().getType(), new TypeDescriptor(springValueProperty.getField())));
                } catch (IllegalAccessException e) {
                    log.error("更新变量属性值发生错误", e);
                }
            }
            // 更新缓存
            SpringValuePropertyStore.getInstance().update(key, properties.getProperty(key));
        }
        log.info("配置项已刷新");
    }

    private Set<String> findChanges(Properties properties) {
        return properties.keySet().stream()
                .map(String::valueOf)
                .filter(key -> SpringValuePropertyStore.getInstance().isChange(key, properties.getProperty(key)))
                .collect(Collectors.toSet());
    }



}
