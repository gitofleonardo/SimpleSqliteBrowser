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

package net.coderazzi.filters.gui;

import net.coderazzi.filters.IParser;
import net.coderazzi.filters.parser.DateComparator;
import net.coderazzi.filters.parser.Parser;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Default {@link Format} instances, supporting all the basic java types<br>
 * It also includes support for {@link Comparator} of {@link Date} instances.
 * <br>
 * The default {@link IParser} is automatically configured to use these {@link
 * Format} instances, when created by the {@link TableFilterHeader}.<br>
 * Users can add any {@link Format} or {@link Comparator} definitions, as the
 * class is used as a singleton.
 *
 * @author  Luis M Pena - lu@coderazzi.net
 */
public class LooseParserModel extends ParserModel {

    /** Creates the parser as required with the given parameters */
    protected IParser createParser(Format fmt, Comparator cmp,
                                  Comparator stringCmp, boolean ignoreCase,
                                  int modelIndex) {
        return new Parser(fmt, cmp, stringCmp, ignoreCase, modelIndex){
            @Override
            protected String getInstantAppliedExpression(String expression) {
                return expression;
            }

            @Override
            public IOperand getDefaultOperator(boolean instantMode) {
                return super.getDefaultOperator(true);
            }
        };
    }
}
