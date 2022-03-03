package cn.codest.minimalconfig.property;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SpringValuePropertyStore {

    private static SpringValuePropertyStore instance = null;

    private static MultiValueMap<String, SpringValueProperty> store = null;

    private final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();

    private final Lock READ_LOCK = LOCK.readLock();

    private final Lock WRITE_LOCK = LOCK.writeLock();

    public static synchronized SpringValuePropertyStore getInstance() {
        if (null == instance) {
            instance = new SpringValuePropertyStore();
            store = new LinkedMultiValueMap<>();
        }
        return instance;
    }

    /**
     * 缓存@Value注解的元素
     * @param property
     */
    public void add(SpringValueProperty property) {
        WRITE_LOCK.lock();

        try {
            store.add(property.getKey(), property);
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    /**
     * 判断配置是否发生变化
     * @param key           配置项key
     * @param currentValue  配置项当前value
     * @return
     */
    public Boolean isChange(String key, String currentValue) {
        return StringUtils.hasLength(key)
                && StringUtils.hasLength(currentValue)
                && store.containsKey(key)
                && !Objects.equals(store.getFirst(key).getValue(), currentValue);
    }

    /**
     * 更新所有Field当前属性值
     * @param key           配置项key
     * @param currentValue  配置项当前value
     */
    public void update(String key, String currentValue) {
        WRITE_LOCK.lock();
        try {
            store.get(key).stream().forEach(springValueProperty -> springValueProperty.setValue(currentValue));
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    /**
     * 根据配置项读取所有元素
     * @param key
     * @return
     */
    public List<SpringValueProperty> get(String key) {
        READ_LOCK.lock();

        try {
            return store.get(key);
        } finally {
            READ_LOCK.unlock();
        }
    }


}
