package zone.dragon.dropwizard.health;

import com.codahale.metrics.health.HealthCheckRegistry;
import org.glassfish.hk2.api.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zone.dragon.dropwizard.ComponentActivator;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Activates and initializes all {@link InjectableHealthCheck health checks} registered with Jersey and adds them to DropWizard's
 * {@link HealthCheckRegistry}.
 *
 * @author Bryan Harclerode
 */
public class HealthCheckActivator extends ComponentActivator {
    private static final Logger log = LoggerFactory.getLogger(HealthCheckActivator.class);
    private final HealthCheckRegistry registry;

    @Inject
    public HealthCheckActivator(ServiceLocator locator, HealthCheckRegistry registry) {
        super(locator);
        if (registry == null) {
            throw new NullPointerException("registry is marked non-null but is null");
        }
        this.registry = registry;
    }

    @Override
    protected void activateComponents() {
        activate(InjectableHealthCheck.class, (name, component) -> {
            if (name == null) {
                log.warn("Health check {} has no name; Use @Named() to give it one!", component);
                name = String.format("%s.%s", component.getClass().getSimpleName(), UUID.randomUUID());
            }
            log.info("Registering health check {}", name);
            registry.register(name, component);
        });
    }
}
