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

package com.moscona.util;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


/**
 * StringHelper Tester.
 *
 * @author Arnon Moscona
 * @version 1.0
 * @since <pre>Feb 10, 2014</pre>
 */
public class StringHelperTest {

    // FIXME these tests are very partial. Need to add many more...

    private static class Bean {
        private int i = -1;
        private double d = Math.PI;
        private String s = "name";

        public Bean() {}

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        public double getD() {
            return d;
        }

        public void setD(double d) {
            this.d = d;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }
    }

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: join(String[] list, String delimiter)
     */
    @Test
    public void testJoinForListDelimiter() throws Exception {
        String result = StringHelper.join(new String[]{"1", "2", "3"}, ", ");
        assertEquals("Should match commons", "1, 2, 3", result);
    }

    /**
     * Method: join(Collection c, String delimiter)
     */
    @Test
    public void testJoinForCDelimiter() throws Exception {
        String result = StringHelper.join(Arrays.asList("1", "2", "3"), ", ");
        assertEquals("Should match commons", "1, 2, 3", result);
    }

    /**
     * Method: toPaddedString(long i, int length)
     */
    @Test
    public void testToPaddedStringForILength() throws Exception {
        String result = StringHelper.toPaddedString(123, 5);
        assertEquals("00123", result);
    }

    /**
     * Method: toPaddedString(long i, int length, char padChar)
     */
    @Test
    public void testToPaddedStringForILengthPadChar() throws Exception {
        String result = StringHelper.toPaddedString(123, 5, ' ');
        assertEquals("  123", result);
    }

    /**
     * Method: toPaddedString(long i)
     */
    @Test
    public void testToPaddedStringI() throws Exception {
        String result = StringHelper.toPaddedString(1);
        assertEquals("01", result);
    }

    /**
     * Method: parseBoolean(String b)
     */
    @Test
    public void testParseBoolean() throws Exception {
        for (String t: Arrays.asList("true", "True", "TRUE", "1", "on", "On", "ON", "yes", "Yes", "YES")) {
            assertTrue("\""+t+"\" should be true", StringHelper.parseBoolean(t));
        }
        for (String f: Arrays.asList("false", "False", "FALSE", "0", "off", "Off", "OFF", "no", "No", "NO")) {
            assertFalse("\""+f+"\" should be false", StringHelper.parseBoolean(f));
        }
    }

    /**
     * Method: toString(Object bean)
     */
    @Test
    public void testToString() throws Exception {
        String result = StringHelper.toString(new Bean());
        assertEquals("Bean representation incorrect", "StringHelperTest.Bean[i=-1,d=3.141592653589793,s=name]", result);
    }

    /**
     * Method: prettyPrint(double num)
     */
    @Test
    public void testPrettyPrintNum() throws Exception {
        assertEquals("Double pretty print incorrect", "3.14", StringHelper.prettyPrint(Math.PI));
        assertEquals("Large double pretty print incorrect", "3,141.59", StringHelper.prettyPrint(Math.PI*1000));
        assertEquals("Large integer pretty print incorrect", "1,024", StringHelper.prettyPrint(1024));
    }

    /**
     * Method: prettyPrint(Number value)
     */
    @Test
    public void testPrettyPrintValue() throws Exception {
        assertEquals("Number pretty print incorrect", "3.14", StringHelper.prettyPrint(new Double(Math.PI)));
    }

    /**
     * Method: wrap(String str, int maxWidth)
     */
    @Test
    public void testWrap() throws Exception {
        assertEquals("incorrect string wrap1", "123\n456\n7", StringHelper.wrap("123 456 7",3));
        assertEquals("incorrect string wrap2", "1 2\n3\n456\n7", StringHelper.wrap("1 2\t3 456 7",3));
        assertEquals("incorrect string wrap3", "123\n456\n789xxx", StringHelper.wrap("123 456 789xxx",3));
    }

    /**
     * Method: asArray(List<String> list)
     */
    @Test
    public void testAsArray() throws Exception {
        String[] original = new String[] {"1","2","3"};
        List<String> list = Arrays.asList(original);
        String[] result = StringHelper.asArray(list);
        assertArrayEquals("toArray failed", original, result);
    }

    /**
     * Method: camelCase(String englishName)
     */
    @Test
    public void testCamelCase() throws Exception {
        assertEquals("camelCase", StringHelper.camelCase("camel case"));
    }

    /**
     * Method: camelToEnglish(String s)
     */
    @Test
    public void testCamelToEnglish() throws Exception {
        assertEquals("camel case", StringHelper.camelToEnglish("camelCase"));
        assertEquals("camel case", StringHelper.camelToEnglish("CamelCase"));
    }

    /**
     * Method: englishToCamel(String findingEnglish)
     */
//    @Test
//    public void testEnglishToCamel() throws Exception {
//        assertEquals("camelCase", StringHelper.englishToCamel("camel case"));
//    }


} 
