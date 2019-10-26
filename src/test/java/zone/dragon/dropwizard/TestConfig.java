package zone.dragon.dropwizard;

import io.dropwizard.Configuration;

/**
 * @author Bryan Harclerode
 */
public class TestConfig extends Configuration {
    private String testProperty;

    public String getTestProperty() {
        return testProperty;
    }

    public void setTestProperty(String testProperty) {
        this.testProperty = testProperty;
    }

}
