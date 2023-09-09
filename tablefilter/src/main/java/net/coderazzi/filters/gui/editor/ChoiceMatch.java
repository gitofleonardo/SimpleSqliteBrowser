/**
 * Author:  Luis M Pena  ( lu@coderazzi.net )
 * License: MIT License
 *
 * Copyright (c) 2007 Luis M. Pena  -  lu@coderazzi.net
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.coderazzi.filters.gui.editor;

import java.util.Comparator;


/** Class to find matches in the lists (history / choices). */
class ChoiceMatch {
    // exact is true if the given index corresponds to an string that fully
    // matches the passed string
    boolean exact;
    // the matched content
    Object content;
    // index in the list. Will be -1 if the content is empty or a fullMatch
    // is required and cannot be found
    int index = -1;
    // length of the string that is matched, if exact is false.
    int len;

    /** Returns the number of matching characters between two strings. */
    public static int getMatchingLength(String     a,
                                        String     b,
                                        Comparator stringComparator) {
        int max = Math.min(a.length(), b.length());
        for (int i = 0; i < max; i++) {
            char f = a.charAt(i);
            char s = b.charAt(i);
            if ((f != s)
                    && (stringComparator.compare(String.valueOf(f),
                            String.valueOf(s)) != 0)) {
                return i;
            }
        }

        return max;
    }

}
