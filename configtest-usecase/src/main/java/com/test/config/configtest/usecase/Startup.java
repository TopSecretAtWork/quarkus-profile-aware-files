package com.test.config.configtest.usecase;

import io.quarkus.runtime.StartupEvent;
import io.smallrye.config.SmallRyeConfig;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.List;
import java.util.stream.StreamSupport;

@ApplicationScoped
public class Startup
{

    private static final Logger LOG = LoggerFactory.getLogger(Startup.class);

    void onStart(@Observes StartupEvent startupEvent)
    {
        Config config = ConfigProvider.getConfig();

        List<String> propertyNames = StreamSupport.stream(config.getPropertyNames().spliterator(), false)
                                                  .filter(x -> x.startsWith("services"))
                                                  .toList();

        LOG.info("Config property names: '{}'", propertyNames);

        for (String propertyName : propertyNames)
        {
            LOG.info("name: '{}' -> value: '{}'; (source: {})", propertyName, config.getConfigValue(propertyName).getValue(), config.getConfigValue(propertyName));
        }

        SmallRyeConfig smallRyeConfig = (SmallRyeConfig) config;

        List<String> profiles = smallRyeConfig.getProfiles();
        LOG.info("Config profiles: {}", profiles);

        ConfigSource configSource = StreamSupport.stream(config.getConfigSources().spliterator(), false)
                                                  .filter(x -> x.getName().contains("Specified default values"))
                                                  .findFirst().orElseGet(() -> null);

        if (configSource != null)
        {
            LOG.info("Default config source (class: {}): {}", configSource.getClass(), configSource.getProperties());
        }
    }
}
