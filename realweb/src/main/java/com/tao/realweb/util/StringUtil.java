/**
 * $Revision: 13635 $
 * $Date: 2013-05-04 15:55:48 +0800 (周六, 04 五月 2013) $
 *
 * Copyright (C) 2004-2008 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tao.realweb.util;


import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.BreakIterator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to peform common String manipulation algorithms.
 */
public class StringUtil {

	private static final Logger Log = LoggerFactory.getLogger(StringUtil.class);

    // Constants used by escapeHTMLTags
    private static final char[] QUOTE_ENCODE = "&quot;".toCharArray();
    private static final char[] AMP_ENCODE = "&amp;".toCharArray();
    private static final char[] LT_ENCODE = "&lt;".toCharArray();
    private static final char[] GT_ENCODE = "&gt;".toCharArray();

    private StringUtil() {
        // Not instantiable.
    }

    /**
     * Replaces all instances of oldString with newString in string.
     *
     * @param string the String to search to perform replacements on.
     * @param oldString the String that should be replaced by newString.
     * @param newString the String that will replace all instances of oldString.
     * @return a String will all instances of oldString replaced by newString.
     */
    public static String replace(String string, String oldString, String newString) {
        if (string == null) {
            return null;
        }
        int i = 0;
        // Make sure that oldString appears at least once before doing any processing.
        if ((i = string.indexOf(oldString, i)) >= 0) {
            // Use char []'s, as they are more efficient to deal with.
            char[] string2 = string.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuilder buf = new StringBuilder(string2.length);
            buf.append(string2, 0, i).append(newString2);
            i += oLength;
            int j = i;
            // Replace all remaining instances of oldString with newString.
            while ((i = string.indexOf(oldString, i)) > 0) {
                buf.append(string2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }
            buf.append(string2, j, string2.length - j);
            return buf.toString();
        }
        return string;
    }

    /**
     * Replaces all instances of oldString with newString in line with the
     * added feature that matches of newString in oldString ignore case.
     *
     * @param line      the String to search to perform replacements on
     * @param oldString the String that should be replaced by newString
     * @param newString the String that will replace all instances of oldString
     * @return a String will all instances of oldString replaced by newString
     */
    public static String replaceIgnoreCase(String line, String oldString,
                                                 String newString) {
        if (line == null) {
            return null;
        }
        String lcLine = line.toLowerCase();
        String lcOldString = oldString.toLowerCase();
        int i = 0;
        if ((i = lcLine.indexOf(lcOldString, i)) >= 0) {
            char[] line2 = line.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuilder buf = new StringBuilder(line2.length);
            buf.append(line2, 0, i).append(newString2);
            i += oLength;
            int j = i;
            while ((i = lcLine.indexOf(lcOldString, i)) > 0) {
                buf.append(line2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }
            buf.append(line2, j, line2.length - j);
            return buf.toString();
        }
        return line;
    }

    /**
     * Replaces all instances of oldString with newString in line with the
     * added feature that matches of newString in oldString ignore case.
     * The count paramater is set to the number of replaces performed.
     *
     * @param line      the String to search to perform replacements on
     * @param oldString the String that should be replaced by newString
     * @param newString the String that will replace all instances of oldString
     * @param count     a value that will be updated with the number of replaces
     *                  performed.
     * @return a String will all instances of oldString replaced by newString
     */
    public static String replaceIgnoreCase(String line, String oldString,
            String newString, int[] count)
    {
        if (line == null) {
            return null;
        }
        String lcLine = line.toLowerCase();
        String lcOldString = oldString.toLowerCase();
        int i = 0;
        if ((i = lcLine.indexOf(lcOldString, i)) >= 0) {
            int counter = 1;
            char[] line2 = line.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuilder buf = new StringBuilder(line2.length);
            buf.append(line2, 0, i).append(newString2);
            i += oLength;
            int j = i;
            while ((i = lcLine.indexOf(lcOldString, i)) > 0) {
                counter++;
                buf.append(line2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }
            buf.append(line2, j, line2.length - j);
            count[0] = counter;
            return buf.toString();
        }
        return line;
    }

    /**
     * Replaces all instances of oldString with newString in line.
     * The count Integer is updated with number of replaces.
     *
     * @param line the String to search to perform replacements on.
     * @param oldString the String that should be replaced by newString.
     * @param newString the String that will replace all instances of oldString.
     * @return a String will all instances of oldString replaced by newString.
     */
    public static String replace(String line, String oldString,
            String newString, int[] count)
    {
        if (line == null) {
            return null;
        }
        int i = 0;
        if ((i = line.indexOf(oldString, i)) >= 0) {
            int counter = 1;
            char[] line2 = line.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuilder buf = new StringBuilder(line2.length);
            buf.append(line2, 0, i).append(newString2);
            i += oLength;
            int j = i;
            while ((i = line.indexOf(oldString, i)) > 0) {
                counter++;
                buf.append(line2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }
            buf.append(line2, j, line2.length - j);
            count[0] = counter;
            return buf.toString();
        }
        return line;
    }

    /**
     * This method takes a string and strips out all tags except <br> tags while still leaving
     * the tag body intact.
     *
     * @param in the text to be converted.
     * @return the input string with all tags removed.
     */
    public static String stripTags(String in) {
        if (in == null) {
            return null;
        }
        char ch;
        int i = 0;
        int last = 0;
        char[] input = in.toCharArray();
        int len = input.length;
        StringBuilder out = new StringBuilder((int)(len * 1.3));
        for (; i < len; i++) {
            ch = input[i];
            if (ch > '>') {
            }
            else if (ch == '<') {
                if (i + 3 < len && input[i + 1] == 'b' && input[i + 2] == 'r' && input[i + 3] == '>') {
                    i += 3;
                    continue;
                }
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
            }
            else if (ch == '>') {
                last = i + 1;
            }
        }
        if (last == 0) {
            return in;
        }
        if (i > last) {
            out.append(input, last, i - last);
        }
        return out.toString();
    }

    /**
     * This method takes a string which may contain HTML tags (ie, &lt;b&gt;,
     * &lt;table&gt;, etc) and converts the '&lt'' and '&gt;' characters to
     * their HTML escape sequences. It will also replace LF  with &lt;br&gt;.
     *
     * @param in the text to be converted.
     * @return the input string with the characters '&lt;' and '&gt;' replaced
     *         with their HTML escape sequences.
     */
    public static String escapeHTMLTags(String in) {
    	return escapeHTMLTags(in, true);
    }

    /**
     * This method takes a string which may contain HTML tags (ie, &lt;b&gt;,
     * &lt;table&gt;, etc) and converts the '&lt'' and '&gt;' characters to
     * their HTML escape sequences.
     *
     * @param in the text to be converted.
     * @param includeLF set to true to replace \n with <br>.
     * @return the input string with the characters '&lt;' and '&gt;' replaced
     *         with their HTML escape sequences.
     */
    public static String escapeHTMLTags(String in, boolean includeLF) {
        if (in == null) {
            return null;
        }
        char ch;
        int i = 0;
        int last = 0;
        char[] input = in.toCharArray();
        int len = input.length;
        StringBuilder out = new StringBuilder((int)(len * 1.3));
        for (; i < len; i++) {
            ch = input[i];
            if (ch > '>') {
            }
            else if (ch == '<') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(LT_ENCODE);
            }
            else if (ch == '>') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(GT_ENCODE);
            }
            else if (ch == '\n' && includeLF == true) {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append("<br>");
            }
        }
        if (last == 0) {
            return in;
        }
        if (i > last) {
            out.append(input, last, i - last);
        }
        return out.toString();
    }

    /**
     * Used by the hash method.
     */
    private static Map<String, MessageDigest> digests =
            new ConcurrentHashMap<String, MessageDigest>();

    /**
     * Hashes a String using the Md5 algorithm and returns the result as a
     * String of hexadecimal numbers. This method is synchronized to avoid
     * excessive MessageDigest object creation. If calling this method becomes
     * a bottleneck in your code, you may wish to maintain a pool of
     * MessageDigest objects instead of using this method.
     * <p/>
     * A hash is a one-way function -- that is, given an
     * input, an output is easily computed. However, given the output, the
     * input is almost impossible to compute. This is useful for passwords
     * since we can store the hash and a hacker will then have a very hard time
     * determining the original password.
     * <p/>
     * In Jive, every time a user logs in, we simply
     * take their plain text password, compute the hash, and compare the
     * generated hash to the stored hash. Since it is almost impossible that
     * two passwords will generate the same hash, we know if the user gave us
     * the correct password or not. The only negative to this system is that
     * password recovery is basically impossible. Therefore, a reset password
     * method is used instead.
     *
     * @param data the String to compute the hash of.
     * @return a hashed version of the passed-in String
     */
    public static String hash(String data) {
        return hash(data, "MD5");
    }

    /**
     * Hashes a String using the specified algorithm and returns the result as a
     * String of hexadecimal numbers. This method is synchronized to avoid
     * excessive MessageDigest object creation. If calling this method becomes
     * a bottleneck in your code, you may wish to maintain a pool of
     * MessageDigest objects instead of using this method.
     * <p/>
     * A hash is a one-way function -- that is, given an
     * input, an output is easily computed. However, given the output, the
     * input is almost impossible to compute. This is useful for passwords
     * since we can store the hash and a hacker will then have a very hard time
     * determining the original password.
     * <p/>
     * In Jive, every time a user logs in, we simply
     * take their plain text password, compute the hash, and compare the
     * generated hash to the stored hash. Since it is almost impossible that
     * two passwords will generate the same hash, we know if the user gave us
     * the correct password or not. The only negative to this system is that
     * password recovery is basically impossible. Therefore, a reset password
     * method is used instead.
     *
     * @param data the String to compute the hash of.
     * @param algorithm the name of the algorithm requested.
     * @return a hashed version of the passed-in String
     */
    public static String hash(String data, String algorithm) {
        try {
            return hash(data.getBytes("UTF-8"), algorithm);
        }
        catch (UnsupportedEncodingException e) {
            Log.error(e.getMessage(), e);
        }
        return data;
    }

    /**
     * Hashes a byte array using the specified algorithm and returns the result as a
     * String of hexadecimal numbers. This method is synchronized to avoid
     * excessive MessageDigest object creation. If calling this method becomes
     * a bottleneck in your code, you may wish to maintain a pool of
     * MessageDigest objects instead of using this method.
     * <p/>
     * A hash is a one-way function -- that is, given an
     * input, an output is easily computed. However, given the output, the
     * input is almost impossible to compute. This is useful for passwords
     * since we can store the hash and a hacker will then have a very hard time
     * determining the original password.
     * <p/>
     * In Jive, every time a user logs in, we simply
     * take their plain text password, compute the hash, and compare the
     * generated hash to the stored hash. Since it is almost impossible that
     * two passwords will generate the same hash, we know if the user gave us
     * the correct password or not. The only negative to this system is that
     * password recovery is basically impossible. Therefore, a reset password
     * method is used instead.
     *
     * @param bytes the byte array to compute the hash of.
     * @param algorithm the name of the algorithm requested.
     * @return a hashed version of the passed-in String
     */
    public static String hash(byte[] bytes, String algorithm) {
        synchronized (algorithm.intern()) {
            MessageDigest digest = digests.get(algorithm);
            if (digest == null) {
                try {
                    digest = MessageDigest.getInstance(algorithm);
                    digests.put(algorithm, digest);
                }
                catch (NoSuchAlgorithmException nsae) {
                    Log.error("Failed to load the " + algorithm + " MessageDigest. " +
                            "Jive will be unable to function normally.", nsae);
                    return null;
                }
            }
            // Now, compute hash.
            digest.update(bytes);
            return encodeHex(digest.digest());
        }
    }

    /**
     * Turns an array of bytes into a String representing each byte as an
     * unsigned hex number.
     * <p/>
     * Method by Santeri Paavolainen, Helsinki Finland 1996<br>
     * (c) Santeri Paavolainen, Helsinki Finland 1996<br>
     * Distributed under LGPL.
     *
     * @param bytes an array of bytes to convert to a hex-string
     * @return generated hex string
     */
    public static String encodeHex(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length * 2);
        int i;

        for (i = 0; i < bytes.length; i++) {
            if (((int)bytes[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString((int)bytes[i] & 0xff, 16));
        }
        return buf.toString();
    }

    /**
     * Turns a hex encoded string into a byte array. It is specifically meant
     * to "reverse" the toHex(byte[]) method.
     *
     * @param hex a hex encoded String to transform into a byte array.
     * @return a byte array representing the hex String[
     */
    public static byte[] decodeHex(String hex) {
        char[] chars = hex.toCharArray();
        byte[] bytes = new byte[chars.length / 2];
        int byteCount = 0;
        for (int i = 0; i < chars.length; i += 2) {
            int newByte = 0x00;
            newByte |= hexCharToByte(chars[i]);
            newByte <<= 4;
            newByte |= hexCharToByte(chars[i + 1]);
            bytes[byteCount] = (byte)newByte;
            byteCount++;
        }
        return bytes;
    }

    /**
     * Returns the the byte value of a hexadecmical char (0-f). It's assumed
     * that the hexidecimal chars are lower case as appropriate.
     *
     * @param ch a hexedicmal character (0-f)
     * @return the byte value of the character (0x00-0x0F)
     */
    private static byte hexCharToByte(char ch) {
        switch (ch) {
            case '0':
                return 0x00;
            case '1':
                return 0x01;
            case '2':
                return 0x02;
            case '3':
                return 0x03;
            case '4':
                return 0x04;
            case '5':
                return 0x05;
            case '6':
                return 0x06;
            case '7':
                return 0x07;
            case '8':
                return 0x08;
            case '9':
                return 0x09;
            case 'a':
                return 0x0A;
            case 'b':
                return 0x0B;
            case 'c':
                return 0x0C;
            case 'd':
                return 0x0D;
            case 'e':
                return 0x0E;
            case 'f':
                return 0x0F;
        }
        return 0x00;
    }

    /**
     * Encodes a String as a base64 String.
     *
     * @param data a String to encode.
     * @return a base64 encoded String.
     */
    public static String encodeBase64(String data) {
        byte[] bytes = null;
        try {
            bytes = data.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException uee) {
            Log.error(uee.getMessage(), uee);
        }
        return encodeBase64(bytes);
    }

    /**
     * Encodes a byte array into a base64 String.
     *
     * @param data a byte array to encode.
     * @return a base64 encode String.
     */
    public static String encodeBase64(byte[] data) {
        // Encode the String. We pass in a flag to specify that line
        // breaks not be added. This is consistent with our previous base64
        // implementation. Section 2.1 of 3548 (base64 spec) also specifies
        // no line breaks by default.
        return Base64.encodeBytes(data, Base64.DONT_BREAK_LINES);
    }

    /**
     * Decodes a base64 String.
     *
     * @param data a base64 encoded String to decode.
     * @return the decoded String.
     */
    public static byte[] decodeBase64(String data) {
        return Base64.decode(data);
    }

    /**
     * Converts a line of text into an array of lower case words using a
     * BreakIterator.wordInstance().<p>
     *
     * This method is under the Jive Open Source Software License and was
     * written by Mark Imbriaco.
     *
     * @param text a String of text to convert into an array of words
     * @return text broken up into an array of words.
     */
    public static String[] toLowerCaseWordArray(String text) {
        if (text == null || text.length() == 0) {
            return new String[0];
        }

        List<String> wordList = new ArrayList<String>();
        BreakIterator boundary = BreakIterator.getWordInstance();
        boundary.setText(text);
        int start = 0;

        for (int end = boundary.next(); end != BreakIterator.DONE;
             start = end, end = boundary.next()) {
            String tmp = text.substring(start, end).trim();
            // Remove characters that are not needed.
            tmp = replace(tmp, "+", "");
            tmp = replace(tmp, "/", "");
            tmp = replace(tmp, "\\", "");
            tmp = replace(tmp, "#", "");
            tmp = replace(tmp, "*", "");
            tmp = replace(tmp, ")", "");
            tmp = replace(tmp, "(", "");
            tmp = replace(tmp, "&", "");
            if (tmp.length() > 0) {
                wordList.add(tmp);
            }
        }
        return wordList.toArray(new String[wordList.size()]);
    }

    /**
     * Pseudo-random number generator object for use with randomString().
     * The Random class is not considered to be cryptographically secure, so
     * only use these random Strings for low to medium security applications.
     */
    private static Random randGen = new Random();

    /**
     * Array of numbers and letters of mixed case. Numbers appear in the list
     * twice so that there is a more equal chance that a number will be picked.
     * We can use the array to get a random number or letter by picking a random
     * array index.
     */
    private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" +
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

    /**
     * Returns a random String of numbers and letters (lower and upper case)
     * of the specified length. The method uses the Random class that is
     * built-in to Java which is suitable for low to medium grade security uses.
     * This means that the output is only pseudo random, i.e., each number is
     * mathematically generated so is not truly random.<p>
     * <p/>
     * The specified length must be at least one. If not, the method will return
     * null.
     *
     * @param length the desired length of the random String to return.
     * @return a random String of numbers and letters of the specified length.
     */
    public static String randomString(int length) {
        if (length < 1) {
            return null;
        }
        // Create a char buffer to put random letters and numbers in.
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
        }
        return new String(randBuffer);
    }

    /**
     * Intelligently chops a String at a word boundary (whitespace) that occurs
     * at the specified index in the argument or before. However, if there is a
     * newline character before <code>length</code>, the String will be chopped
     * there. If no newline or whitespace is found in <code>string</code> up to
     * the index <code>length</code>, the String will chopped at <code>length</code>.
     * <p/>
     * For example, chopAtWord("This is a nice String", 10) will return
     * "This is a" which is the first word boundary less than or equal to 10
     * characters into the original String.
     *
     * @param string the String to chop.
     * @param length the index in <code>string</code> to start looking for a
     *               whitespace boundary at.
     * @return a substring of <code>string</code> whose length is less than or
     *         equal to <code>length</code>, and that is chopped at whitespace.
     */
    public static String chopAtWord(String string, int length) {
        if (string == null || string.length() == 0) {
            return string;
        }

        char[] charArray = string.toCharArray();
        int sLength = string.length();
        if (length < sLength) {
            sLength = length;
        }

        // First check if there is a newline character before length; if so,
        // chop word there.
        for (int i = 0; i < sLength - 1; i++) {
            // Windows
            if (charArray[i] == '\r' && charArray[i + 1] == '\n') {
                return string.substring(0, i + 1);
            }
            // Unix
            else if (charArray[i] == '\n') {
                return string.substring(0, i);
            }
        }
        // Also check boundary case of Unix newline
        if (charArray[sLength - 1] == '\n') {
            return string.substring(0, sLength - 1);
        }

        // Done checking for newline, now see if the total string is less than
        // the specified chop point.
        if (string.length() < length) {
            return string;
        }

        // No newline, so chop at the first whitespace.
        for (int i = length - 1; i > 0; i--) {
            if (charArray[i] == ' ') {
                return string.substring(0, i).trim();
            }
        }

        // Did not find word boundary so return original String chopped at
        // specified length.
        return string.substring(0, length);
    }

    /**
     * Reformats a string where lines that are longer than <tt>width</tt>
     * are split apart at the earliest wordbreak or at maxLength, whichever is
     * sooner. If the width specified is less than 5 or greater than the input
     * Strings length the string will be returned as is.
     * <p/>
     * Please note that this method can be lossy - trailing spaces on wrapped
     * lines may be trimmed.
     *
     * @param input the String to reformat.
     * @param width the maximum length of any one line.
     * @return a new String with reformatted as needed.
     */
   /* public static String wordWrap(String input, int width, Locale locale) {
        // protect ourselves
        if (input == null) {
            return "";
        }
        else if (width < 5) {
            return input;
        }
        else if (width >= input.length()) {
            return input;
        }

        // default locale
        if (locale == null) {
            locale = JiveGlobals.getLocale();
        }

        StringBuilder buf = new StringBuilder(input);
        boolean endOfLine = false;
        int lineStart = 0;

        for (int i = 0; i < buf.length(); i++) {
            if (buf.charAt(i) == '\n') {
                lineStart = i + 1;
                endOfLine = true;
            }

            // handle splitting at width character
            if (i > lineStart + width - 1) {
                if (!endOfLine) {
                    int limit = i - lineStart - 1;
                    BreakIterator breaks = BreakIterator.getLineInstance(locale);
                    breaks.setText(buf.substring(lineStart, i));
                    int end = breaks.last();

                    // if the last character in the search string isn't a space,
                    // we can't split on it (looks bad). Search for a previous
                    // break character
                    if (end == limit + 1) {
                        if (!Character.isWhitespace(buf.charAt(lineStart + end))) {
                            end = breaks.preceding(end - 1);
                        }
                    }

                    // if the last character is a space, replace it with a \n
                    if (end != BreakIterator.DONE && end == limit + 1) {
                        buf.replace(lineStart + end, lineStart + end + 1, "\n");
                        lineStart = lineStart + end;
                    }
                    // otherwise, just insert a \n
                    else if (end != BreakIterator.DONE && end != 0) {
                        buf.insert(lineStart + end, '\n');
                        lineStart = lineStart + end + 1;
                    }
                    else {
                        buf.insert(i, '\n');
                        lineStart = i + 1;
                    }
                }
                else {
                    buf.insert(i, '\n');
                    lineStart = i + 1;
                    endOfLine = false;
                }
            }
        }

        return buf.toString();
    }*/

    /**
     * Escapes all necessary characters in the String so that it can be used in SQL
     *
     * @param string the string to escape.
     * @return the string with appropriate characters escaped.
     */
    public static String escapeForSQL(String string) {
        if (string == null) {
            return null;
        }
        else if (string.length() == 0) {
            return string;
        }

        char ch;
        char[] input = string.toCharArray();
        int i = 0;
        int last = 0;
        int len = input.length;
        StringBuilder out = null;
        for (; i < len; i++) {
            ch = input[i];

            if (ch == '\'') {
                if (out == null) {
                     out = new StringBuilder(len + 2);
                }
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append('\'').append('\'');
            }
        }

        if (out == null) {
            return string;
        }
        else if (i > last) {
            out.append(input, last, i - last);
        }

        return out.toString();
    }

    /**
     * Escapes all necessary characters in the String so that it can be used
     * in an XML doc.
     *
     * @param string the string to escape.
     * @return the string with appropriate characters escaped.
     */
    public static String escapeForXML(String string) {
        if (string == null) {
            return null;
        }
        char ch;
        int i = 0;
        int last = 0;
        char[] input = string.toCharArray();
        int len = input.length;
        StringBuilder out = new StringBuilder((int)(len * 1.3));
        for (; i < len; i++) {
            ch = input[i];
            if (ch > '>') {
            }
            else if (ch == '<') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(LT_ENCODE);
            }
            else if (ch == '&') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(AMP_ENCODE);
            }
            else if (ch == '"') {
                if (i > last) {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(QUOTE_ENCODE);
            }
        }
        if (last == 0) {
            return string;
        }
        if (i > last) {
            out.append(input, last, i - last);
        }
        return out.toString();
    }

    /**
     * Unescapes the String by converting XML escape sequences back into normal
     * characters.
     *
     * @param string the string to unescape.
     * @return the string with appropriate characters unescaped.
     */
    public static String unescapeFromXML(String string) {
        string = replace(string, "&lt;", "<");
        string = replace(string, "&gt;", ">");
        string = replace(string, "&quot;", "\"");
        return replace(string, "&amp;", "&");
    }

    private static final char[] zeroArray =
            "0000000000000000000000000000000000000000000000000000000000000000".toCharArray();

    /**
     * Pads the supplied String with 0's to the specified length and returns
     * the result as a new String. For example, if the initial String is
     * "9999" and the desired length is 8, the result would be "00009999".
     * This type of padding is useful for creating numerical values that need
     * to be stored and sorted as character data. Note: the current
     * implementation of this method allows for a maximum <tt>length</tt> of
     * 64.
     *
     * @param string the original String to pad.
     * @param length the desired length of the new padded String.
     * @return a new String padded with the required number of 0's.
     */
    public static String zeroPadString(String string, int length) {
        if (string == null || string.length() > length) {
            return string;
        }
        StringBuilder buf = new StringBuilder(length);
        buf.append(zeroArray, 0, length - string.length()).append(string);
        return buf.toString();
    }

    /**
     * Formats a Date as a fifteen character long String made up of the Date's
     * padded millisecond value.
     *
     * @return a Date encoded as a String.
     */
    public static String dateToMillis(Date date) {
        return zeroPadString(Long.toString(date.getTime()), 15);
    }

    /**
     * Returns a textual representation for the time that has elapsed.
     *
     * @param delta the elapsed time.
     * @return textual representation for the time that has elapsed.
     */
   /* public static String getElapsedTime(long delta) {
        if (delta < JiveConstants.MINUTE) {
            return LocaleUtils.getLocalizedString("global.less-minute");
        }
        else if (delta < JiveConstants.HOUR) {
            long mins = delta / JiveConstants.MINUTE;
            StringBuilder sb = new StringBuilder();
            sb.append(mins).append(" ");
            sb.append((mins==1) ? LocaleUtils.getLocalizedString("global.minute") : LocaleUtils.getLocalizedString("global.minutes"));
            return sb.toString();
        }
        else if (delta < JiveConstants.DAY) {
            long hours = delta / JiveConstants.HOUR;
            delta -= hours * JiveConstants.HOUR;
            long mins = delta / JiveConstants.MINUTE;
            StringBuilder sb = new StringBuilder();
            sb.append(hours).append(" ");
            sb.append((hours == 1) ? LocaleUtils.getLocalizedString("global.hour") : LocaleUtils.getLocalizedString("global.hours"));
            sb.append(", ");
            sb.append(mins).append(" ");
            sb.append((mins == 1) ? LocaleUtils.getLocalizedString("global.minute") : LocaleUtils.getLocalizedString("global.minutes"));
            return sb.toString();
        } else {
            long days = delta / JiveConstants.DAY;
            delta -= days * JiveConstants.DAY;
            long hours = delta / JiveConstants.HOUR;
            delta -= hours * JiveConstants.HOUR;
            long mins = delta / JiveConstants.MINUTE;
            StringBuilder sb = new StringBuilder();
            sb.append(days).append(" ");
            sb.append((days == 1) ? LocaleUtils.getLocalizedString("global.day") : LocaleUtils.getLocalizedString("global.days"));
            sb.append(", ");
            sb.append(hours).append(" ");
            sb.append((hours == 1) ? LocaleUtils.getLocalizedString("global.hour") : LocaleUtils.getLocalizedString("global.hours"));
            sb.append(", ");
            sb.append(mins).append(" ");
            sb.append((mins == 1) ? LocaleUtils.getLocalizedString("global.minute") : LocaleUtils.getLocalizedString("global.minutes"));
            return sb.toString();
        }
    }
*/
    /**
     * Returns a formatted String from time.
     *
     * @param diff the amount of elapsed time.
     * @return the formatte String.
     */
    public static String getTimeFromLong(long diff) {
        final String HOURS = "h";
        final String MINUTES = "min";
        //final String SECONDS = "sec";

        final long MS_IN_A_DAY = 1000 * 60 * 60 * 24;
        final long MS_IN_AN_HOUR = 1000 * 60 * 60;
        final long MS_IN_A_MINUTE = 1000 * 60;
        final long MS_IN_A_SECOND = 1000;
        //Date currentTime = new Date();
        //long numDays = diff / MS_IN_A_DAY;
        diff = diff % MS_IN_A_DAY;
        long numHours = diff / MS_IN_AN_HOUR;
        diff = diff % MS_IN_AN_HOUR;
        long numMinutes = diff / MS_IN_A_MINUTE;
        diff = diff % MS_IN_A_MINUTE;
        //long numSeconds = diff / MS_IN_A_SECOND;
        diff = diff % MS_IN_A_SECOND;
        //long numMilliseconds = diff;

        StringBuffer buf = new StringBuffer();
        if (numHours > 0) {
            buf.append(numHours + " " + HOURS + ", ");
        }

        if (numMinutes > 0) {
            buf.append(numMinutes + " " + MINUTES);
        }

        //buf.append(numSeconds + " " + SECONDS);

        String result = buf.toString();

        if (numMinutes < 1) {
            result = "< 1 minute";
        }

        return result;
    }

    /**
     * Returns a collection of Strings as a comma-delimitted list of strings.
     *
     * @return a String representing the Collection.
     */
    public static String collectionToString(Collection<String> collection) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        String delim = "";
        for (String element : collection) {
            buf.append(delim);
            buf.append(element);
            delim = ",";
        }
        return buf.toString();
    }

    /**
     * Returns a comma-delimitted list of Strings as a Collection.
     *
     * @return a Collection representing the String.
     */
    public static Collection<String> stringToCollection(String string) {
        if (string == null || string.trim().length() == 0) {
            return Collections.emptyList();
        }
        Collection<String> collection = new ArrayList<String>();
        StringTokenizer tokens = new StringTokenizer(string, ",");
        while (tokens.hasMoreTokens()) {
            collection.add(tokens.nextToken().trim());
        }
        return collection;
    }

    /**
     * Abbreviates a string to a specified length and then adds an ellipsis
     * if the input is greater than the maxWidth. Example input:
     * <pre>
     *      user1@jivesoftware.com/home
     * </pre>
     * and a maximum length of 20 characters, the abbreviate method will return:
     * <pre>
     *      user1@jivesoftware.c...
     * </pre>
     * @param str the String to abbreviate.
     * @param maxWidth the maximum size of the string, minus the ellipsis.
     * @return the abbreviated String, or <tt>null</tt> if the string was <tt>null</tt>.
     */
    public static String abbreviate(String str, int maxWidth) {
        if (null == str) {
            return null;
        }

        if (str.length() <= maxWidth) {
            return str;
        }
        
        return str.substring(0, maxWidth) + "...";
    }

    /**
     * Returns true if the string passed in is a valid Email address.
     *
     * @param address Email address to test for validity.
     * @return true if the string passed in is a valid email address.
     */
  /*  public static boolean isValidEmailAddress(String address) {
        if (address == null) {
            return false;
        }

        if (!address.contains("@")) {
            return false;
        }

        try {
            InternetAddress.parse(address);
            return true;
        }
        catch (AddressException e) {
            return false;
        }
    }
    */
    /**
	 * Removes characters likely to enable Cross Site Scripting attacks from the
	 * provided input string. The characters that are removed from the input
	 * string, if present, are:
	 * 
	 * <pre>
	 * &lt; &gt; &quot; ' % ; ) ( &amp; + -
	 * </pre>
	 * 
	 * @param input the string to be scrubbed
	 * @return Input without certain characters;
	 */
	public static String removeXSSCharacters(String input) {
		final String[] xss = { "<", ">", "\"", "'", "%", ";", ")", "(", "&",
				"+", "-" };
		for (int i = 0; i < xss.length; i++) {
			input = input.replace(xss[i], "");
		}
		return input;
	}
	
	/**
	 * Returns the UTF-8 bytes for the given String, suppressing
	 * UnsupportedEncodingException (in lieu of log message)
	 * 
	 * @param input The source string
	 * @return The UTF-8 encoding for the given string
	 */
	public static byte[] getBytes(String input) {
		try {
			return input.getBytes("UTF-8");
		} catch (UnsupportedEncodingException uee) {
			Log.warn("Unable to encode string using UTF-8: " + input);
			return input.getBytes(); // default encoding
		}
	}
	
	/**
	 * Returns the UTF-8 String for the given byte array, suppressing
	 * UnsupportedEncodingException (in lieu of log message)
	 * 
	 * @param input The source byte array
	 * @return The UTF-8 encoded String for the given byte array
	 */
	public static String getString(byte[] input) {
		try {
			return new String(input, "UTF-8");
		} catch (UnsupportedEncodingException uee) {
			String result = new String(input); // default encoding
			Log.warn("Unable to decode byte array using UTF-8: " + result);
			return result;
		}
	}
	public static boolean stringIsNotEmpty(String text)
	{
		if(text !=null && !"".equals(text))
		{
			return true;
		}
		return false;
	}
	public static boolean stringIsEmpty(String text)
	{
		if(text ==null || "".equals(text))
		{
			return true;
		}
		return false;
	}
	 private static Pattern numericPattern = Pattern.compile("^[0-9\\-]+$");  
	    private static Pattern integerPattern = Pattern.compile("^[0-9]+$");  
	    private static Pattern numericStringPattern = Pattern.compile("^[0-9\\-\\-]+$");  
	    private static Pattern floatNumericPattern = Pattern.compile("^[0-9\\.]+$");  
	    private static Pattern abcPattern = Pattern.compile("^[a-z|A-Z]+$");  
	    public static final String splitStrPattern = ",|，|;|；|、|\\.|。|-|_|\\(|\\)|\\[|\\]|\\{|\\}|\\\\|/| |　|\"";  
	      
	    /** 
	     * 判断是否数字表示 
	     *  
	     * @param src 
	     *            源字符串 
	     * @return 是否数字的标志 
	     */  
	    public static boolean isNumeric(String src) {  
	        boolean return_value = false;  
	        if (src != null && src.length() > 0) {  
	            Matcher m = numericPattern.matcher(src);  
	            if (m.find()) {  
	                return_value = true;  
	            }  
	        }  
	        return return_value;  
	    }  
	      
	    /** 
	     *  
	     * 是否是数字。整数，没有任何符号 
	     * 
	     * @param src 
	     * @return 
	     */  
	    public static boolean isInteger(String src){  
	        boolean return_value = false;  
	        if (src != null && src.length() > 0) {  
	            Matcher m = integerPattern.matcher(src);  
	            if (m.find()) {  
	                return_value = true;  
	            }  
	        }  
	        return return_value;  
	    }  
	      
	    /** 
	     * 判断是否数字 
	     *  
	     * @param src 
	     *            源字符串 
	     * @return 是否数字的标志 
	     */  
	    public static boolean isNumericString(String src) {  
	        boolean return_value = false;  
	        if (src != null && src.length() > 0) {  
	            Matcher m = numericStringPattern.matcher(src);  
	            if (m.find()) {  
	                return_value = true;  
	            }  
	        }  
	        return return_value;  
	    }  
	      
	    /** 
	     * 判断是否纯字母组合 
	     *  
	     * @param src 
	     *            源字符串 
	     * @return 是否纯字母组合的标志 
	     */  
	    public static boolean isABC(String src) {  
	        boolean return_value = false;  
	        if (src != null && src.length() > 0) {  
	            Matcher m = abcPattern.matcher(src);  
	            if (m.find()) {  
	                return_value = true;  
	            }  
	        }  
	        return return_value;  
	    }  
	      
	    /** 
	     * 判断是否浮点数字表示 
	     *  
	     * @param src 
	     *            源字符串 
	     * @return 是否数字的标志 
	     */  
	    public static boolean isFloatNumeric(String src) {  
	        boolean return_value = false;  
	        if (src != null && src.length() > 0) {  
	            Matcher m = floatNumericPattern.matcher(src);  
	            if (m.find()) {  
	                return_value = true;  
	            }  
	        }  
	        return return_value;  
	    }  
	      
	    /** 
	     * 把string array or list用给定的符号symbol连接成一个字符串 
	     *  
	     * @param array 
	     * @param symbol 
	     * @return 
	     */  
	    public static String joinString(List array, String symbol) {  
	        String result = "";  
	        if (array != null) {  
	            for (int i = 0; i < array.size(); i++) {  
	                String temp = array.get(i).toString();  
	                if (temp != null && temp.trim().length() > 0)  
	                    result += (temp + symbol);  
	            }  
	            if (result.length() > 1)  
	                result = result.substring(0, result.length() - 1);  
	        }  
	        return result;  
	    }  
	      
	    /** 
	     * 截取字符串 
	     *  
	     * @param subject 
	     * @param size 
	     * @return 
	     */  
	    public static String subStringNotEncode(String subject, int size) {  
	        if (subject != null && subject.length() > size) {  
	            subject = subject.substring(0, size) + "...";  
	        }  
	        return subject;  
	    }  
	      
	      
	    /** 
	     * 截取字符串　超出的字符用symbol代替 　　 
	     *  
	     * @param len 
	     *            　字符串长度　长度计量单位为一个GBK汉字　　两个英文字母计算为一个单位长度 
	     * @param str 
	     * @param symbol 
	     * @return 
	     */  
	    public static String getLimitLengthString(String str, int len, String symbol) {  
	        int iLen = len * 2;  
	        int counterOfDoubleByte = 0;  
	        String strRet = "";  
	        try {  
	            if (str != null) {  
	                byte[] b = str.getBytes("GBK");  
	                if (b.length <= iLen) {  
	                    return str;  
	                }  
	                for (int i = 0; i < iLen; i++) {  
	                    if (b[i] < 0) {  
	                        counterOfDoubleByte++;  
	                    }  
	                }  
	                if (counterOfDoubleByte % 2 == 0) {  
	                    strRet = new String(b, 0, iLen, "GBK") + symbol;  
	                    return strRet;  
	                } else {  
	                    strRet = new String(b, 0, iLen - 1, "GBK") + symbol;  
	                    return strRet;  
	                }  
	            } else {  
	                return "";  
	            }  
	        } catch (Exception ex) {  
	            return str.substring(0, len);  
	        } finally {  
	            strRet = null;  
	        }  
	    }  
	      
	    /** 
	     * 截取字符串　超出的字符用symbol代替 　　 
	     *  
	     * @param len 
	     *            　字符串长度　长度计量单位为一个GBK汉字　　两个英文字母计算为一个单位长度 
	     * @param str 
	     * @param symbol 
	     * @return12 
	     */  
	    public static String getLimitLengthString(String str, int len) {  
	        return getLimitLengthString(str, len, "...");  
	    }  
	      
	      
	    /** 
	     * 截取字符，不转码 
	     *  
	     * @param subject 
	     * @param size 
	     * @return 
	     */  
	    public static String subStrNotEncode(String subject, int size) {  
	        if (subject.length() > size) {  
	            subject = subject.substring(0, size);  
	        }  
	        return subject;  
	    }  
	      
	    /** 
	     * 把string array or list用给定的符号symbol连接成一个字符串 
	     *  
	     * @param array 
	     * @param symbol 
	     * @return 
	     */  
	    public static String joinString(String[] array, String symbol) {  
	        String result = "";  
	        if (array != null) {  
	            for (int i = 0; i < array.length; i++) {  
	                String temp = array[i];  
	                if (temp != null && temp.trim().length() > 0)  
	                    result += (temp + symbol);  
	            }  
	            if (result.length() > 1)  
	                result = result.substring(0, result.length() - 1);  
	        }  
	        return result;  
	    }  
	      
	    /** 
	     * 取得字符串的实际长度（考虑了汉字的情况） 
	     *  
	     * @param SrcStr 
	     *            源字符串 
	     * @return 字符串的实际长度 
	     */  
	    public static int getStringLen(String SrcStr) {  
	        int return_value = 0;  
	        if (SrcStr != null) {  
	            char[] theChars = SrcStr.toCharArray();  
	            for (int i = 0; i < theChars.length; i++) {  
	                return_value += (theChars[i] <= 255) ? 1 : 2;  
	            }  
	        }  
	        return return_value;  
	    }  
	      
	    /** 
	     * 检查数据串中是否包含非法字符集 
	     *  
	     * @param str 
	     * @return [true]|[false] 包含|不包含 
	     */  
	    public static boolean check(String str) {  
	        String sIllegal = "'\"";  
	        int len = sIllegal.length();  
	        if (null == str)  
	            return false;  
	        for (int i = 0; i < len; i++) {  
	            if (str.indexOf(sIllegal.charAt(i)) != -1)  
	                return true;  
	        }  
	          
	        return false;  
	    }  
	      
	    /** 
	     * getHideEmailPrefix - 隐藏邮件地址前缀。 
	     *  
	     * @param email 
	     *            - EMail邮箱地址 例如: linwenguo@koubei.com 等等... 
	     * @return 返回已隐藏前缀邮件地址, 如 *********@koubei.com. 
	     * @version 1.0 (2006.11.27) Wilson Lin 
	     */  
	    public static String getHideEmailPrefix(String email) {  
	        if (null != email) {  
	            int index = email.lastIndexOf('@');  
	            if (index > 0) {  
	                email = repeat("*", index).concat(email.substring(index));  
	            }  
	        }  
	        return email;  
	    }  
	      
	    /** 
	     * repeat - 通过源字符串重复生成N次组成新的字符串。 
	     *  
	     * @param src 
	     *            - 源字符串 例如: 空格(" "), 星号("*"), "浙江" 等等... 
	     * @param num 
	     *            - 重复生成次数 
	     * @return 返回已生成的重复字符串 
	     * @version 1.0 (2006.10.10) Wilson Lin 
	     */  
	    public static String repeat(String src, int num) {  
	        StringBuffer s = new StringBuffer();  
	        for (int i = 0; i < num; i++)  
	            s.append(src);  
	        return s.toString();  
	    }  
	      
	      
	    /** 
	     * 根据指定的字符把源字符串分割成一个数组 
	     *  
	     * @param src 
	     * @return 
	     */  
	    public static List<String> parseString2ListByCustomerPattern(String pattern, String src) {  
	          
	        if (src == null)  
	            return null;  
	        List<String> list = new ArrayList<String>();  
	        String[] result = src.split(pattern);  
	        for (int i = 0; i < result.length; i++) {  
	            list.add(result[i]);  
	        }  
	        return list;  
	    }  
	      
	    /** 
	     * 根据指定的字符把源字符串分割成一个数组 
	     *  
	     * @param src 
	     * @return 
	     */  
	    public static List<String> parseString2ListByPattern(String src) {  
	        String pattern = "，|,|、|。";  
	        return parseString2ListByCustomerPattern(pattern, src);  
	    }  
	      
	    /** 
	     * 格式化一个float 
	     *  
	     * @param format 
	     *            要格式化成的格式 such as #.00, #.# 
	     */  
	      
	    public static String formatFloat(float f, String format) {  
	        DecimalFormat df = new DecimalFormat(format);  
	        return df.format(f);  
	    }  
	      
	    /** 
	     * 判断是否是空字符串 null和"" 都返回 true 
	     *  
	     * @param s 
	     * @return 
	     */  
	    public static boolean isEmpty(String s) {  
	        if (s != null && !s.equals("")) {  
	            return false;  
	        }  
	        return true;  
	    }  
	      
	    /** 
	     * 自定义的分隔字符串函数 例如: 1,2,3 =>[1,2,3] 3个元素 ,2,3=>[,2,3] 3个元素 ,2,3,=>[,2,3,] 
	     * 4个元素 ,,,=>[,,,] 4个元素 
	     * 5.22算法修改，为提高速度不用正则表达式 两个间隔符,,返回""元素 
	     *  
	     * @param split 
	     *            分割字符 默认, 
	     * @param src 
	     *            输入字符串 
	     * @return 分隔后的list 
	     */  
	    public static List<String> splitToList(String split, String src) {  
	        // 默认,  
	        String sp = ",";  
	        if (split != null && split.length() == 1) {  
	            sp = split;  
	        }  
	        List<String> r = new ArrayList<String>();  
	        int lastIndex = -1;  
	        int index = src.indexOf(sp);  
	        if (-1 == index && src != null) {  
	            r.add(src);  
	            return r;  
	        }  
	        while (index >= 0) {  
	            if (index > lastIndex) {  
	                r.add(src.substring(lastIndex + 1, index));  
	            } else {  
	                r.add("");  
	            }  
	              
	            lastIndex = index;  
	            index = src.indexOf(sp, index + 1);  
	            if (index == -1) {  
	                r.add(src.substring(lastIndex + 1, src.length()));  
	            }  
	        }  
	        return r;  
	    }  
	      
	    /** 
	     * 把 名=值 参数表转换成字符串 (a=1,b=2 =>a=1&b=2) 
	     *  
	     * @param map 
	     * @return 
	     */  
	    public static String linkedHashMapToString(LinkedHashMap<String, String> map) {  
	        if (map != null && map.size() > 0) {  
	            String result = "";  
	            Iterator it = map.keySet().iterator();  
	            while (it.hasNext()) {  
	                String name = (String) it.next();  
	                String value = (String) map.get(name);  
	                result += (result.equals("")) ? "" : "&";  
	                result += String.format("%s=%s", name, value);  
	            }  
	            return result;  
	        }  
	        return null;  
	    }  
	      
	    /** 
	     * 解析字符串返回 名称=值的参数表 (a=1&b=2 => a=1,b=2) 
	     *  
	     * @see test.koubei.util.StringUtilTest#testParseStr() 
	     * @param str 
	     * @return 
	     */  
	    @SuppressWarnings("unchecked")  
	    public static LinkedHashMap<String, String> toLinkedHashMap(String str) {  
	        if (str != null && !str.equals("") && str.indexOf("=") > 0) {  
	            LinkedHashMap result = new LinkedHashMap();  
	              
	            String name = null;  
	            String value = null;  
	            int i = 0;  
	            while (i < str.length()) {  
	                char c = str.charAt(i);  
	                switch (c) {  
	                    case 61: // =  
	                        value = "";  
	                        break;  
	                    case 38: // &  
	                        if (name != null && value != null && !name.equals("")) {  
	                            result.put(name, value);  
	                        }  
	                        name = null;  
	                        value = null;  
	                        break;  
	                    default:  
	                        if (value != null) {  
	                            value = (value != null) ? (value + c) : "" + c;  
	                        } else {  
	                            name = (name != null) ? (name + c) : "" + c;  
	                        }  
	                }  
	                i++;  
	                  
	            }  
	              
	            if (name != null && value != null && !name.equals("")) {  
	                result.put(name, value);  
	            }  
	              
	            return result;  
	              
	        }  
	        return null;  
	    }  
	      
	    /** 
	     * 根据输入的多个解释和下标返回一个值 
	     *  
	     * @param captions 
	     *            例如:"无,爱干净,一般,比较乱" 
	     * @param index 
	     *            1 
	     * @return 一般 
	     */  
	    public static String getCaption(String captions, int index) {  
	        if (index > 0 && captions != null && !captions.equals("")) {  
	            String[] ss = captions.split(",");  
	            if (ss != null && ss.length > 0 && index < ss.length) {  
	                return ss[index];  
	            }  
	        }  
	        return null;  
	    }  
	      
	    /** 
	     * 数字转字符串,如果num<=0 则输出""; 
	     *  
	     * @param num 
	     * @return 
	     */  
	    public static String numberToString(Object num) {  
	        if (num == null) {  
	            return null;  
	        } else if (num instanceof Integer && (Integer) num > 0) {  
	            return Integer.toString((Integer) num);  
	        } else if (num instanceof Long && (Long) num > 0) {  
	            return Long.toString((Long) num);  
	        } else if (num instanceof Float && (Float) num > 0) {  
	            return Float.toString((Float) num);  
	        } else if (num instanceof Double && (Double) num > 0) {  
	            return Double.toString((Double) num);  
	        } else {  
	            return "";  
	        }  
	    }  
	      
	    /** 
	     * 货币转字符串 
	     *  
	     * @param money 
	     * @param style 
	     *            样式 [default]要格式化成的格式 such as #.00, #.# 
	     * @return 
	     */  
	      
	    public static String moneyToString(Object money, String style) {  
	        if (money != null && style != null && (money instanceof Double || money instanceof Float)) {  
	            Double num = (Double) money;  
	              
	            if (style.equalsIgnoreCase("default")) {  
	                // 缺省样式 0 不输出 ,如果没有输出小数位则不输出.0  
	                if (num == 0) {  
	                    // 不输出0  
	                    return "";  
	                } else if ((num * 10 % 10) == 0) {  
	                    // 没有小数  
	                    return Integer.toString((int) num.intValue());  
	                } else {  
	                    // 有小数  
	                    return num.toString();  
	                }  
	                  
	            } else {  
	                DecimalFormat df = new DecimalFormat(style);  
	                return df.format(num);  
	            }  
	        }  
	        return null;  
	    }  
	      
	    /** 
	     * 在sou中是否存在finds 如果指定的finds字符串有一个在sou中找到,返回true; 
	     *  
	     * @param sou 
	     * @param find 
	     * @return 
	     */  
	    public static boolean strPos(String sou, String... finds) {  
	        if (sou != null && finds != null && finds.length > 0) {  
	            for (int i = 0; i < finds.length; i++) {  
	                if (sou.indexOf(finds[i]) > -1)  
	                    return true;  
	            }  
	        }  
	        return false;  
	    }  
	      
	    public static boolean strPos(String sou, List<String> finds) {  
	        if (sou != null && finds != null && finds.size() > 0) {  
	            for (String s : finds) {  
	                if (sou.indexOf(s) > -1)  
	                    return true;  
	            }  
	        }  
	        return false;  
	    }  
	      
	    public static boolean strPos(String sou, String finds) {  
	        List<String> t = splitToList(",", finds);  
	        return strPos(sou, t);  
	    }  
	      
	    /** 
	     * 判断两个字符串是否相等 如果都为null则判断为相等,一个为null另一个not null则判断不相等 否则如果s1=s2则相等 
	     *  
	     * @param s1 
	     * @param s2 
	     * @return 
	     */  
	    public static boolean equals(String s1, String s2) {  
	        if (StringUtil.isEmpty(s1) && StringUtil.isEmpty(s2)) {  
	            return true;  
	        } else if (!StringUtil.isEmpty(s1) && !StringUtil.isEmpty(s2)) {  
	            return s1.equals(s2);  
	        }  
	        return false;  
	    }  
	      
	    public static int toInt(String s) {  
	        if (s != null && !"".equals(s.trim())) {  
	            try {  
	                return Integer.parseInt(s);  
	            } catch (Exception e) {  
	                return 0;  
	            }  
	        }  
	        return 0;  
	    }  
	      
	    public static double toDouble(String s) {  
	        if (s != null && !"".equals(s.trim())) {  
	            return Double.parseDouble(s);  
	        }  
	        return 0;  
	    }  
	      
	    public static boolean isPhone(String phone) {  
	        if (phone == null && "".equals(phone)) {  
	            return false;  
	        }  
	        String[] strPhone = phone.split("-");  
	        try {  
	            for (int i = 0; i < strPhone.length; i++) {  
	                Long.parseLong(strPhone[i]);  
	            }  
	              
	        } catch (Exception e) {  
	            return false;  
	        }  
	        return true;  
	          
	    }  
	      
	    /** 
	     * 把xml 转为object 
	     *  
	     * @param xml 
	     * @return 
	     */  
	    public static Object xmlToObject(String xml) {  
	        try {  
	            ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes("UTF8"));  
	            XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(in));  
	            return decoder.readObject();  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	        return null;  
	    }  
	      
	    /** 
	     * 按规定长度截断字符串，没有...的省略符号 
	     *  
	     * @param subject 
	     * @param size 
	     * @return 
	     */  
	      
	    public static long toLong(String s) {  
	        try {  
	            if (s != null && !"".equals(s.trim()))  
	                return Long.parseLong(s);  
	        } catch (Exception exception) {  
	        }  
	        return 0L;  
	    }  
	      
	    public static String simpleEncrypt(String str) {  
	        if (str != null && str.length() > 0) {  
	            // str = str.replaceAll("0","a");  
	            str = str.replaceAll("1", "b");  
	            // str = str.replaceAll("2","c");  
	            str = str.replaceAll("3", "d");  
	            // str = str.replaceAll("4","e");  
	            str = str.replaceAll("5", "f");  
	            str = str.replaceAll("6", "g");  
	            str = str.replaceAll("7", "h");  
	            str = str.replaceAll("8", "i");  
	            str = str.replaceAll("9", "j");  
	        }  
	        return str;  
	          
	    }  
	      
	    /** 
	     * 过滤用户输入的URL地址（防治用户广告） 目前只针对以http或www开头的URL地址 
	     * 本方法调用的正则表达式，不建议用在对性能严格的地方例如:循环及list页面等 
	     *  
	     * @param str 
	     *            需要处理的字符串 
	     * @return 返回处理后的字符串 
	     */  
	    public static String removeURL(String str) {  
	        if (str != null)  
	            str = str.toLowerCase().replaceAll("(http|www|com|cn|org|\\.)+", "");  
	        return str;  
	    }  
	      
	    /** 
	     * 随即生成指定位数的含数字验证码字符串 
	     *  
	     * @param bit 
	     *            指定生成验证码位数 
	     * @return String 
	     */  
	    public static String numRandom(int bit) {  
	        if (bit == 0)  
	            bit = 6; // 默认6位  
	        String str = "";  
	        str = "0123456789";// 初始化种子  
	        return RandomStringUtils.random(bit, str);// 返回6位的字符串  
	    }  
	      
	    /** 
	     * 随即生成指定位数的含验证码字符串 
	     *  
	     * @param bit 
	     *            指定生成验证码位数 
	     * @return String 
	     */  
	    public static String random(int bit) {  
	        if (bit == 0)  
	            bit = 6; // 默认6位  
	        // 因为o和0,l和1很难区分,所以,去掉大小写的o和l  
	        String str = "";  
	        str = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz";// 初始化种子  
	        return RandomStringUtils.random(bit, str);// 返回6位的字符串  
	    }  
	      
	    /** 
	     * 检查字符串是否属于手机号码 
	     *  
	     * @param str 
	     * @return boolean 
	     */  
	    public static boolean isMobile(String str) {  
	        if (str == null || str.equals(""))  
	            return false;  
	        if (str.length() != 11 || !isNumeric(str))  
	            return false;  
	        if (!str.substring(0, 2).equals("13") && !str.substring(0, 2).equals("15")  
	                && !str.substring(0, 2).equals("18"))  
	            return false;  
	        return true;  
	    }  
	      
	    /** 
	     * Wap页面的非法字符检查 
	     *  
	     * @param str 
	     * @return 
	     */  
	    public static String replaceWapStr(String str) {  
	        if (str != null) {  
	            str = str.replaceAll("<span class=\"keyword\">", "");  
	            str = str.replaceAll("</span>", "");  
	            str = str.replaceAll("<strong class=\"keyword\">", "");  
	            str = str.replaceAll("<strong>", "");  
	            str = str.replaceAll("</strong>", "");  
	              
	            str = str.replace('$', '＄');  
	              
	            str = str.replaceAll("&", "＆");  
	            str = str.replace('&', '＆');  
	              
	            str = str.replace('<', '＜');  
	              
	            str = str.replace('>', '＞');  
	              
	        }  
	        return str;  
	    }  
	      
	    /** 
	     * 字符串转float 如果异常返回0.00 
	     *  
	     * @param s 
	     *            输入的字符串 
	     * @return 转换后的float 
	     */  
	    public static Float toFloat(String s) {  
	        try {  
	            return Float.parseFloat(s);  
	        } catch (NumberFormatException e) {  
	            return new Float(0);  
	        }  
	    }  
	      
	    /** 
	     * 页面中去除字符串中的空格、回车、换行符、制表符 
	     *  
	     * @param str 
	     * @return 
	     */  
	    public static String replaceBlank(String str) {  
	        if (str != null) {  
	            Pattern p = Pattern.compile("\\s*|\t|\r|\n");  
	            Matcher m = p.matcher(str);  
	            str = m.replaceAll("");  
	        }  
	        return str;  
	    }  
	      
	    /** 
	     * 全角生成半角 
	     *  
	     * @param str 
	     * @return 
	     */  
	    public static String Q2B(String QJstr) {  
	        String outStr = "";  
	        String Tstr = "";  
	        byte[] b = null;  
	        for (int i = 0; i < QJstr.length(); i++) {  
	            try {  
	                Tstr = QJstr.substring(i, i + 1);  
	                b = Tstr.getBytes("unicode");  
	            } catch (java.io.UnsupportedEncodingException e) {  
	                e.printStackTrace();  
	            }  
	            if (b[3] == -1) {  
	                b[2] = (byte) (b[2] + 32);  
	                b[3] = 0;  
	                try {  
	                    outStr = outStr + new String(b, "unicode");  
	                } catch (java.io.UnsupportedEncodingException ex) {  
	                    ex.printStackTrace();  
	                }  
	            } else {  
	                outStr = outStr + Tstr;  
	            }  
	        }  
	        return outStr;  
	    }  
	      
	    /** 
	     * 转换编码 
	     *  
	     * @param s 
	     *            源字符串 
	     * @param fencode 
	     *            源编码格式 
	     * @param bencode 
	     *            目标编码格式 
	     * @return 目标编码 
	     */  
	    public static String changCoding(String s, String fencode, String bencode) {  
	        try {  
	            String str = new String(s.getBytes(fencode), bencode);  
	            return str;  
	        } catch (UnsupportedEncodingException e) {  
	            return s;  
	        }  
	    }  
	      
	    /** 
	     * 除去html标签 
	     *  
	     * @param str 
	     * @return 
	     */  
	    public static String removeHTMLLableExe(String str) {  
	        str = stringReplace(str, ">\\s*<", "><");  
	        str = stringReplace(str, " ", " ");// 替换空格  
	        str = stringReplace(str, "<br ?/?>", "\n");// 去<br><br />  
	        str = stringReplace(str, "<([^<>]+)>", "");// 去掉<>内的字符  
	        str = stringReplace(str, "\\s\\s\\s*", " ");// 将多个空白变成一个空格  
	        str = stringReplace(str, "^\\s*", "");// 去掉头的空白  
	        str = stringReplace(str, "\\s*$", "");// 去掉尾的空白  
	        str = stringReplace(str, " +", " ");  
	        return str;  
	    }  
	      
	    /** 
	     * 除去html标签 
	     *  
	     * @param str 
	     *            源字符串 
	     * @return 目标字符串 
	     */  
	    public static String removeHTMLLable(String str) {  
	        str = stringReplace(str, "\\s", "");// 去掉页面上看不到的字符  
	        str = stringReplace(str, "<br ?/?>", "\n");// 去<br><br />  
	        str = stringReplace(str, "<([^<>]+)>", "");// 去掉<>内的字符  
	        str = stringReplace(str, " ", " ");// 替换空格  
	        str = stringReplace(str, "&(\\S)(\\S?)(\\S?)(\\S?);", "");// 去<br><br />  
	        return str;  
	    }  
	      
	    /** 
	     * 去掉HTML标签之外的字符串 
	     *  
	     * @param str 
	     *            源字符串 
	     * @return 目标字符串 
	     */  
	    public static String removeOutHTMLLable(String str) {  
	        str = stringReplace(str, ">([^<>]+)<", "><");  
	        str = stringReplace(str, "^([^<>]+)<", "<");  
	        str = stringReplace(str, ">([^<>]+)$", ">");  
	        return str;  
	    }  
	      
	    /** 
	     * 字符串替换 
	     *  
	     * @param str 
	     *            源字符串 
	     * @param sr 
	     *            正则表达式样式 
	     * @param sd 
	     *            替换文本 
	     * @return 结果串 
	     */  
	    public static String stringReplace(String str, String sr, String sd) {  
	        String regEx = sr;  
	        Pattern p = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);  
	        Matcher m = p.matcher(str);  
	        str = m.replaceAll(sd);  
	        return str;  
	    }  
	      
	    /** 
	     * 将html的省略写法替换成非省略写法 
	     *  
	     * @param str 
	     *            html字符串 
	     * @param pt 
	     *            标签如table 
	     * @return 结果串 
	     */  
	    public static String fomateToFullForm(String str, String pt) {  
	        String regEx = "<" + pt + "\\s+([\\S&&[^<>]]*)/>";  
	        Pattern p = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);  
	        Matcher m = p.matcher(str);  
	        String[] sa = null;  
	        String sf = "";  
	        String sf2 = "";  
	        String sf3 = "";  
	        for (; m.find();) {  
	            sa = p.split(str);  
	            if (sa == null) {  
	                break;  
	            }  
	            sf = str.substring(sa[0].length(), str.indexOf("/>", sa[0].length()));  
	            sf2 = sf + "></" + pt + ">";  
	            sf3 = str.substring(sa[0].length() + sf.length() + 2);  
	            str = sa[0] + sf2 + sf3;  
	            sa = null;  
	        }  
	        return str;  
	    }  
	      
	    /** 
	     * 得到字符串的子串位置序列 
	     *  
	     * @param str 
	     *            字符串 
	     * @param sub 
	     *            子串 
	     * @param b 
	     *            true子串前端,false子串后端 
	     * @return 字符串的子串位置序列 
	     */  
	    public static int[] getSubStringPos(String str, String sub, boolean b) {  
	        // int[] i = new int[(new Integer((str.length()-stringReplace( str , sub  
	        // , "" ).length())/sub.length())).intValue()] ;  
	        String[] sp = null;  
	        int l = sub.length();  
	        sp = splitString(str, sub);  
	        if (sp == null) {  
	            return null;  
	        }  
	        int[] ip = new int[sp.length - 1];  
	        for (int i = 0; i < sp.length - 1; i++) {  
	            ip[i] = sp[i].length() + l;  
	            if (i != 0) {  
	                ip[i] += ip[i - 1];  
	            }  
	        }  
	        if (b) {  
	            for (int j = 0; j < ip.length; j++) {  
	                ip[j] = ip[j] - l;  
	            }  
	        }  
	        return ip;  
	    }  
	      
	    /** 
	     * 根据正则表达式分割字符串 
	     *  
	     * @param str 
	     *            源字符串 
	     * @param ms 
	     *            正则表达式 
	     * @return 目标字符串组 
	     */  
	    public static String[] splitString(String str, String ms) {  
	        String regEx = ms;  
	        Pattern p = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);  
	        String[] sp = p.split(str);  
	        return sp;  
	    }  
	      
	    /** 
	     * ************************************************************************* 
	     * 根据正则表达式提取字符串,相同的字符串只返回一个 
	     *  
	     * @param str 
	     *            源字符串 
	     * @param pattern 
	     *            正则表达式 
	     * @return 目标字符串数据组 
	     */  
	      
	    // ★传入一个字符串，把符合pattern格式的字符串放入字符串数组  
	    // java.util.regex是一个用正则表达式所订制的模式来对字符串进行匹配工作的类库包  
	    public static String[] getStringArrayByPattern(String str, String pattern) {  
	        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);  
	        Matcher matcher = p.matcher(str);  
	        // 范型  
	        Set<String> result = new HashSet<String>();// 目的是：相同的字符串只返回一个。。。 不重复元素  
	        // boolean find() 尝试在目标字符串里查找下一个匹配子串。  
	        while (matcher.find()) {  
	            for (int i = 0; i < matcher.groupCount(); i++) { // int groupCount()  
	                                                             // 返回当前查找所获得的匹配组的数量。  
	                // System.out.println(matcher.group(i));  
	                result.add(matcher.group(i));  
	                  
	            }  
	        }  
	        String[] resultStr = null;  
	        if (result.size() > 0) {  
	            resultStr = new String[result.size()];  
	            return result.toArray(resultStr);// 将Set result转化为String[] resultStr  
	        }  
	        return resultStr;  
	          
	    }  
	      
	    /** 
	     * 得到第一个b,e之间的字符串,并返回e后的子串 
	     *  
	     * @param s 
	     *            源字符串 
	     * @param b 
	     *            标志开始 
	     * @param e 
	     *            标志结束 
	     * @return b,e之间的字符串 
	     */  
	      
	    /* 
	     * String aaa="abcdefghijklmn"; String[] bbb=StringProcessor.midString(aaa, 
	     * "b","l"); System.out.println("bbb[0]:"+bbb[0]);//cdefghijk 
	     * System.out.println("bbb[1]:"+bbb[1]);//lmn 
	     * ★这个方法是得到第二个参数和第三个参数之间的字符串,赋给元素0;然后把元素0代表的字符串之后的,赋给元素1 
	     */  
	      
	    /* 
	     * String aaa="abcdefgllhijklmn5465"; String[] 
	     * bbb=StringProcessor.midString(aaa, "b","l"); //ab cdefg llhijklmn5465 // 
	     * 元素0 元素1 
	     */  
	    public static String[] midString(String s, String b, String e) {  
	        int i = s.indexOf(b) + b.length();  
	        int j = s.indexOf(e, i);  
	        String[] sa = new String[2];  
	        if (i < b.length() || j < i + 1 || i > j) {  
	            sa[1] = s;  
	            sa[0] = null;  
	            return sa;  
	        } else {  
	            sa[0] = s.substring(i, j);  
	            sa[1] = s.substring(j);  
	            return sa;  
	        }  
	    }  
	      
	    /** 
	     * 带有前一次替代序列的正则表达式替代 
	     *  
	     * @param s 
	     * @param pf 
	     * @param pb 
	     * @param start 
	     * @return 
	     */  
	    public static String stringReplace(String s, String pf, String pb, int start) {  
	        Pattern pattern_hand = Pattern.compile(pf);  
	        Matcher matcher_hand = pattern_hand.matcher(s);  
	        int gc = matcher_hand.groupCount();  
	        int pos = start;  
	        String sf1 = "";  
	        String sf2 = "";  
	        String sf3 = "";  
	        int if1 = 0;  
	        String strr = "";  
	        while (matcher_hand.find(pos)) {  
	            sf1 = matcher_hand.group();  
	            if1 = s.indexOf(sf1, pos);  
	            if (if1 >= pos) {  
	                strr += s.substring(pos, if1);  
	                pos = if1 + sf1.length();  
	                sf2 = pb;  
	                for (int i = 1; i <= gc; i++) {  
	                    sf3 = "\\" + i;  
	                    sf2 = replaceAll(sf2, sf3, matcher_hand.group(i));  
	                }  
	                strr += sf2;  
	            } else {  
	                return s;  
	            }  
	        }  
	        strr = s.substring(0, start) + strr;  
	        return strr;  
	    }  
	      
	    /** 
	     * 存文本替换 
	     *  
	     * @param s 
	     *            源字符串 
	     * @param sf 
	     *            子字符串 
	     * @param sb 
	     *            替换字符串 
	     * @return 替换后的字符串 
	     */  
	    public static String replaceAll(String s, String sf, String sb) {  
	        int i = 0, j = 0;  
	        int l = sf.length();  
	        boolean b = true;  
	        boolean o = true;  
	        String str = "";  
	        do {  
	            j = i;  
	            i = s.indexOf(sf, j);  
	            if (i > j) {  
	                str += s.substring(j, i);  
	                str += sb;  
	                i += l;  
	                o = false;  
	            } else {  
	                str += s.substring(j);  
	                b = false;  
	            }  
	        } while (b);  
	        if (o) {  
	            str = s;  
	        }  
	        return str;  
	    }  
	      
	    /** 
	     * 判断是否与给定字符串样式匹配 
	     *  
	     * @param str 
	     *            字符串 
	     * @param pattern 
	     *            正则表达式样式 
	     * @return 是否匹配是true,否false 
	     */  
	    public static boolean isMatch(String str, String pattern) {  
	        Pattern pattern_hand = Pattern.compile(pattern);  
	        Matcher matcher_hand = pattern_hand.matcher(str);  
	        boolean b = matcher_hand.matches();  
	        return b;  
	    }  
	      
	    /** 
	     * 截取字符串 
	     *  
	     * @param s 
	     *            源字符串 
	     * @param jmp 
	     *            跳过jmp 
	     * @param sb 
	     *            取在sb 
	     * @param se 
	     *            于se 
	     * @return 之间的字符串 
	     */  
	    public static String subStringExe(String s, String jmp, String sb, String se) {  
	        if (isEmpty(s)) {  
	            return "";  
	        }  
	        int i = s.indexOf(jmp);  
	        if (i >= 0 && i < s.length()) {  
	            s = s.substring(i + 1);  
	        }  
	        i = s.indexOf(sb);  
	        if (i >= 0 && i < s.length()) {  
	            s = s.substring(i + 1);  
	        }  
	        if (se == "") {  
	            return s;  
	        } else {  
	            i = s.indexOf(se);  
	            if (i >= 0 && i < s.length()) {  
	                s = s.substring(i + 1);  
	            }  
	            return s;  
	        }  
	    }  
	      
	    /** 
	     * 用要通过URL传输的内容进行编码 
	     *  
	     * @param 源字符串 
	     * @return 经过编码的内容 
	     */  
	    public static String URLEncode(String src) {  
	        String return_value = "";  
	        try {  
	            if (src != null) {  
	                return_value = URLEncoder.encode(src, "GBK");  
	                  
	            }  
	        } catch (UnsupportedEncodingException e) {  
	            e.printStackTrace();  
	            return_value = src;  
	        }  
	          
	        return return_value;  
	    }  
	      
	    /** 
	     * 换成GBK的字符串 
	     *  
	     * @param str 
	     * @return 
	     */  
	    public static String getGBK(String str) {  
	          
	        return transfer(str);  
	    }  
	      
	    public static String transfer(String str) {  
	        Pattern p = Pattern.compile("&#\\d+;");  
	        Matcher m = p.matcher(str);  
	        while (m.find()) {  
	            String old = m.group();  
	            str = str.replaceAll(old, getChar(old));  
	        }  
	        return str;  
	    }  
	      
	    public static String getChar(String str) {  
	        String dest = str.substring(2, str.length() - 1);  
	        char ch = (char) Integer.parseInt(dest);  
	        return "" + ch;  
	    }  
	      
	    /** 
	     * 首页中切割字符串. 
	     *  
	     * @date 2007-09-17 
	     * @param str 
	     * @return 
	     */  
	    public static String subYhooString(String subject, int size) {  
	        subject = subject.substring(1, size);  
	        return subject;  
	    }  
	      
	    public static String subYhooStringDot(String subject, int size) {  
	        subject = subject.substring(1, size) + "...";  
	        return subject;  
	    }  
	      
	    /** 
	     * 泛型方法(通用)，把list转换成以“,”相隔的字符串 调用时注意类型初始化（申明类型） 如：List<Integer> intList = 
	     * new ArrayList<Integer>(); 调用方法：StringUtil.listTtoString(intList); 
	     * 效率：list中4条信息，1000000次调用时间为850ms左右 
	     *  
	     * @param <T> 
	     *            泛型 
	     * @param list 
	     *            list列表 
	     * @return 以“,”相隔的字符串 
	     */  
	    public static <T> String listTtoString(List<T> list) {  
	        if (list == null || list.size() < 1)  
	            return "";  
	        Iterator<T> i = list.iterator();  
	        if (!i.hasNext())  
	            return "";  
	        StringBuilder sb = new StringBuilder();  
	        for (;;) {  
	            T e = i.next();  
	            sb.append(e);  
	            if (!i.hasNext())  
	                return sb.toString();  
	            sb.append(",");  
	        }  
	    }  
	      
	    /** 
	     * 把整形数组转换成以“,”相隔的字符串 
	     *  
	     * @param a 
	     *            数组a 
	     * @return 以“,”相隔的字符串 
	     */  
	    public static String intArraytoString(int[] a) {  
	        if (a == null)  
	            return "";  
	        int iMax = a.length - 1;  
	        if (iMax == -1)  
	            return "";  
	        StringBuilder b = new StringBuilder();  
	        for (int i = 0;; i++) {  
	            b.append(a[i]);  
	            if (i == iMax)  
	                return b.toString();  
	            b.append(",");  
	        }  
	    }  
	      
	    /** 
	     * 判断文字内容重复 
	     *  
	     * @Date 2008-04-17 
	     */  
	    public static boolean isContentRepeat(String content) {  
	        int similarNum = 0;  
	        int forNum = 0;  
	        int subNum = 0;  
	        int thousandNum = 0;  
	        String startStr = "";  
	        String nextStr = "";  
	        boolean result = false;  
	        float endNum = (float) 0.0;  
	        if (content != null && content.length() > 0) {  
	            if (content.length() % 1000 > 0)  
	                thousandNum = (int) Math.floor(content.length() / 1000) + 1;  
	            else  
	                thousandNum = (int) Math.floor(content.length() / 1000);  
	            if (thousandNum < 3)  
	                subNum = 100 * thousandNum;  
	            else if (thousandNum < 6)  
	                subNum = 200 * thousandNum;  
	            else if (thousandNum < 9)  
	                subNum = 300 * thousandNum;  
	            else  
	                subNum = 3000;  
	            for (int j = 1; j < subNum; j++) {  
	                if (content.length() % j > 0)  
	                    forNum = (int) Math.floor(content.length() / j) + 1;  
	                else  
	                    forNum = (int) Math.floor(content.length() / j);  
	                if (result || j >= content.length())  
	                    break;  
	                else {  
	                    for (int m = 0; m < forNum; m++) {  
	                        if (m * j > content.length() || (m + 1) * j > content.length()  
	                                || (m + 2) * j > content.length())  
	                            break;  
	                        startStr = content.substring(m * j, (m + 1) * j);  
	                        nextStr = content.substring((m + 1) * j, (m + 2) * j);  
	                        if (startStr.equals(nextStr)) {  
	                            similarNum = similarNum + 1;  
	                            endNum = (float) similarNum / forNum;  
	                            if (endNum > 0.4) {  
	                                result = true;  
	                                break;  
	                            }  
	                        } else  
	                            similarNum = 0;  
	                    }  
	                }  
	            }  
	        }  
	        return result;  
	    }  
	      
	    /** 
	     * Ascii转为Char 
	     *  
	     * @param asc 
	     * @return 
	     */  
	    public static String AsciiToChar(int asc) {  
	        String TempStr = "A";  
	        char tempchar = (char) asc;  
	        TempStr = String.valueOf(tempchar);  
	        return TempStr;  
	    }  
	      
	    /** 
	     * 判断是否是空字符串 null和"" null返回result,否则返回字符串 
	     *  
	     * @param s 
	     * @return 
	     */  
	    public static String isEmpty(String s, String result) {  
	        if (s != null && !s.equals("")) {  
	            return s;  
	        }  
	        return result;  
	    }  
	      
	    /** 
	     * 移除html标签 
	     *  
	     * @param htmlstr 
	     * @return 
	     */  
	    public static String removeHtmlTag(String htmlstr) {  
	        Pattern pat = Pattern.compile("\\s*<.*?>\\s*", Pattern.DOTALL | Pattern.MULTILINE  
	                | Pattern.CASE_INSENSITIVE); // \\s?[s|Sc|Cr|Ri|Ip|Pt|T]  
	        Matcher m = pat.matcher(htmlstr);  
	        String rs = m.replaceAll("");  
	        rs = rs.replaceAll(" ", " ");  
	        rs = rs.replaceAll("<", "<");  
	        rs = rs.replaceAll(">", ">");  
	        return rs;  
	    }  
	      
	    /** 
	     * 取从指定搜索项开始的字符串，返回的值不包含搜索项 
	     *  
	     * @param captions 
	     *            例如:"www.koubei.com" 
	     * @param regex 
	     *            分隔符，例如"." 
	     * @return 结果字符串，如：koubei.com，如未找到返回空串 
	     */  
	    public static String getStrAfterRegex(String captions, String regex) {  
	        if (!isEmpty(captions) && !isEmpty(regex)) {  
	            int pos = captions.indexOf(regex);  
	            if (pos != -1 && pos < captions.length() - 1) {  
	                return captions.substring(pos + 1);  
	            }  
	        }  
	        return "";  
	    }  
	      
	    /** 
	     * 把字节码转换成16进制 
	     */  
	    public static String byte2hex(byte bytes[]) {  
	        StringBuffer retString = new StringBuffer();  
	        for (int i = 0; i < bytes.length; ++i) {  
	            retString.append(Integer.toHexString(0x0100 + (bytes[i] & 0x00FF)).substring(1).toUpperCase());  
	        }  
	        return retString.toString();  
	    }  
	      
	    /** 
	     * 把16进制转换成字节码 
	     *  
	     * @param hex 
	     * @return 
	     */  
	    public static byte[] hex2byte(String hex) {  
	        byte[] bts = new byte[hex.length() / 2];  
	        for (int i = 0; i < bts.length; i++) {  
	            bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);  
	        }  
	        return bts;  
	    }  
	      
	    /** 
	     * 转换数字为固定长度的字符串 
	     *  
	     * @param length 
	     *            希望返回的字符串长度 
	     * @param data 
	     *            传入的数值 
	     * @return 
	     */  
	    public static String getStringByInt(int length, String data) {  
	        String s_data = "";  
	        int datalength = data.length();  
	        if (length > 0 && length >= datalength) {  
	            for (int i = 0; i < length - datalength; i++) {  
	                s_data += "0";  
	            }  
	            s_data += data;  
	        }  
	          
	        return s_data;  
	    }  
	      
	    /** 
	     * 判断是否位数字,并可为空 
	     *  
	     * @param src 
	     * @return 
	     */  
	    public static boolean isNumericAndCanNull(String src) {  
	        Pattern numericPattern = Pattern.compile("^[0-9]+$");  
	        if (src == null || src.equals(""))  
	            return true;  
	        boolean return_value = false;  
	        if (src != null && src.length() > 0) {  
	            Matcher m = numericPattern.matcher(src);  
	            if (m.find()) {  
	                return_value = true;  
	            }  
	        }  
	        return return_value;  
	    }  
	      
	    /** 
	     * @param src 
	     * @return 
	     */  
	    public static boolean isFloatAndCanNull(String src) {  
	        Pattern numericPattern = Pattern.compile("^(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*))$");  
	        if (src == null || src.equals(""))  
	            return true;  
	        boolean return_value = false;  
	        if (src != null && src.length() > 0) {  
	            Matcher m = numericPattern.matcher(src);  
	            if (m.find()) {  
	                return_value = true;  
	            }  
	        }  
	        return return_value;  
	    }  
	    /**
	     * 不为空
	     * @param str
	     * @return
	     */
	    public static boolean isNotEmpty(String str) {  
	        if (str != null && !str.equals(""))  
	            return true;  
	        else  
	            return false;  
	    }  
	     /**
	      * 是日期
	      * @param date
	      * @return
	      */
	    public static boolean isDate(String date) {  
	        String regEx = "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}";  
	        Pattern p = Pattern.compile(regEx);  
	        Matcher m = p.matcher(date);  
	        boolean result = m.find();  
	        return result;  
	    }  
	      
	    public static boolean isFormatDate(String date, String regEx) {  
	        Pattern p = Pattern.compile(regEx);  
	        Matcher m = p.matcher(date);  
	        boolean result = m.find();  
	        return result;  
	    }  
	      
	    /** 
	     * 根据指定整型list 组装成为一个字符串 
	     *  
	     * @param src 
	     * @return 
	     */  
	    public static String listToString(List<Integer> list) {  
	        String str = "";  
	        if (list != null && list.size() > 0) {  
	            for (int id : list) {  
	                str = str + id + ",";  
	            }  
	            if (!"".equals(str) && str.length() > 0)  
	                str = str.substring(0, str.length() - 1);  
	        }  
	        return str;  
	    }  
	      
	    /** 
	     * 页面的非法字符检查 
	     *  
	     * @param str 
	     * @return 
	     */  
	    public static String replaceStr(String str) {  
	        if (str != null && str.length() > 0) {  
	            str = str.replaceAll("~", ",");  
	            str = str.replaceAll(" ", ",");  
	            str = str.replaceAll("　", ",");  
	            str = str.replaceAll(" ", ",");  
	            str = str.replaceAll("`", ",");  
	            str = str.replaceAll("!", ",");  
	            str = str.replaceAll("@", ",");  
	            str = str.replaceAll("#", ",");  
	            str = str.replaceAll("\\$", ",");  
	            str = str.replaceAll("%", ",");  
	            str = str.replaceAll("\\^", ",");  
	            str = str.replaceAll("&", ",");  
	            str = str.replaceAll("\\*", ",");  
	            str = str.replaceAll("\\(", ",");  
	            str = str.replaceAll("\\)", ",");  
	            str = str.replaceAll("-", ",");  
	            str = str.replaceAll("_", ",");  
	            str = str.replaceAll("=", ",");  
	            str = str.replaceAll("\\+", ",");  
	            str = str.replaceAll("\\{", ",");  
	            str = str.replaceAll("\\[", ",");  
	            str = str.replaceAll("\\}", ",");  
	            str = str.replaceAll("\\]", ",");  
	            str = str.replaceAll("\\|", ",");  
	            str = str.replaceAll("\\\\", ",");  
	            str = str.replaceAll(";", ",");  
	            str = str.replaceAll(":", ",");  
	            str = str.replaceAll("'", ",");  
	            str = str.replaceAll("\\\"", ",");  
	            str = str.replaceAll("<", ",");  
	            str = str.replaceAll(">", ",");  
	            str = str.replaceAll("\\.", ",");  
	            str = str.replaceAll("\\?", ",");  
	            str = str.replaceAll("/", ",");  
	            str = str.replaceAll("～", ",");  
	            str = str.replaceAll("`", ",");  
	            str = str.replaceAll("！", ",");  
	            str = str.replaceAll("＠", ",");  
	            str = str.replaceAll("＃", ",");  
	            str = str.replaceAll("＄", ",");  
	            str = str.replaceAll("％", ",");  
	            str = str.replaceAll("︿", ",");  
	            str = str.replaceAll("＆", ",");  
	            str = str.replaceAll("×", ",");  
	            str = str.replaceAll("（", ",");  
	            str = str.replaceAll("）", ",");  
	            str = str.replaceAll("－", ",");  
	            str = str.replaceAll("＿", ",");  
	            str = str.replaceAll("＋", ",");  
	            str = str.replaceAll("＝", ",");  
	            str = str.replaceAll("｛", ",");  
	            str = str.replaceAll("［", ",");  
	            str = str.replaceAll("｝", ",");  
	            str = str.replaceAll("］", ",");  
	            str = str.replaceAll("｜", ",");  
	            str = str.replaceAll("＼", ",");  
	            str = str.replaceAll("：", ",");  
	            str = str.replaceAll("；", ",");  
	            str = str.replaceAll("＂", ",");  
	            str = str.replaceAll("＇", ",");  
	            str = str.replaceAll("＜", ",");  
	            str = str.replaceAll("，", ",");  
	            str = str.replaceAll("＞", ",");  
	            str = str.replaceAll("．", ",");  
	            str = str.replaceAll("？", ",");  
	            str = str.replaceAll("／", ",");  
	            str = str.replaceAll("·", ",");  
	            str = str.replaceAll("￥", ",");  
	            str = str.replaceAll("……", ",");  
	            str = str.replaceAll("（", ",");  
	            str = str.replaceAll("）", ",");  
	            str = str.replaceAll("——", ",");  
	            str = str.replaceAll("-", ",");  
	            str = str.replaceAll("【", ",");  
	            str = str.replaceAll("】", ",");  
	            str = str.replaceAll("、", ",");  
	            str = str.replaceAll("”", ",");  
	            str = str.replaceAll("’", ",");  
	            str = str.replaceAll("《", ",");  
	            str = str.replaceAll("》", ",");  
	            str = str.replaceAll("“", ",");  
	            str = str.replaceAll("。", ",");  
	        }  
	        return str;  
	    }  
	      
	    /** 
	     * 全角字符变半角字符 
	     *  
	     * @param str 
	     * @return 
	     */  
	    public static String full2Half(String str) {  
	        if (str == null || "".equals(str))  
	            return "";  
	        StringBuffer sb = new StringBuffer();  
	          
	        for (int i = 0; i < str.length(); i++) {  
	            char c = str.charAt(i);  
	              
	            if (c >= 65281 && c < 65373)  
	                sb.append((char) (c - 65248));  
	            else  
	                sb.append(str.charAt(i));  
	        }  
	          
	        return sb.toString();  
	          
	    }  
	      
	    /** 
	     * 全角括号转为半角 
	     *  
	     * @param str 
	     * @return 
	     */  
	    public static String replaceBracketStr(String str) {  
	        if (str != null && str.length() > 0) {  
	            str = str.replaceAll("（", "(");  
	            str = str.replaceAll("）", ")");  
	        }  
	        return str;  
	    }  
	      
	    /** 
	     * 分割字符，从开始到第一个split字符串为止 
	     *  
	     * @param src 
	     *            源字符串 
	     * @param split 
	     *            截止字符串 
	     * @return 
	     */  
	    public static String subStr(String src, String split) {  
	        if (!isEmpty(src)) {  
	            int index = src.indexOf(split);  
	            if (index >= 0) {  
	                return src.substring(0, index);  
	            }  
	        }  
	        return src;  
	    }  
	      
	    /** 
	     * 取url里的keyword（可选择参数）参数，用于整站搜索整合 
	     *  
	     * @param params 
	     * @param qString 
	     * @return 
	     */  
	    public static String getKeyWord(String params, String qString) {  
	        String keyWord = "";  
	        if (qString != null) {  
	            String param = params + "=";  
	            int i = qString.indexOf(param);  
	            if (i != -1) {  
	                int j = qString.indexOf("&", i + param.length());  
	                if (j > 0) {  
	                    keyWord = qString.substring(i + param.length(), j);  
	                }  
	            }  
	        }  
	        return keyWord;  
	    }  
	      
	    /** 
	     * 解析字符串返回map键值对(例：a=1&b=2 => a=1,b=2) 
	     *  
	     * @param query 
	     *            源参数字符串 
	     * @param split1 
	     *            键值对之间的分隔符（例：&） 
	     * @param split2 
	     *            key与value之间的分隔符（例：=） 
	     * @param dupLink 
	     *            重复参数名的参数值之间的连接符，连接后的字符串作为该参数的参数值，可为null 
	     *            null：不允许重复参数名出现，则靠后的参数值会覆盖掉靠前的参数值。 
	     * @return map 
	     */  
	    @SuppressWarnings("unchecked")  
	    public static Map<String, String> parseQuery(String query, char split1, char split2,  
	            String dupLink) {  
	        if (!isEmpty(query) && query.indexOf(split2) > 0) {  
	            Map<String, String> result = new HashMap();  
	              
	            String name = null;  
	            String value = null;  
	            String tempValue = "";  
	            int len = query.length();  
	            for (int i = 0; i < len; i++) {  
	                char c = query.charAt(i);  
	                if (c == split2) {  
	                    value = "";  
	                } else if (c == split1) {  
	                    if (!isEmpty(name) && value != null) {  
	                        if (dupLink != null) {  
	                            tempValue = result.get(name);  
	                            if (tempValue != null) {  
	                                value += dupLink + tempValue;  
	                            }  
	                        }  
	                        result.put(name, value);  
	                    }  
	                    name = null;  
	                    value = null;  
	                } else if (value != null) {  
	                    value += c;  
	                } else {  
	                    name = (name != null) ? (name + c) : "" + c;  
	                }  
	            }  
	              
	            if (!isEmpty(name) && value != null) {  
	                if (dupLink != null) {  
	                    tempValue = result.get(name);  
	                    if (tempValue != null) {  
	                        value += dupLink + tempValue;  
	                    }  
	                }  
	                result.put(name, value);  
	            }  
	              
	            return result;  
	        }  
	        return null;  
	    }  
	      
	    /** 
	     * 将list 用传入的分隔符组装为String 
	     *  
	     * @param list 
	     * @param slipStr 
	     * @return String 
	     */  
	    @SuppressWarnings("unchecked")  
	    public static String listToStringSlipStr(List list, String slipStr) {  
	        StringBuffer returnStr = new StringBuffer();  
	        if (list != null && list.size() > 0) {  
	            for (int i = 0; i < list.size(); i++) {  
	                returnStr.append(list.get(i)).append(slipStr);  
	            }  
	        }  
	        if (returnStr.toString().length() > 0)  
	            return returnStr.toString().substring(0, returnStr.toString().lastIndexOf(slipStr));  
	        else  
	            return "";  
	    }  
	      
	    /** 
	     * 获取从start开始用*替换len个长度后的字符串 
	     *  
	     * @param str 
	     *            要替换的字符串 
	     * @param start 
	     *            开始位置 
	     * @param len 
	     *            长度 
	     * @return 替换后的字符串 
	     */  
	    public static String getMaskStr(String str, int start, int len) {  
	        if (StringUtil.isEmpty(str)) {  
	            return str;  
	        }  
	        if (str.length() < start) {  
	            return str;  
	        }  
	          
	        // 获取*之前的字符串  
	        String ret = str.substring(0, start);  
	          
	        // 获取最多能打的*个数  
	        int strLen = str.length();  
	        if (strLen < start + len) {  
	            len = strLen - start;  
	        }  
	          
	        // 替换成*  
	        for (int i = 0; i < len; i++) {  
	            ret += "*";  
	        }  
	          
	        // 加上*之后的字符串  
	        if (strLen > start + len) {  
	            ret += str.substring(start + len);  
	        }  
	          
	        return ret;  
	    }  
	      
	    /** 
	     * 根据传入的分割符号,把传入的字符串分割为List字符串 
	     *  
	     * @param slipStr 
	     *            分隔的字符串 
	     * @param src 
	     *            字符串 
	     * @return 列表 
	     */  
	    public static List<String> stringToStringListBySlipStr(String slipStr, String src) {  
	          
	        if (src == null)  
	            return null;  
	        List<String> list = new ArrayList<String>();  
	        String[] result = src.split(slipStr);  
	        for (int i = 0; i < result.length; i++) {  
	            list.add(result[i]);  
	        }  
	        return list;  
	    }  
	      
	    /** 
	     * 截取字符串 
	     *  
	     * @param str 
	     *            原始字符串 
	     * @param len 
	     *            要截取的长度 
	     * @param tail 
	     *            结束加上的后缀 
	     * @return 截取后的字符串 
	     */  
	    public static String getHtmlSubString(String str, int len, String tail) {  
	        if (str == null || str.length() <= len) {  
	            return str;  
	        }  
	        int length = str.length();  
	        char c = ' ';  
	        String tag = null;  
	        String name = null;  
	        int size = 0;  
	        String result = "";  
	        boolean isTag = false;  
	        List<String> tags = new ArrayList<String>();  
	        int i = 0;  
	        for (int end = 0, spanEnd = 0; i < length && len > 0; i++) {  
	            c = str.charAt(i);  
	            if (c == '<') {  
	                end = str.indexOf('>', i);  
	            }  
	              
	            if (end > 0) {  
	                // 截取标签  
	                tag = str.substring(i, end + 1);  
	                int n = tag.length();  
	                if (tag.endsWith("/>")) {  
	                    isTag = true;  
	                } else if (tag.startsWith("</")) { // 结束符  
	                    name = tag.substring(2, end - i);  
	                    size = tags.size() - 1;  
	                    // 堆栈取出html开始标签  
	                    if (size >= 0 && name.equals(tags.get(size))) {  
	                        isTag = true;  
	                        tags.remove(size);  
	                    }  
	                } else { // 开始符  
	                    spanEnd = tag.indexOf(' ', 0);  
	                    spanEnd = spanEnd > 0 ? spanEnd : n;  
	                    name = tag.substring(1, spanEnd);  
	                    if (name.trim().length() > 0) {  
	                        // 如果有结束符则为html标签  
	                        spanEnd = str.indexOf("</" + name + ">", end);  
	                        if (spanEnd > 0) {  
	                            isTag = true;  
	                            tags.add(name);  
	                        }  
	                    }  
	                }  
	                // 非html标签字符  
	                if (!isTag) {  
	                    if (n >= len) {  
	                        result += tag.substring(0, len);  
	                        break;  
	                    } else {  
	                        len -= n;  
	                    }  
	                }  
	                  
	                result += tag;  
	                isTag = false;  
	                i = end;  
	                end = 0;  
	            } else { // 非html标签字符  
	                len--;  
	                result += c;  
	            }  
	        }  
	        // 添加未结束的html标签  
	        for (String endTag : tags) {  
	            result += "</" + endTag + ">";  
	        }  
	        if (i < length) {  
	            result += tail;  
	        }  
	        return result;  
	    }  
	    
	    public static String getObjectStr(Object o){
	    	if(o == null)
	    		return "";
	    	else
	    		return o.toString();
	    }
}