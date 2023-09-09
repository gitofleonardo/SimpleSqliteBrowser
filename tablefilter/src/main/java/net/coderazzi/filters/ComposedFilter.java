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

import java.util.HashSet;
import java.util.Set;


/**
 * <p>Abstract parent class to support the composition of multiple filters.</p>
 *
 * <p>The exact composition semantics (and / or / not) are not defined.</p>
 *
 * @author  Luis M Pena - lu@coderazzi.net
 */
abstract public class ComposedFilter extends Filter implements IFilterObserver {

    /** Set of associated IFilters. */
    protected Set<IFilter> filters;

    /** disabled filters. */
    private Set<IFilter> disabledFilters = new HashSet<IFilter>();

    /** Default constructor. */
    protected ComposedFilter() {
        filters = new HashSet<IFilter>();
    }

    /**
     * Constructor built up out of one or more {@link
     * IFilter} instances.
     */
    protected ComposedFilter(IFilter... observables) {
        this();
        addFilter(observables);
    }

    /**
     * Subscribes one or more {@link IFilter} instances to
     * receive filter events from this composition filter.
     */
    public void addFilter(IFilter... filtersToAdd) {
        for (IFilter filter : filtersToAdd) {
            if (filters.add(filter)) {
                filter.addFilterObserver(this);
                if (filter.isEnabled()) {
                    super.setEnabled(true);
                } else {
                    disabledFilters.add(filter);
                }
            }
        }
    }

    /**
     * Unsubscribes one or more {@link IFilter}s that were
     * previously subscribed to receive filter events.
     */
    public void removeFilter(IFilter... filtersToRemove) {
        boolean report = false;
        for (IFilter filter : filtersToRemove) {
            if (filters.remove(filter)) {
                filter.removeFilterObserver(this);
                disabledFilters.remove(filter);
                report = true;
            }
        }

        if (report) {
            if (isEnabled() && !filters.isEmpty()
                    && (disabledFilters.size() == filters.size())) {
                super.setEnabled(false);
            } else {
                reportFilterUpdatedToObservers();
            }
        }
    }

    /**
     * Returns all {@link IFilter} instances previously
     * added.
     */
    public Set<IFilter> getFilters() {
        return new HashSet<IFilter>(filters);
    }

    /** @see  IFilterObserver#filterUpdated(IFilter) */
    @Override public void filterUpdated(IFilter filter) {
        boolean enabled = isEnabled();
        boolean changeState = false;
        if (filter.isEnabled()) {
            changeState = disabledFilters.remove(filter) && !enabled;
        } else {
            changeState = disabledFilters.add(filter)
                    && (disabledFilters.size() == filters.size());
        }

        if (changeState) {
            super.setEnabled(!enabled);
        } else {
            reportFilterUpdatedToObservers();
        }
    }

    /** @see  IFilter#setEnabled(boolean) */
    @Override public void setEnabled(boolean enable) {
        if (filters.isEmpty()) {
            super.setEnabled(enable);
        } else {
            // perhaps some filter will not honor the request
            // super.setEnabled is now only call when the filters report
            // its update
            for (IFilter filter : filters) {
                filter.setEnabled(enable);
            }
        }
    }

    /** Returns true if there is information of this filter as disabled. */
    protected boolean isDisabled(IFilter filter) {
        return disabledFilters.contains(filter);
    }

}
