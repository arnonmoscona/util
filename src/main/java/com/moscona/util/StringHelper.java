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

// FIXME Unit tests
// FIXME Refactor to make this Java8 friendly
// import com.moscona.dataSpace.IScalar;  // FIXME this needs to be an extension in dataSpace, not here
import com.moscona.exceptions.InvalidArgumentException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


import java.util.*;

/**
 * Created: Jun 15, 2010 4:25:26 PM
 * By: Arnon Moscona
 */
@SuppressWarnings({"UtilityClass"})
public class StringHelper {
    private StringHelper() {

    }

    public static String join(String[] list, String delimiter) {
        return StringUtils.join(list, delimiter);
    }

    public static String join(Collection c, String delimiter) {
        return StringUtils.join(c,delimiter);
    }

    public static String toPaddedString(long i,int length) {
        return StringUtils.leftPad(Long.toString(i),length,'0');
    }

    public static String toPaddedString(long i,int length, char padChar) {
        return StringUtils.leftPad(Long.toString(i),length,padChar);
    }

    public static String toPaddedString(long i) {
        return toPaddedString(i,2);
    }

    public static boolean parseBoolean(String b) throws InvalidArgumentException {
        String strValue = b.trim().toLowerCase();
        if (strValue.equals("true") || strValue.equals("yes") || strValue.equals("1") || strValue.equals("on")) {
            return true;
        }
        if (strValue.equals("false") || strValue.equals("no") || strValue.equals("0") || strValue.equals("off")) {
            return false;
        }
        throw new InvalidArgumentException("The value \""+b+"\" could not be parsed as a boolean");
    }

    /**
     * A generic toString() method for any object. Uses BeanUtils. No guarantees. Consider Lombok instead
     * @param bean the object to make a String for
     * @return whatever it comes up with...
     */
    public static String toString(Object bean) {
        try {
            return ReflectionToStringBuilder.toString(bean, ToStringStyle.SHORT_PREFIX_STYLE);
        }
        catch (Exception e) {
            return "Error generating toString() for "+bean.getClass().getName()+": "+e;
        }
    }

    public static String prettyPrint(double num) {
        if (Math.abs(num) < 0.01) {
            return new Formatter().format("%,.5f", num).toString();
        }
        if (Math.abs(num) < 1.0) {
            return new Formatter().format("%,.3f", num).toString();
        }
        return new Formatter().format("%,.2f", num).toString();
    }

    public static String prettyPrint(long num) {
        return new Formatter().format("%,d", num).toString();
    }

    public static String prettyPrint(Number value) {
        if (value instanceof Double || value instanceof Float) {
            return prettyPrint(value.doubleValue());
        }
        return prettyPrint(value.longValue());
    }

    public static String wrap(String str, int maxWidth) {
        if (str.length() < maxWidth) {
            return str;
        }

        StringBuilder s = new StringBuilder();
        int currentLineLength = 0;
        for (String word: str.trim().split("[ \t]")) {
            if (currentLineLength>0 && currentLineLength+word.length() > maxWidth) {
                s.append("\n");
                currentLineLength = 0;
            }
            if (currentLineLength > 0) {
                s.append(" ");
                currentLineLength++;
            }
            s.append(word);
            currentLineLength += word.length();
        }

        return s.toString();
    }

    // FIXME this needs to be an extension in dataSpace, not here
//    public static String toString(Map<String, IScalar> row, String indent) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("[\n");
//        for (String key: row.keySet()) {
//            sb.append(indent).append("\"").append(key).append("\": ").append(row.get(key)).append("\n");
//        }
//        sb.append("]\n");
//        return sb.toString();
//    }

    public static String[] asArray(List<String> list) {
        String[] arr = new String[list.size()];
        for (int i=0; i<list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    public static String camelCase(String englishName) {
        String[] parts = englishName.split(" +");
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String s: parts) {
            sb.append(isFirst ? s : StringUtils.capitalize(s));
            isFirst = false;
        }
        return sb.toString();
    }

    public static String camelToEnglish(String s) {
        String[] words = StringUtils.splitByCharacterTypeCamelCase(s);
        return StringUtils.join(words," ").toLowerCase();
    }

//    public static String englishToCamel(String findingEnglish) {
//        String[] words = findingEnglish.split(" +");
//        String result = "";
//        boolean isFirst = true;
//        for (String word: words) {
//            result += (isFirst ? word : StringUtils.capitalize(word));
//            isFirst = false;
//        }
//        return result;
//    }
}
