package com.moscona.test.util;

import groovy.lang.GroovyClassLoader;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;


/**
 * TestPropertiesHelper Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>May 6, 2014</pre>
 */
public class TestPropertiesHelperTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: getProjectRootPath()
     */
    @Test
    public void testGetProjectRootPath() throws Exception {
        TestResourceHelper helper = new TestResourceHelper("projectRootMaker");
        String root = helper.getProjectRootPath();
        assertTrue("failed root test", (new File(root + "/src/test/groovy")).exists());
        Class.forName("com.moscona.test.easyb.TestHelper");
    }

    @Test
    public void testGetProjectRootPathDefault() throws Exception {
        TestResourceHelper helper = new TestResourceHelper();
        String root = helper.getProjectRootPath();
        assertTrue("failed root test", (new File(root + "/src/test/groovy")).exists());
    }

    @Test
    public void testGetProjectRootPathConst() throws Exception {
        TestResourceHelper helper = new TestResourceHelper(TestResourceHelper.DEFAULT_ROOT_MARKER);
        String root = helper.getProjectRootPath();
        assertTrue("failed root test", (new File(root + "/src/test/groovy")).exists());
    }

    @Test(expected = IOException.class)
    public void testGetProjectRootPathFailed() throws Exception {
        new TestResourceHelper("no such file");
    }
} 
