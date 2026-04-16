package stepDefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.After;

public class Hooks {

    @Before
    public void setup() {
        System.out.println("=== TEST STARTED ===");
    }

    @After
    public void teardown() {
        System.out.println("=== TEST FINISHED ===");
    }
}
