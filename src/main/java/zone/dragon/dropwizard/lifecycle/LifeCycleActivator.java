package zone.dragon.dropwizard.lifecycle;

import io.dropwizard.lifecycle.JettyManaged;
import org.eclipse.jetty.server.Server;
import org.glassfish.hk2.api.ServiceLocator;
import zone.dragon.dropwizard.ComponentActivator;

import javax.inject.Inject;

/**
 * Activates {@link InjectableLifeCycle}, {@link InjectableLifeCycleListener}, and {@link InjectableManaged} components registered with
 * Jersey and adds them to Jetty
 *
 * @author Bryan Harclerode
 */
public class LifeCycleActivator extends ComponentActivator {
    private Server container;

    @Inject
    public LifeCycleActivator(ServiceLocator locator, Server server) {
        super(locator);
        if (server == null) {
            throw new NullPointerException("server is marked non-null but is null");
        }
        container = server;
    }

    @Override
    protected void activateComponents() {
        activate(InjectableContainerListener.class, (name, component) -> container.addEventListener(component));
        activate(InjectableManaged.class, (name, component) -> container.addBean(new JettyManaged(component)));
        activate(InjectableLifeCycle.class, (name, component) -> container.addBean(component));
        activate(InjectableLifeCycleListener.class, (name, component) -> {
            container.addLifeCycleListener(component);
            // synthesize starting and started event since we've already begun.
            component.lifeCycleStarting(container);
        });
        activate(InjectableServerLifecycleListener.class, (name, component) -> component.serverStarted(container));
    }
}
