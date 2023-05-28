package io.quarkus.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.config.spi.ConfigSource;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigSourceContext;
import io.smallrye.config.ConfigSourceFactory;
import io.smallrye.config.ConfigValue;
import io.smallrye.config.common.MapBackedConfigSource;

@StaticInitSafe
public class StaticInitConfigSourceFactory implements ConfigSourceFactory {
    @Override
    public Iterable<ConfigSource> getConfigSources(final ConfigSourceContext context) {
        ConfigValue value = context.getValue("skip.build.sources");
        if (value.getValue() != null && value.getValue().equals("true")) {
            return List.of(new MapBackedConfigSource("StaticInitConfigSource", Map.of("config.static.init.my-prop", "1234")) {
            });
        } else {
            return Collections.emptyList();
        }
    }
}
