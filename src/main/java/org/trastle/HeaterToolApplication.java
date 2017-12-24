package org.trastle;

import com.github.trastle.haverlandsmartwaveclient.HaverlandSmartwaveClient;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Duration;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.MetricsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trastle.haverlandmetrics.HaverlandScraper;
import org.trastle.managed.HaverlandScraperTaskScheduler;

public class HeaterToolApplication extends Application<HeaterToolConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeaterToolApplication.class);

    public static void main(final String[] args) throws Exception {
        new HeaterToolApplication().run(args);
    }

    @Override
    public String getName() {
        return "HeaterTool";
    }

    @Override
    public void initialize(final Bootstrap<HeaterToolConfiguration> bootstrap) {

        // See: http://www.dropwizard.io/1.2.0/docs/manual/core.html#environment-variables
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
    }

    @Override
    public void run(final HeaterToolConfiguration configuration, final Environment environment) {
        LOGGER.info("Starting HeaterTool...");

        HaverlandScraper task = new HaverlandScraper(
                configuration.getHaverland().getUsername(),
                configuration.getHaverland().getPassword());

        HaverlandScraperTaskScheduler hcts = new HaverlandScraperTaskScheduler(task, Duration.minutes(1L));
        environment.lifecycle().manage(hcts);

        environment.servlets()
                .addServlet("heaterMetrics", new MetricsServlet(CollectorRegistry.defaultRegistry))
                .addMapping("/metrics");

        LOGGER.info("Started HeaterTool.");
    }

}
