package zone.dragon.dropwizard;

import zone.dragon.dropwizard.task.InjectableTask;

import javax.inject.Inject;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class TestInjectableTask extends InjectableTask {
    private TestConfig config;

    @Inject
    protected TestInjectableTask(TestConfig config) {
        super("test-task");
        this.config = config;
    }

    @Override
    public void execute(Map<String, List<String>> map, PrintWriter printWriter) throws Exception {
        printWriter.println("Executing task " + getName() + ", testProperty: " + config.getTestProperty());
        printWriter.flush();
    }
}
