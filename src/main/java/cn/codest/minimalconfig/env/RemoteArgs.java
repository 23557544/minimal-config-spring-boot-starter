package cn.codest.minimalconfig.env;

import org.springframework.lang.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class RemoteArgs {

    private final Properties optionArgs;

    public RemoteArgs(Properties optionArgs) {
        this.optionArgs = optionArgs;
    }

    public Set<String> getOptionNames() {

        return Collections.unmodifiableSet(
                this.optionArgs.keySet().stream().map(String::valueOf).collect(Collectors.toSet())
        );
    }

    public boolean containsOption(String optionName) {
        return this.optionArgs.containsKey(optionName);
    }

    @Nullable
    public Object getOptionValue(String optionName) {
        return this.optionArgs.get(optionName);
    }
}
