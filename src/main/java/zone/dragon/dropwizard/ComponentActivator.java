package zone.dragon.dropwizard;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEvent.Type;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import javax.inject.Named;
import java.util.function.BiConsumer;

/**
 * Helper class for initializing custom Jersey components; Implement the {@link #activateComponents()} to call
 * {@link #activate(Class, ComponentConsumer)} and then retrieve/initialize custom components.
 *
 * @author Bryan Harclerode
 */
public abstract class ComponentActivator implements ApplicationEventListener {

    protected interface ComponentConsumer<T> extends BiConsumer<String, T> {
    }

    /**
     * Tries to reflect the name of a class, as defined by the {@link Named @Named} annotation
     *
     * @param implementation Class to inspect for a name
     * @return The annotated name for this class, or {@code null} if it does not have one
     */
    private static String getName(Class<?> implementation) {
        Named named = implementation.getAnnotation(Named.class);
        if (named == null || named.value().isEmpty()) {
            return null;
        }
        return named.value();
    }

    private final ServiceLocator locator;

    /**
     * Finds all Jersey components that provide a specific contract, and invokes a callback with each discovered component to activate it.
     *
     * @param contract The contract that components must implement
     * @param consumer Callback to invoke for each discovered component
     * @param <T>      Type of the component to find and activate
     */
    protected <T> void activate(Class<T> contract, ComponentConsumer<T> consumer) {
        if (contract == null) {
            throw new NullPointerException("contract is marked non-null but is null");
        }
        if (consumer == null) {
            throw new NullPointerException("consumer is marked non-null but is null");
        }
        locator.getAllServiceHandles(contract).forEach(handle -> {
            String name = handle.getActiveDescriptor().getName();
            T service = handle.getService();
            if (name == null) {
                name = getName(service.getClass());
            }
            consumer.accept(name, service);
        });
    }

    /**
     * Called once all Jersey components are ready and bound; The implementation should use {@link #activate(Class, ComponentConsumer)} to
     * load activate supported components.
     */
    protected abstract void activateComponents();

    @Override
    public void onEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent.getType() == Type.INITIALIZATION_START) {
            // Request all implementations of the contract from HK2 and activate them
            activateComponents();
        }
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return null; // no request processing
    }

    public ComponentActivator(final ServiceLocator locator) {
        if (locator == null) {
            throw new NullPointerException("locator is marked non-null but is null");
        }
        this.locator = locator;
    }
}
