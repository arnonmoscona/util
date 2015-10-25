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

package com.moscona.util.transformation;

import com.moscona.exceptions.InvalidArgumentException;
import com.moscona.util.TimeHelper;

/**
 * Created: Mar 18, 2010 2:22:05 PM
 * By: Arnon Moscona
 * A utility class to help with conversions to and from byte arrays
 */
@SuppressWarnings({"UtilityClass", "MagicNumber"})
public class ByteArrayHelper {
    private ByteArrayHelper() {
    }

    private static byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }

    public static int byteArrayToInt(byte[] bytes) throws InvalidArgumentException {
        byte[] b = padLeft(bytes, 4);
        return (b[0] << 24)
                + ((b[1] & 0xFF) << 16)
                + ((b[2] & 0xFF) << 8)
                + (b[3] & 0xFF);
    }

    private static byte[] padLeft(byte[] bytes, int length) throws InvalidArgumentException {
        if (bytes.length == length) {
            return bytes; // nothing to do
        }
        else if (bytes.length > length) {
            throw new InvalidArgumentException("argument too long (more bytes than required length)");
        }
        byte[] retval = new byte[length];  // default initialization is to zeros
        int bytesLength = bytes.length;

        for (int i=0; i< bytesLength; i++) {
            retval[length-1-i] = bytes[bytesLength-1-i]; // fill up right to left
        }

        return retval;
    }

    public static byte[] intToBytes(int value, int length) throws InvalidArgumentException {
        if (length<1 || length>4) {
            throw new InvalidArgumentException("Invalid length. Got "+length+", which is not between 1 and 4");
        }

        byte[] retval = new byte[length];
        byte[] arr = intToByteArray(value);
        for(int i=0; i<=3; i++) {
            byte nextByte = arr[3-i];

            if (i<length) {
                // populate return value
                retval[length-1-i] = nextByte; // copy from tail of array
            } else {
                // check for overflow
                if (nextByte !=0) {
                    throw new InvalidArgumentException("Argument "+value+" out of bounds for conversion to byte["+length+"]");
                }
            }
        }


        return retval;
    }

    public static byte[] floatToBytes(float price, int length, int factor) throws InvalidArgumentException {
        int intValue = (int) (price*factor);
        return intToBytes(intValue, length);
    }

    public static float bytesToFloat(byte[] bytes, int factor) throws InvalidArgumentException {
        int intValue = byteArrayToInt(bytes);
        return ((float)intValue) / factor;
    }

    public static String convertToString(byte[] bytes) throws InvalidArgumentException {
        if (bytes.length != 4) {
            throw new InvalidArgumentException("Can only convert byte[4] as timestamps");
        }

        return TimeHelper.convertToString(ByteArrayHelper.byteArrayToInt(bytes));
    }
}

