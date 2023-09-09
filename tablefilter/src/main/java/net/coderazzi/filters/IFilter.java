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

import javax.swing.RowFilter;


/**
 * <p>Interface to be implemented by any instance holding a filter than can be
 * updated dynamically.</p>
 *
 * <p>Any change on the filter is propagated to the observers, in no given
 * order.</p>
 *
 * @author  Luis M Pena - lu@coderazzi.net
 */
public interface IFilter {

    /** {@link RowFilter} interface. */
    boolean include(RowFilter.Entry rowEntry);

    /** Returns true if the filter is enabled. */
    boolean isEnabled();

    /** Enables/Disables the filter. */
    void setEnabled(boolean enable);

    /** Adds an observer to receive filter change notifications. */
    void addFilterObserver(IFilterObserver observer);

    /**
     * Unregisters an observer, that will not receive any further filter update
     * notifications.
     */
    void removeFilterObserver(IFilterObserver observer);
}
