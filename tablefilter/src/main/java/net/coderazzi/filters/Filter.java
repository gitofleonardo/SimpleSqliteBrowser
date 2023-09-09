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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.RowFilter;


/**
 * Commodity class implementing the interface {@link
 * IFilter} on a {@link RowFilter}.
 *
 * @author  Luis M Pena - lu@coderazzi.net
 */
abstract public class Filter extends RowFilter implements IFilter {

    /** The set of currently subscribed observers. */
    private Set<IFilterObserver> observers = new HashSet<IFilterObserver>();

    /** The enabled state. */
    private boolean enabled = true;

    /** @see  IFilter#isEnabled() */
    @Override public boolean isEnabled() {
        return enabled;
    }

    /** @see  IFilter#setEnabled(boolean) */
    @Override public void setEnabled(boolean enable) {
        if (enable != this.enabled) {
            this.enabled = enable;
            reportFilterUpdatedToObservers();
        }
    }

    /** @see  IFilter#addFilterObserver(IFilterObserver) */
    @Override public void addFilterObserver(IFilterObserver observer) {
        observers.add(observer);
    }

    /** @see  IFilter#removeFilterObserver(IFilterObserver) */
    @Override public void removeFilterObserver(IFilterObserver observer) {
        observers.remove(observer);
    }

    /** Returns all the registered {@link IFilterObserver} instances. */
    public Set<IFilterObserver> getFilterObservers() {
        return new HashSet<IFilterObserver>(observers);
    }

    /**
     * Method to be called by subclasses to report to the observers that the
     * filter has changed.
     */
    public void reportFilterUpdatedToObservers() {
        for (IFilterObserver obs : new ArrayList<IFilterObserver>(observers)) {
            obs.filterUpdated(this);
        }
    }
}
