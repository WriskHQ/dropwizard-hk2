package zone.dragon.dropwizard.task;

import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;
import zone.dragon.dropwizard.ComponentActivator;

import javax.inject.Inject;

/**
 * Activates and initializes all {@link io.dropwizard.servlets.tasks.Task tasks} registered with Jersey and adds them to DropWizard.
 */
public class TaskActivator extends ComponentActivator {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TaskActivator.class);
    private final Environment environment;

    @Inject
    public TaskActivator(ServiceLocator locator, Environment environment) {
        super(locator);
        if (locator == null) {
            throw new java.lang.NullPointerException("locator is marked non-null but is null");
        }
        if (environment == null) {
            throw new java.lang.NullPointerException("environment is marked non-null but is null");
        }
        this.environment = environment;
    }

    @Override
    protected void activateComponents() {
        activate(InjectableTask.class, (name, component) -> {
            log.info("Registering task {}", component.getName());
            environment.admin().addTask(component);
        });
    }
}
