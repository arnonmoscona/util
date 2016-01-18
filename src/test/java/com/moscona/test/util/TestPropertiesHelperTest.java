/*
 * Copyright (c) 2015. Arnon Moscona
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
    // FIXME everything here must be reviewed to see if it's still needed

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
        TestResourceHelper helper = new TestResourceHelper("projectRootMaker"); //fixme this is a major bug - get rid of it
        String root = helper.getProjectRootPath();
        assertTrue("failed root test", (new File(root + "/src/test/groovy")).exists());
//        Class.forName("com.moscona.test.easyb.TestHelper");
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
