package cn.codest.minimalconfig.env;

import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.util.StringUtils;

public class RemotePropertySource extends EnumerablePropertySource<RemoteArgs> {

    public RemotePropertySource(String name, RemoteArgs source) {
        super(name, source);
    }

    protected RemotePropertySource(String name) {
        super(name);
    }

    @Override
    public String[] getPropertyNames() {
        return StringUtils.toStringArray(this.source.getOptionNames());
    }

    @Override
    public Object getProperty(String name) {
        return this.source.getOptionValue(name);
    }

}
