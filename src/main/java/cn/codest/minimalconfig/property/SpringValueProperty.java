package cn.codest.minimalconfig.property;

import java.io.Serializable;
import java.lang.reflect.Field;

public class SpringValueProperty implements Serializable {

    private static final long serialVersionUID = -8956519848116609175L;

    /**
     * @Value注解中的表达式
     */
    private String express;

    private String key;

    private String value;

    private String beanName;

    private Object bean;

    private Field field;

    public SpringValueProperty(String express, String key, String value, String beanName, Object bean, Field field) {
        this.express = express;
        this.key = key;
        this.value = value;
        this.beanName = beanName;
        this.bean = bean;
        this.field = field;
    }

    public String getExpress() {
        return express;
    }

    public void setExpress(String express) {
        this.express = express;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }
}
