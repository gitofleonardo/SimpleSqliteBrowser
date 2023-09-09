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

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.Format;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.DefaultRowSorter;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import net.coderazzi.filters.IFilter;
import net.coderazzi.filters.IFilterObserver;
import net.coderazzi.filters.IParser;
import net.coderazzi.filters.gui.editor.FilterEditor;


/**
 * <p>Implementation of a table filter that displays a set of editors associated
 * to each table's column. This is the main Gui component in this library.</p>
 *
 * <p>These editors are moved and resized as the table's columns are resized, so
 * this Swing component is better suited to be displayed atop, inline the {@link
 * JTable}, or just below, using the same size -and resizing- as the table
 * itself. The position can be automatically handled by the header itself -that
 * is the default behaviour-</p>
 *
 * <p>The editor associated to each column has the type {@link IFilterEditor},
 * and can be manipulated separately.</p>
 *
 * <p>The implementation relies on the {@link
 * FiltersHandler} class, please read its
 * documentation to understand the requirements on the table and its model, and
 * how it is affected by this filter</p>
 *
 * <p>The default settings can be modified by using system properties or by
 * setting values on the singleton {@link FilterSettings} instance</p>
 *
 * <p>Providing a filter header to an existing table is as easy as doing:</p>
 * <code>TableFilterHeader filter = new TableFilterHeader(table);</code>
 *
 * @author  Luis M Pena - lu@coderazzi.net
 */
public class TableFilterHeader extends JPanel implements PropertyChangeListener {

    private static final long serialVersionUID = 5217701111228491294L;

    /** Minimum number of visible choices -if there are choices-. */
    private static final int MIN_VISIBLE_CHOICES = 4;

    /**
     * <p>Location of the header in relation to the table</p>
     *
     * <p>Note that this location is only meaningful when the table is set
     * inside a scroll pane, and this header instance is not explicitly included
     * in a container</p>
     *
     * <ul>
     *   <li>TOP: the filter is placed automatically above the table
     *     header.</li>
     *   <li>INLINE: the filter is placed below the table header, above the
     *     table's content.</li>
     *   <li>NONE: the filter is not automatically placed.</li>
     *   <li>REPLACE: the filter replaces the header (column names).</li>
     * </ul>
     *
     * @author  Luis M Pena - lu@coderazzi.net
     */
    public enum Position {
        TOP, INLINE, NONE, REPLACE
    }

    /** whether the user has explicitly provided colors/font. */
    private boolean backgroundSet;
    private boolean foregroundSet;
    private boolean disabledBgColorSet;
    private boolean disabledColorSet;
    private boolean selectionBackgroundSet;
    private boolean selectionForegroundSet;
    private boolean selectionColorSet;
    private boolean gridColorSet;
    private boolean errorColorSet;   // the editor's content is wrong
    private boolean warningColorSet; // the filter produces no visible rows
    private boolean fontSet;

    /** The helper to handle the location of the filter in the table header. */
    private PositionHelper positionHelper = new PositionHelper(this);

    /** Appearance instance. */
    Look look = new Look();

    /** Flag to handle instant filtering support. */
    boolean instantFilteringEnabled = FilterSettings.instantFiltering;

    /** Flag to handle allowance for vanishing during instant filtering */
    boolean instantVanishingEnabled = FilterSettings.allowInstantVanishing;

    /** Flag to handle auto completion support. */
    boolean autoCompletionEnabled = FilterSettings.autoCompletion;

    /** Flag to handle auto closing popups during table updates*/
    boolean hidePopupsOnTableUpdates = FilterSettings.hidePopupsOnTableUpdates;

    /** This is the total max number of visible rows (history PLUS choices). */
    int maxHistory = FilterSettings.maxPopupHistory;

    /** Setting to add / decrease height to the filter row. */
    int filterRowHeightDelta = FilterSettings.filterRowHeightDelta;

    /**
     * The columnsController is a glue component, controlling the filters
     * associated to each column.
     */
    FilterColumnsControllerPanel columnsController;

    /**
     * The privately owned instance of FiltersHandler that conforms the filter
     * defined by the TableFilterHeader.
     */
    FiltersHandler filtersHandler;

    /** The set of currently subscribed observers. */
    Set<IFilterHeaderObserver> observers = new HashSet<IFilterHeaderObserver>();

    /** Helper to revalidate the controller when the table changes size. */
    private ComponentAdapter resizer = new ComponentAdapter() {
        @Override public void componentResized(ComponentEvent e) {
            if (columnsController != null) {
                columnsController.revalidate();
            }
        }
    };

    /** Basic constructor, requires an attached table. */
    public TableFilterHeader() {
        this(null, null, null);
    }

    /** Basic constructor, using default {@link IParserModel}. */
    public TableFilterHeader(JTable table) {
        this(table, null, null);
    }

    /** Advanced constructor, enabling setting the {@link AutoChoices} mode */
    public TableFilterHeader(JTable table, AutoChoices mode) {
        this(table, null, mode);
    }

    /** Advanced constructor. */
    public TableFilterHeader(JTable table, IParserModel parserModel) {
    	this(table, parserModel, null);
    }

    /** Full constructor. */
    public TableFilterHeader(JTable table, IParserModel parserModel,
    		AutoChoices mode) {
        super(new BorderLayout());
        add(new JPanel(), BorderLayout.CENTER); // do not take all width
        if (mode == null) {
        	mode = FilterSettings.autoChoices;
        }
        if (parserModel == null) {
            parserModel = FilterSettings.newParserModel();
        }
        filtersHandler = new FiltersHandler(mode, parserModel);
        backgroundSet = foregroundSet = fontSet = false;
        setPosition(FilterSettings.headerPosition);
        setTable(table);
    }
    
    /** Returns the filter editor for the given column in the table model. */
    public IFilterEditor getFilterEditor(int modelColumn) {
        return (columnsController == null)
            ? null
            : columnsController.getFilterEditor(getTable()
                    .convertColumnIndexToView(modelColumn));
    }
    
    /** Required to track model changes on the table */
    @Override public void propertyChange(PropertyChangeEvent evt) {
    	if ("model".equals(evt.getPropertyName())){
    		updateLook();
    		recreateController();
    	} else if ("componentOrientation".equals(evt.getPropertyName())){
            recreateController();
        }
    }

    /**
     * <p>Attaches the table where the filtering will be applied.</p>
     *
     * <p>It will be created a row of editors, that follow the size and position
     * of each of the columns in the table.</p>
     * 
     * <p>Setting the parameter to null effectively de-associates the 
     * TableFilterHeader from any previously associated table -which, unless
     * the {@link Position} is set to NONE, also implies removing the filter
     * header from the GUI-.</p>
     */
    public void setTable(JTable table) {
        filtersHandler.enableNotifications(false);

        JTable oldTable = getTable();
        positionHelper.changeTable(oldTable, table);
        if (oldTable != null) {
            oldTable.removeComponentListener(resizer);
            oldTable.removePropertyChangeListener("model", this);
            oldTable.removePropertyChangeListener("componentOrientation", this);
        }

        filtersHandler.setTable(table);
        if (table == null) {
            removeController();
            revalidate();
        } else {
            updateLook();
            recreateController();
            table.addComponentListener(resizer);
            table.addPropertyChangeListener("model", this);
            table.addPropertyChangeListener("componentOrientation", this);
        }

        filtersHandler.enableNotifications(true);
    }

    /** Returns the table currently attached. */
    public JTable getTable() {
        return (filtersHandler == null) ? null : filtersHandler.getTable();
    }

    /**
     * Sets the {@link IParserModel}, used to define the parsing of text on the
     * filter editors.
     */
    public void setParserModel(IParserModel parserModel) {
        filtersHandler.setParserModel(parserModel);
    }

    /**
     * Retrieves the current {@link IParserModel}; The returned reference is
     * required to update properties like {@link Format} or {@link Comparator}
     * instances associated to each class, or whether to ignore case.
     */
    public IParserModel getParserModel() {
        return filtersHandler.getParserModel();
    }

    /**
     * Sets the auto choices flag. When set, all editors are automatically
     * populated with choices extracted from the table's content -and updated as
     * the table is updated-.
     */
    public void setAutoChoices(AutoChoices set) {
        filtersHandler.setAutoChoices(set);
    }

    /** Returns the auto choices flag. */
    public AutoChoices getAutoChoices() {
        return filtersHandler.getAutoChoices();
    }

    /** Sets the adaptive choices mode. */
    public void setAdaptiveChoices(boolean enable) {
        filtersHandler.setAdaptiveChoices(enable);
    }

    /** Returns the adaptive choices mode. */
    public boolean isAdaptiveChoices() {
        return filtersHandler.isAdaptiveChoices();
    }

    /**
     * Enables instant filtering, as the user edits the filter's text<br>
     * The exact way the instant filtering works depends on the associated.
     *
     * @see  IParser#parseInstantText(String)
     */
    public void setInstantFiltering(boolean enable) {
        if (this.instantFilteringEnabled != enable) {
            this.instantFilteringEnabled = enable;
            if (columnsController != null) {
                for (FilterEditor fe : columnsController) {
                    fe.setInstantFiltering(enable);
                }
            }
        }
    }

    /** Returns true if instant filtering is enabled. */
    public boolean isInstantFiltering() {
        return this.instantFilteringEnabled;
    }

    /**
     * Enables vanishing during instant filtering<br>
     * If enabled, entering a filter expression that produces no rows
     * will hide; otherwise, the filter is just marked with warning color.
     */
    public void setAllowedInstantVanishing(boolean enable) {
        if (this.instantVanishingEnabled != enable) {
            this.instantVanishingEnabled = enable;
            if (columnsController != null) {
                for (FilterEditor fe : columnsController) {
                    fe.setAllowedInstantVanishing(enable);
                }
            }
        }
    }

    /** Returns true if vanishing is enabled during instant filtering */
    public boolean isAllowedInstantVanishing() {
        return this.instantVanishingEnabled;
    }

    /** Enables auto completion, as the user edits the filter's text. */
    public void setAutoCompletion(boolean enable) {
        if (this.autoCompletionEnabled != enable) {
            this.autoCompletionEnabled = enable;
            if (columnsController != null) {
                for (FilterEditor fe : columnsController) {
                    fe.setAutoCompletion(enable);
                }
            }
        }
    }

    /** Returns true if auto completion is enabled. */
    public boolean isAutoCompletion() {
        return this.autoCompletionEnabled;
    }

    /** Enables / Disables auto selection mode */
    public void setAutoSelection(boolean enable) {
        filtersHandler.setAutoSelection(enable);
    }

    /** Returns true if auto selection is enabled. */
    public boolean isAutoSelection() {
        return filtersHandler.isAutoSelection();
    }

    /** Enables / Disables automatic hiding of popups during table updates */
    public void setHidePopupsOnTableUpdates(boolean enable) {
        if (this.hidePopupsOnTableUpdates != enable) {
            this.hidePopupsOnTableUpdates = enable;
            if (columnsController != null) {
                for (FilterEditor fe : columnsController) {
                    fe.setHidePopupOnTableUpdates(enable);
                }
            }
        }
    }

    /** Returns true if popups automatically hide during table updates. */
    public boolean isHidePopupsOnTableUpdates() {
        return hidePopupsOnTableUpdates;
    }

    /**
     * Sets the filter on updates flag.<br>
     * It sets the sortOnUpdates flag on the underlying {@link DefaultRowSorter}
     * it is, in fact, just a helper to set this flag without accessing directly
     * the row sorter.
     *
     * @see  DefaultRowSorter#setSortsOnUpdates(boolean)
     */
    public void setFilterOnUpdates(boolean enable) {
        filtersHandler.setFilterOnUpdates(enable);
    }

    /** Returns true if the filter is reapplied on updates. */
    public boolean isFilterOnUpdates() {
        return filtersHandler.isFilterOnUpdates();
    }

    /** Hides / makes visible the header. */
    @Override public void setVisible(boolean flag) {
        if (isVisible() != flag) {
            positionHelper.headerVisibilityChanged(flag);
        }

        super.setVisible(flag);
        positionHelper.headerVisibilityChanged(flag);
    }

    /** Enables/Disables the filters. */
    @Override public void setEnabled(boolean enabled) {
        // it is not possible to call to super.setEnabled(enabled);
        // the filter header can embed the the header of the table, which
        // would then become also disabled.
        if (filtersHandler != null) {
            filtersHandler.setEnabled(enabled);
        }
    }

    /** Returns the current enable status. */
    @Override public boolean isEnabled() {
        return (filtersHandler == null) || filtersHandler.isEnabled();
    }

    /** Sets the position of the header related to the table. */
    public void setPosition(Position location) {
        positionHelper.setPosition(location);
    }

    /** Returns the mode currently associated to the TableHeader. */
    public Position getPosition() {
        return positionHelper.getPosition();
    }

    /**
     * Sets the maximum number of visible rows in the popup menu (a minimum is
     * always enforced).
     */
    public void setMaxVisibleRows(int maxVisibleRows) {
        int tmp = Math.max(MIN_VISIBLE_CHOICES, maxVisibleRows);
        if (tmp != look.maxVisiblePopupRows) {
            look.maxVisiblePopupRows = tmp;
            lookUpdated();
        }
    }

    /** Returns the maximum number of visible rows in the popup menu. */
    public int getMaxVisibleRows() {
        return look.maxVisiblePopupRows;
    }

    /**
     * Sets the maximum history size, always lower than the max number of
     * visible rows.
     */
    public void setMaxHistory(int maxHistory) {
        this.maxHistory = maxHistory;
        if (columnsController != null) {
            for (FilterEditor fe : columnsController) {
                fe.setMaxHistory(maxHistory);
            }
        }
    }

    /** Returns the maximum history size. */
    public int getMaxHistory() {
        return maxHistory;
    }

    /** Adds a filter -user specified- to the filter header. */
    public void addFilter(IFilter... filter) {
        filtersHandler.addFilter(filter);
    }

    /** Adds a filter -user specified- to the filter header. */
    public void removeFilter(IFilter... filter) {
        filtersHandler.removeFilter(filter);
    }

    /** Adds a new observer to the header. */
    public void addHeaderObserver(IFilterHeaderObserver observer) {
        observers.add(observer);
    }

    /** Removes an existing observer from the header. */
    public void removeHeaderObserver(IFilterHeaderObserver observer) {
        observers.remove(observer);
    }

    /**
     * <p>Invokes resetFilter on all the editor filters.</p>
     *
     * @see  IFilterEditor#resetFilter()
     */
    public void resetFilter() {

        if (columnsController != null) {
            filtersHandler.enableNotifications(false);
            for (FilterEditor fe : columnsController) {
                fe.resetFilter();
            }

            filtersHandler.enableNotifications(true);
        }
    }

    /** Sets the background color used by the parsed-based editors. */
    @Override public void setBackground(Color bg) {
        super.setBackground(bg);

        if (look != null) {
            backgroundSet = true;
            look.background = bg;
            lookUpdated();
        }
    }

    /** Sets the foreground color used by the editors. */
    @Override public void setForeground(Color fg) {
        super.setForeground(fg);

        if (look != null) {
            foregroundSet = true;
            look.foreground = fg;
            lookUpdated();
        }
    }

    /** Sets the color used for disabled fields. */
    public void setDisabledForeground(Color dfg) {
        this.disabledColorSet = true;
        look.disabledForeground = dfg;
        lookUpdated();
    }

    /** Returns the color used for disabled editors. */
    public Color getDisabledForeground() {
        return look.disabledForeground;
    }

    /** Sets the background color used for disabled editors. */
    public void setDisabledBackground(Color dbg) {
        this.disabledBgColorSet = true;
        look.disabledBackground = dbg;
        lookUpdated();
    }

    /** Returns the background color used for disabled editors. */
    public Color getDisabledBackground() {
        return look.disabledBackground;
    }

    /** Sets the foreground color used to represent selected state. */
    public void setSelectionForeground(Color fg) {
        this.selectionForegroundSet = true;
        look.selectionForeground = fg;
        lookUpdated();
    }

    /** Returns the foreground color on focused editors. */
    public Color getSelectionForeground() {
        return look.selectionForeground;
    }

    /** Sets the background color used to represent selected state. */
    public void setSelectionBackground(Color bg) {
        this.selectionBackgroundSet = true;
        look.selectionBackground = bg;
        lookUpdated();
    }

    /** Returns the background color on focused editors. */
    public Color getSelectionBackground() {
        return look.selectionBackground;
    }

    /** Sets the color set by default as text selection on filters. */
    public void setTextSelectionColor(Color c) {
        this.selectionColorSet = true;
        look.textSelection = c;
        lookUpdated();
    }

    /** Returns the color set by default as text selection on filters. */
    public Color getTextSelectionColor() {
        return look.textSelection;
    }

    /**
     * Sets the foreground color used by the parsing text editors when there are
     * errors on the filter expressions.
     */
    public void setErrorForeground(Color fg) {
        this.errorColorSet = true;
        look.errorForeground = fg;
        lookUpdated();
    }

    /**
     * Returns the color set by default as foreground on each text editor when
     * the user commits any error on the filter expression.
     */
    public Color getErrorForeground() {
        return look.errorForeground;
    }

    /**
     * Sets the foreground color used by the parsing text editors when the
     * associated filter would produce no visible rows.
     */
    public void setWarningForeground(Color fg) {
        this.warningColorSet = true;
        look.warningForeground = fg;
        lookUpdated();
    }

    /**
     * <p>Returns the color set by default as foreground on each text editor
     * when the filter would produce no visible rows</p>
     */
    public Color getWarningForeground() {
        return look.warningForeground;
    }

    /** Sets the color used to draw the header's grid. */
    public void setGridColor(Color c) {
        this.gridColorSet = true;
        look.gridColor = c;
        lookUpdated();
    }

    /** Returns the color set by default for the header's grid. */
    public Color getGridColor() {
        return look.gridColor;
    }

    /** Sets the font used on all the editors. */
    @Override public void setFont(Font font) {
        super.setFont(font);
        if (look != null) {
            fontSet = true;
            look.font = font;
            lookUpdated();
            revalidate();
        }
    }

    /** Setting to add / decrease height to the filter row. */
    public void setRowHeightDelta(int filterRowHeightDelta) {
        this.filterRowHeightDelta = filterRowHeightDelta;
        if (columnsController != null) {
            columnsController.updateHeight();
        }
    }

    /** Returns the filter row's height delta. */
    public int getRowHeightDelta() {
        return filterRowHeightDelta;
    }

    /** Sets the {@link CustomChoiceDecorator} instance. */
    public void setCustomChoiceDecorator(CustomChoiceDecorator decorator) {
        look.customChoiceDecorator = decorator;
        lookUpdated();
    }

    /** Returns the {@link CustomChoiceDecorator} instance. */
    public CustomChoiceDecorator getCustomChoiceDecorator() {
        return look.customChoiceDecorator;
    }

    /** Method automatically invoked when the class ancestor changes. */
    @Override public void addNotify() {
        super.addNotify();
        positionHelper.filterHeaderContainmentUpdate();
    }

    @Override public void updateUI() {
        // updateUI calls to setBackground and setForeground, possibly,
        // and that should not affect directly the look (is updated afterwards)
        Look look = this.look;
        this.look = null;
        super.updateUI();
        this.look = look;
        if (columnsController != null) {
            SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run() {
                        updateLook();
                    }
                });
        }
    }

    /** Updates the whole appearance: colors and font */
    void updateLook() {
        updateBackground();
        updateForeground();
        updateSelectionBackground();
        updateSelectionForeground();
        updateSelectionColor();
        updateDisabledForeground();
        updateDisabledBackground();
        updateErrorForeground();
        updateWarningForeground();
        updateGridColor();
        updateFont();
        lookUpdated();
    }

    private void lookUpdated() {
        if (columnsController != null) {
            columnsController.setLook(look);
        }
    }

    /** Updates the font on all components. */
    private void updateFont() {
        Font f;
        if (fontSet) {
            f = getFont();
        } else {
            f = FilterSettings.font;
            if (f == null) {
                f = getTable().getTableHeader().getFont();
                f = f.deriveFont(f.getSize() * .9f);
            }
        }

        look.font = f;
        super.setFont(f);
    }

    /** Updates the background on all components. */
    private void updateBackground() {
        Color c;
        if (backgroundSet) {
            c = getBackground();
        } else {
            c = FilterSettings.backgroundColor;
            if (c == null) {
                JTable table = getTable();
                Color background = table.getBackground();
                Color header = table.getTableHeader().getBackground();
                c = new Color((header.getRed() + background.getRed()) / 2,
                        (header.getGreen() + background.getGreen()) / 2,
                        (header.getBlue() + background.getBlue()) / 2);
            }
        }

        look.background = c;
        super.setBackground(c);
    }

    /** Updates the foreground on all components. */
    private void updateForeground() {
        Color c;
        if (foregroundSet) {
            c = getForeground();
        } else {
            c = FilterSettings.foregroundColor;
            if (c == null) {
                c = getTable().getForeground();
            }
        }

        look.foreground = c;
        super.setForeground(c);
    }

    /** Updates the selection background on all components. */
    private void updateSelectionBackground() {
        if (!selectionBackgroundSet) {
            look.selectionBackground = FilterSettings.selectionBackgroundColor;
            if ((look.selectionBackground == null) && (getTable() != null)) {
                look.selectionBackground = getTable().getSelectionBackground();
            }
        }
    }

    /** Updates the selection foreground on all components. */
    private void updateSelectionForeground() {
        if (!selectionForegroundSet) {
            look.selectionForeground = FilterSettings.selectionForegroundColor;
            if ((look.selectionForeground == null) && (getTable() != null)) {
                look.selectionForeground = getTable().getSelectionForeground();
            }
        }
    }

    /** Updates the selection color on all components. */
    private void updateSelectionColor() {
        if (!selectionColorSet) {
            look.textSelection = FilterSettings.selectionColor;
            if (look.textSelection == null) {
                Color a = getBackground();
                Color b = getSelectionBackground();
                if ((a != null) && (b != null)) {
                    look.textSelection = new Color((a.getRed() + b.getRed())
                                / 2, (a.getGreen() + b.getGreen()) / 2,
                            (a.getBlue() + b.getBlue()) / 2);
                }
            }
        }
    }

    /** Updates the disabled foreground on all components. */
    private void updateDisabledForeground() {
        if (!disabledColorSet) {
            look.disabledForeground = FilterSettings.disabledColor;
            if ((look.disabledForeground == null) && (getTable() != null)) {
                look.disabledForeground = getTable().getGridColor();
                if (look.disabledForeground.equals(getBackground())) {
                    look.disabledForeground = Color.lightGray;
                }
            }
        }
    }

    /** Updates the disabled foreground on all components. */
    private void updateDisabledBackground() {
        if (!disabledBgColorSet) {
            look.disabledBackground = FilterSettings.disabledBackgroundColor;
            if (look.disabledBackground == null) {
                look.disabledBackground = look.getBackground();
            }
        }
    }

    /** Updates the grid color on all components. */
    private void updateGridColor() {
        if (!gridColorSet) {
            look.gridColor = FilterSettings.gridColor;
            if ((look.gridColor == null) && (getTable() != null)) {
                look.gridColor = getTable().getGridColor();
            }
        }
    }

    /** Updates the error foreground on all components. */
    private void updateErrorForeground() {
        if (!errorColorSet) {
            look.errorForeground = FilterSettings.errorColor;
            if (look.errorForeground == null) {
                look.errorForeground = Color.red;
            }
        }
    }

    /** Updates the warning foreground on all components. */
    private void updateWarningForeground() {
        if (!warningColorSet) {
            look.warningForeground = FilterSettings.warningColor;
            if (look.warningForeground == null) {
                look.warningForeground = new Color(196, 0, 0);
            }
        }
    }

    /**
     * removes the current columnsController.
     *
     * @return  true if there was a controller
     */
    private boolean removeController() {

        if (columnsController != null) {
            columnsController.detach();
            remove(columnsController);
            columnsController = null;

            return true;
        }

        return false;
    }

    /** creates/recreates the current columnsController. */
    void recreateController() {
        filtersHandler.enableNotifications(false);
        removeController();
        columnsController = new FilterColumnsControllerPanel(getFont(),
                getForeground(), getBackground());
        add(columnsController, BorderLayout.WEST);
        revalidate();
        filtersHandler.updateModel();
        filtersHandler.enableNotifications(true);
    }

    /**
     * Class setting up together all the column filters<br>
     * Note that, while the TableFilterHeader handles columns using their model
     * numbering, the FilterColumnsControllerPanel manages the columns as they
     * are sorted in the Table. That is, if the user changes the order of two or
     * more columns, this class reacts by reordering internal data structures
     */
    private class FilterColumnsControllerPanel extends JPanel
        implements TableColumnModelListener, Runnable, Iterable<FilterEditor> {

        private static final long serialVersionUID = -5183169239497633085L;

        /** The list of columns, sorted in the view way. */
        private LinkedList<FilterColumnPanel> columns;

        /** Preferred size of the component. */
        private Dimension preferredSize;

        /**
         * The panel must keep a reference to the TableColumnModel, to be able
         * to 'unregister' when the controller is destroyed.
         */
        private TableColumnModel tableColumnModel;

        /**
         * Variable keeping track of the number of times that the run() method
         * is going to be invoked from the gui thread.
         */
        private int autoRun;

        /**
         * When a new model is set, all columns are first removed, and the new
         * ones then created. While columns are removed, the state of the filter
         * (filtersHandler) can changed between enabled and not enabled, but it
         * is needed to remember the state at the start of the cycle, to create
         * the new editors with the expected enable state.
         */
        private Boolean handlerEnabled;
        
        /**
         * The model associated to the table when the controller is created.
         */
        private TableModel tableModel;

        /**
         * Creates the controller for all the columns<br>
         * It will automatically create an editor of the current EditorMode for
         * each column.
         */
        public FilterColumnsControllerPanel(Font  font,
                                            Color foreground,
                                            Color background) {
            super(null);
            super.setFont(font);
            super.setForeground(foreground);
            super.setBackground(background);
            this.tableColumnModel = getTable().getColumnModel();
            this.tableModel = getTable().getModel();

            boolean enabled = filtersHandler.isEnabled();
            int count = tableColumnModel.getColumnCount();
            columns = new LinkedList<FilterColumnPanel>();
            for (int i = 0; i < count; i++) {
                createColumn(i, enabled);
            }
            preferredSize = new Dimension(0,
                    (count == 0) ? 0
                                 : (columns.get(0).h + filterRowHeightDelta));
            placeComponents();
            tableColumnModel.addColumnModelListener(this);
        }

        /** {@link Iterable} interface. */
        @Override public Iterator<FilterEditor> iterator() {
            final Iterator<FilterColumnPanel> it = columns.iterator();

            return new Iterator<FilterEditor>() {

                @Override public void remove() {
                    // not supported
                }

                @Override public FilterEditor next() {
                    return it.next().editor;
                }

                @Override public boolean hasNext() {
                    return it.hasNext();
                }
            };
        }

        /** Creates the FilterColumnPanel for the given column number. */
        private void createColumn(int columnView, boolean enableIt) {
            int columnModel = getTable().convertColumnIndexToModel(columnView);
            FilterEditor editor = createEditor(columnModel, enableIt);
            FilterColumnPanel column = new FilterColumnPanel(
                    tableColumnModel.getColumn(columnView), editor);
            column.updateHeight();
            columns.add(column);
            add(column);
        }

        /** Creates an editor for the given column. */
        private FilterEditor createEditor(int modelColumn, boolean enableIt) {
            FilterEditor ret = new FilterEditor(filtersHandler, modelColumn,
                    getTable().getModel().getColumnClass(modelColumn), look);
            ret.setMaxHistory(maxHistory);
            ret.setInstantFiltering(instantFilteringEnabled);
            ret.setAutoCompletion(autoCompletionEnabled);
            ret.setHidePopupOnTableUpdates(hidePopupsOnTableUpdates);
            ret.getFilter().setEnabled(enableIt);

            ///////////////////leon code begin
            ret.setChoicesEnable(filtersHandler.isChoicesEnable());
            //////////////////leon code end

            filtersHandler.addFilterEditor(ret);

            return ret;
        }

        /** Detaches the current instance from any registered listeners. */
        public void detach() {

            for (FilterColumnPanel column : columns) {
                column.detach();
            }

            tableColumnModel.removeColumnModelListener(this);
        }

        /**
         * Returns the editor for the given column, or null if such column does
         * not exist.
         */
        public FilterEditor getFilterEditor(int viewColumn) {
            return (columns.size() > viewColumn)
                ? columns.get(viewColumn).editor : null;
        }

        /** Computes the proper preferred height -width is not important-. */
        public void updateHeight() {
            int h = 0;

            for (FilterColumnPanel c : columns) {
                h = Math.max(h, c.h);
            }

            preferredSize.height = h + filterRowHeightDelta;
            placeComponents();
            repaint();
        }

        public void setLook(Look look) {
            boolean fontChange = getFont() != look.font;
            setBackground(look.background);
            setForeground(look.foreground);
            if (columns != null) {
                for (FilterColumnPanel panel : this.columns) {
                    panel.setLook(look);
                }
            }

            if (fontChange) {
                setFont(look.font);
                updateHeight();
            }
        }

        /** {@link TableColumnModelListener} interface. */
        @Override public void columnMarginChanged(ChangeEvent e) {
            placeComponents();
        }

        /** {@link TableColumnModelListener} interface. */
        @Override public void columnMoved(TableColumnModelEvent e) {
            if (e.getFromIndex() != e.getToIndex()) {
                FilterColumnPanel fcp = columns.remove(e.getFromIndex());
                columns.add(e.getToIndex(), fcp);
                placeComponents();
            }
            // previous block places each filter column in the right position
            // BUT does not take in consideration the dragging distance
            JTableHeader header = getTable().getTableHeader();
            TableColumn tc = header.getDraggedColumn();
            if (tc != null) {
                boolean rightToLeft = getTable().getComponentOrientation() ==
                        ComponentOrientation.RIGHT_TO_LEFT;
                // Iterate the filter columns, we need to know the previous
                // and the current column
                Iterator<FilterColumnPanel> it = rightToLeft?
                        columns.descendingIterator() :
                        columns.iterator();
                FilterColumnPanel previous = null;
                while (it.hasNext()) {
                    FilterColumnPanel fcp = it.next();
                    if (fcp.tc == tc){
                        Rectangle r = null;
                        double x = 0;
                        if (previous != null) {
                            r = previous.getBounds();
                            // obtain on X the position that the current
                            // dragged column should be IF there would be no dragging
                            // (previous panel plus its width)
                            x = r.getX() + r.getWidth();
                        }
                        // shift now the column to the correct distance
                        r = fcp.getBounds(r);
                        r.translate((int)(x - r.getX() + header.getDraggedDistance()), 0);
                        fcp.setBounds(r);

                        // one detail is left: the Z order of this column should be lower
                        // that the Z order of the column being dragged over
                        if (rightToLeft) {
                            // in this case, previous is the next column, not the one before!
                            previous = it.hasNext()? it.next() : null;
                        }
                        if (previous != null) {
                            int prevZOrder = getComponentZOrder(previous);
                            int zOrder = getComponentZOrder(fcp);
                            boolean overPreviousDragging =  rightToLeft?
                                    header.getDraggedDistance() > 0 :
                                    header.getDraggedDistance() < 0;
                            if (overPreviousDragging != (zOrder < prevZOrder)) {
                                setComponentZOrder(previous, zOrder);
                                setComponentZOrder(fcp, prevZOrder);
                            }
                        }
                        break;
                    }
                    previous = fcp;
                }
            }
        }

        /** {@link TableColumnModelListener} interface. */
        @Override public void columnAdded(TableColumnModelEvent e) {

        	//Support the case where a model is being changed
        	if (isCorrectModel()) {
	
	            // when adding or removing columns to the table model, or, in
	            // general, when fireTableStructureChanged() is invoked on a
	            // table model, all columns are removed, and the definitive 
        		// ones added.
	            // To avoid sending update notifications to the table, which
	            // may be quite time/CPU consuming, it is better to disable
	            // the notifications and only send them after all columns
	            // have been added or removed.
	            // As there is no way to know when the last column is added
	            // (or removed), the implementation disables the 
        		// notifications and request to be auto called eventually. 
        		// This call (run()) will happen when all the column 
        		// modifications have concluded, so then it is safe to 
        		// reactivate the notifications
	            filtersHandler.enableNotifications(false);
	            if (handlerEnabled == null) {
	                handlerEnabled = filtersHandler.isEnabled();
	            }
	            createColumn(e.getToIndex(), handlerEnabled);
	            update();
        	}
        }

        /** {@link TableColumnModelListener} interface. */
        @Override public void columnRemoved(TableColumnModelEvent e) {
        	
        	//Support the case where a model is being changed
        	if (isCorrectModel()) {
	            // see the comment on columnAdded
	            filtersHandler.enableNotifications(false);
	            if (handlerEnabled == null) {
	                handlerEnabled = filtersHandler.isEnabled();
	            }
                FilterColumnPanel fcp = columns.remove(e.getFromIndex());
	            fcp.detach();
	            remove(fcp);
	            update();
        	}
        }

        /** {@link TableColumnModelListener} interface. */
        @Override public void columnSelectionChanged(ListSelectionEvent e) {
            // nothing needed here
        }
        
        private boolean isCorrectModel() {
        	JTable table = getTable();
        	return table != null && tableModel == table.getModel();
        }

        /**
         * Updates the columns. If this is the GUI thread, better wait until all
         * the events have been handled. Otherwise, do it immediately, as it is
         * not known how the normal/Gui thread can interact
         */
        private void update() {
            autoRun += 1;
            if (SwingUtilities.isEventDispatchThread()) {
                SwingUtilities.invokeLater(this);
            } else {
                run();
            }
        }

        @Override public void run() {
            // see the comment on columnAdded
            if ((--autoRun == 0) && (getTable() != null)) {
                handlerEnabled = null;
                updateHeight();
            }

            filtersHandler.enableNotifications(true);
        }

        /**
         * Places all the components in line, respecting their preferred widths.
         */
        void placeComponents() {
            int x = 0;
            Iterator<FilterColumnPanel> it =
                    getTable().getComponentOrientation() ==
                            ComponentOrientation.RIGHT_TO_LEFT?
                    columns.descendingIterator() :
                    columns.iterator();

            while (it.hasNext()) {
                FilterColumnPanel fcp = it.next();
                fcp.setBounds(x, 0, fcp.w, preferredSize.height);
                x += fcp.w;
            }
            revalidate();
        }

        @Override public Dimension getPreferredSize() {
            JTable table = getTable();
            if (table != null) {
                preferredSize.width = table.getWidth();
            }

            return preferredSize;
        }

        /**
         * Class controlling the filter applied to one specific column<br>
         * It resizes itself automatically as the associated table column is
         * resized.
         */
        private class FilterColumnPanel extends JPanel
            implements PropertyChangeListener, IFilterObserver {

            private static final long serialVersionUID = 6858728575542289815L;

            /** The associated editor. */
            FilterEditor editor;

            /** Dimensions of the component. */
            int w;
            int h;

            /**
             * The TableColumn object, to which is registered to get property
             * changes, in order to keep the same width.
             */
            TableColumn tc;

            /** Constructor. */
            public FilterColumnPanel(TableColumn tc, FilterEditor editor) {
                super(new BorderLayout());
                this.tc = tc;
                w = tc.getWidth();
                add(editor, BorderLayout.CENTER);
                h = getPreferredSize().height;
                editor.getFilter().addFilterObserver(this);
                for (IFilterHeaderObserver observer : observers) {
                    observer.tableFilterEditorCreated(TableFilterHeader.this,
                        editor, tc);
                }

                this.editor = editor;
                tc.addPropertyChangeListener(this);
            }

            /**
             * Performs any cleaning required before removing this component.
             */
            public void detach() {

                if (editor != null) {
                    filtersHandler.removeFilterEditor(editor);
                    remove(editor);
                    editor.getFilter().removeFilterObserver(this);
                    for (IFilterHeaderObserver observer : observers) {
                        observer.tableFilterEditorExcluded(
                            TableFilterHeader.this, editor, tc);
                    }
                }

                tc.removePropertyChangeListener(this);
            }

            public void setLook(Look look) {
                editor.setLook(look);

                Font oldFont = getFont();
                if (oldFont != look.font) {
                    setFont(look.font);
                    updateHeight();
                }
            }

            public void updateHeight() {
                h = getPreferredSize().height;
                revalidate();
            }

            @Override public void filterUpdated(IFilter obs) {
                if (editor != null) { // avoid sending the first update
                    for (IFilterHeaderObserver observer : observers) {
                        observer.tableFilterUpdated(TableFilterHeader.this,
                            editor, tc);
                    }
                }
            }

            /** Listening for changes on the width of the table' column. */
            @Override public void propertyChange(PropertyChangeEvent evt) {
                // just listen for any property
                int newW = tc.getWidth();

                if (w != newW) {
                    w = newW;
                    placeComponents();
                }
            }
        }
    }

    //////////////////////////////////////////////// leon code begins

    public void setChoicesEnable(boolean enable) {
        filtersHandler.setChoicesEnable(enable);
    }

    public boolean isChoicesEnable() {
        return filtersHandler.isChoicesEnable();
    }

    ///////////////////////////////////////////////// leon code ends
}
