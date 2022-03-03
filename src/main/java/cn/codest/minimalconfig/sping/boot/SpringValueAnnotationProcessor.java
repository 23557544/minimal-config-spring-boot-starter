package cn.codest.minimalconfig.sping.boot;

import cn.codest.minimalconfig.property.PlaceholderHelper;
import cn.codest.minimalconfig.property.SpringValueProperty;
import cn.codest.minimalconfig.property.SpringValuePropertyStore;
import org.springframework.beans.BeansException;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.util.ReflectionUtils;

import java.util.Optional;

public class SpringValueAnnotationProcessor implements BeanPostProcessor, BeanFactoryAware, EnvironmentAware {

    private ConfigurableEnvironment environment;

    private ConfigurableBeanFactory beanFactory;

    private PlaceholderHelper helper = new PlaceholderHelper();

    private SimpleTypeConverter typeConverter;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        PropertySourcesPropertyResolver resolver = new PropertySourcesPropertyResolver(environment.getPropertySources());
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            Value value = field.getAnnotation(Value.class);
            if (Optional.ofNullable(value).isPresent()) {
                // 缓存@Value标记的Field
                //  获取配置项Key
                String key = helper.extractPlaceholderKeys(value.value()).stream().findFirst().get();
                SpringValuePropertyStore.getInstance().add(new SpringValueProperty(
                        value.value(),
                        key,
                        environment.getProperty(key),
                        beanName,
                        bean,
                        field
                ));

//                System.out.println(value.value());
//                System.out.println(helper.extractPlaceholderKeys(value.value()).toString());
//
//                System.out.println(resolver.resolvePlaceholders(value.value()));
//
//                Scope scope = beanFactory.getRegisteredScope(beanFactory.getMergedBeanDefinition(beanName).getScope());
//                Object typeValue = beanFactory.getBeanExpressionResolver().evaluate(resolver.resolvePlaceholders(value.value()), new BeanExpressionContext(beanFactory, scope));
//                System.out.println(typeConverter.convertIfNecessary(typeValue, field.getType(), new TypeDescriptor(field)));
            }
        });
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
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
    }
}
