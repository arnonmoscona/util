package com.moscona;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.*;
import org.junit.runner.RunWith;


/**
 * Created: 1/20/14 2:01 PM
 * By: Arnon Moscona
 */
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources", glue = "com.moscona.util")
public class RunCucumberTest {
}
