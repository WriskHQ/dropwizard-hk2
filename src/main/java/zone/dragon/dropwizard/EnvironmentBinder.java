package zone.dragon.dropwizard;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.Configuration;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.component.AbstractLifeCycle.AbstractLifeCycleListener;
import org.eclipse.jetty.util.component.LifeCycle;
import org.glassfish.jersey.internal.inject.AbstractBinder;

import javax.validation.Validator;

/**
 * This binder makes much of the DropWizard environment available to HK2 to be injected into components that request it at runtime.
 * Specifically, the following components are bound: <ul> <li>{@link Environment}</li> <li>{@link HealthCheckRegistry}</li> <li>{@link
 * LifecycleEnvironment}</li> <li>{@link MetricRegistry}</li> <li>{@link Configuration}</li>
 * <li>{@link ObjectMapper}</li> <li>{@link Server}</li><li>{@link Validator}</li></ul>
 *
 * @author Bryan Harclerode
 */
public class EnvironmentBinder<T> extends AbstractBinder {
    private final T configuration;
    private final Environment environment;
    private Server server;

    /**
     * Creates a new binder that exposes the DropWizard environment to HK2
     *
     * @param configuration
     *     DropWizard configuration
     * @param environment
     *     DropWizard environment
     */
    public EnvironmentBinder(T configuration,  Environment environment) {
        if (configuration == null) {
            throw new NullPointerException("configuration is marked non-null but is null");
        }
        if (environment == null) {
            throw new NullPointerException("environment is marked non-null but is null");
        }
        this.configuration = configuration;
        this.environment = environment;
        environment.lifecycle().addLifeCycleListener(new AbstractLifeCycleListener() {
            @Override
            public void lifeCycleStarting(LifeCycle event) {
                if (event instanceof Server) {
                    server = (Server) event;
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void configure() {
        bind(environment).to(Environment.class);
        bind(environment.healthChecks()).to(HealthCheckRegistry.class);
        bind(environment.lifecycle()).to(LifecycleEnvironment.class);
        bind(environment.metrics()).to(MetricRegistry.class);
        bind(environment.getValidator()).to(Validator.class);
        bind(configuration).to((Class) configuration.getClass()).to(Configuration.class);
        bind(server).to(Server.class);
        bind(environment.getObjectMapper()).to(ObjectMapper.class);
    }
}
