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

import java.beans.PropertyChangeListener;

import java.text.Format;

import java.util.Comparator;

import net.coderazzi.filters.IParser;


/**
 * Interface defining the model required to use and create {@link IParser}
 * instances.
 *
 * @author  Luis M Pena - lu@coderazzi.net
 */
public interface IParserModel {

    /** Property fired when the ignore case value changes. */
    String IGNORE_CASE_PROPERTY = "ignoreCase";

    /** Property fired when any class' comparator changes. */
    String COMPARATOR_PROPERTY = "comparator";

    /** Property fired when any class' format changes. */
    String FORMAT_PROPERTY = "format";

    /** Creates a text parser for the given editor. */
    IParser createParser(IFilterEditor editor);

    /** Returns the {@link Format} for the given class. */
    Format getFormat(Class c);

    /** Defines the {@link Format} for the given class. */
    void setFormat(Class c, Format format);

    /**
     * Returns the {@link Comparator} for the given class.<br>
     * It never returns null.
     */
    Comparator getComparator(Class c);

    /** Defines the {@link Comparator} for the given class. */
    void setComparator(Class c, Comparator format);

    /** Returns the {@link Comparator} used for String comparisons. */
    Comparator<String> getStringComparator(boolean ignoreCase);

    /** Sets a String comparator that is case sensitive/insensitive. */
    void setIgnoreCase(boolean set);

    /**
     * Returns true if the String comparator ignores case<br>
     * Note that this is redundant information, which can be retrieved from the
     * {@link #getComparator(Class)} method with a String.class parameter.
     */
    boolean isIgnoreCase();

    /**
     * Adds a {@link PropertyChangeListener}.<br>
     * Any property change will be transmitted as an event
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /** Removes an existing {@link PropertyChangeListener}. */
    void removePropertyChangeListener(PropertyChangeListener listener);

}
