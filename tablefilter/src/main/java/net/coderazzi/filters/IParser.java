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

package net.coderazzi.filters;

import java.text.ParseException;

import javax.swing.RowFilter;

import net.coderazzi.filters.gui.IFilterEditor;


/**
 * Interface defining the requirements on text parsing for filter expressions.
 * <br>
 * Starting on version 4.3, the parser is also able to handle html content;
 * in this case, the parser accepts simple text, but the created filter can
 * be applied to Html content 
 *
 * @author  Luis M Pena - lu@coderazzi.net
 */
public interface IParser {

    /**
     * Parses the text, returning a filter that can be applied to the table.
     *
     * @param  expression  the text to parse
     */
    RowFilter parseText(String expression) throws ParseException;

    /**
     * Parses the text, considered to be a part of the whole text to enter.<br>
     *
     * <p>The behaviour of this method is implementation specific; the default
     * implementation considers the expression to be the beginning of the
     * expected final string</p>
     *
     * <p>This method is invoked when the user inputs text on a filter editor,
     * if instant parsing is enabled, and if the text entered so far does not
     * match any table's row value for the associated column.</p>
     *
     * <p>Alternative implementations that would consider matching the provided
     * expression to any substring ('contain' meaning), should set the
     * autoCompletion flag in the {@link IFilterEditor}to false</p>
     *
     * @param   expression  the text to parse
     *
     * @return  the filter plus the real expression used to create the filter
     */
    InstantFilter parseInstantText(String expression) throws ParseException;

    /**
     * Escapes a given expression, such that, when parsed, the parser will make
     * no character/operator substitutions.
     */
    String escape(String s);

    /**
     * Removes any Html content from the passed string, converting special
     * Html characters to Java characters.
     */
    String stripHtml(String s);

    /** Helper class used on {@link IParser#parseInstantText(String)}. */
    public class InstantFilter {
        public RowFilter filter;
        public String    expression;
    }

}
