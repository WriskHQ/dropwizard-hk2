package zone.dragon.dropwizard;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import zone.dragon.dropwizard.health.HealthCheckActivator;
import zone.dragon.dropwizard.lifecycle.LifeCycleActivator;
import zone.dragon.dropwizard.metrics.MetricActivator;
import zone.dragon.dropwizard.task.TaskActivator;

/**
 * Provides integration between DropWizard and HK2, allowing
 *
 * @author Bryan Harclerode
 */
public class HK2Bundle<T> implements ConfiguredBundle<T> {
    @Override
    public void run(T configuration, Environment environment) {
        if (configuration == null) {
            throw new NullPointerException("configuration is marked non-null but is null");
        }
        if (environment == null) {
            throw new NullPointerException("environment is marked non-null but is null");
        }
        environment.jersey().register(new EnvironmentBinder<>(configuration, environment));
        environment.jersey().register(HealthCheckActivator.class);
        environment.jersey().register(MetricActivator.class);
        environment.jersey().register(LifeCycleActivator.class);
        environment.jersey().register(TaskActivator.class);
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }
}
