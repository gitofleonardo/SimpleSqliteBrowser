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

import javax.swing.table.TableColumn;


/**
 * <p>A ITableFilterHeaderObserver instance receives notifications when the
 * associated {@link IFilterEditor} instances are
 * created, destroyed, or update the held filter.</p>
 *
 * @author  Luis M Pena - lu@coderazzi.net
 */
public interface IFilterHeaderObserver {

    /**
     * <p>Informs the observer than a new filter editor is created</p>
     *
     * @param  header       the associated table filter header
     * @param  editor
     * @param  tableColumn  the associated {@link TableColumn}
     */
    void tableFilterEditorCreated(TableFilterHeader header,
                                  IFilterEditor     editor,
                                  TableColumn       tableColumn);

    /**
     * <p>Informs the observer than an existing filter editor has been excluded
     * from the filter header</p>
     *
     * @param  header       the associated table filter header
     * @param  editor
     * @param  tableColumn  the associated {@link TableColumn}
     */
    void tableFilterEditorExcluded(TableFilterHeader header,
                                   IFilterEditor     editor,
                                   TableColumn       tableColumn);

    /**
     * <p>Notification made by the {@link
     * IFilterEditor} when the filter's content is
     * updated</p>
     *
     * @param  header       the associated table filter header
     * @param  editor       the observable instance
     * @param  tableColumn  the associated {@link TableColumn}
     */
    void tableFilterUpdated(TableFilterHeader header,
                            IFilterEditor     editor,
                            TableColumn       tableColumn);
}
