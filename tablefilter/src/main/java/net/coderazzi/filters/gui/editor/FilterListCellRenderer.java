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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import net.coderazzi.filters.gui.ChoiceRenderer;
import net.coderazzi.filters.gui.CustomChoice;
import net.coderazzi.filters.gui.CustomChoiceDecorator;
import net.coderazzi.filters.gui.IFilterEditor;
import net.coderazzi.filters.gui.Look;


/**
 * Special cellRenderer used on the history and choices list, and to render the
 * content when is not text-based (the user has specified a {@link
 * ListCellRenderer}.<br>
 *
 * <p>This FilterListCellRenderer encapsulated the user's Renderer, to have some
 * specific appearance: it differentiates between selected cells, selected
 * focused cells, and other cells; a cell that is selected is considered focused
 * if the current focus lies on the Popup menu (and not on the editor itself):
 * </p>
 *
 * <ul>
 *   <li>A selected and focused cells is displayed as usual (selected
 *     background).</li>
 *   <li>A selected, not focused cell, only gets an arrow, shown on its left
 *     side.</li>
 * </ul>
 *
 * <p>To avoid inconsistencies, the space required for the arrow is left blank
 * on the unselected cells<br>
 * </p>
 */
class FilterListCellRenderer extends JComponent implements ListCellRenderer {

    private static final long serialVersionUID = 6736940091246039334L;
    private final static int X_MARGIN_ARROW = 1;
    private final static int WIDTH_ARROW = 5;
    private final static int HEIGHT_ARROW = 6; // must be even
    private final static int X[] = { 0, WIDTH_ARROW, 0 };
    private final static int Y[] = { 0, HEIGHT_ARROW / 2, HEIGHT_ARROW };

    private CellRendererPane painter = new CellRendererPane();
    private JList referenceList;
    private Component inner;
    private Color arrowColor;

    private boolean showArrow;
    private boolean focusOnList;
    private int xDeltaBase;
    private int width;

    IFilterEditor editor;
    Color fg;
    Color bg;
    ChoiceRenderer renderer;

    private class DefaultRenderer extends JLabel implements ListCellRenderer {

        private static final long serialVersionUID = 5837846455371777058L;

        CustomChoice currentCustomChoice;
        boolean isSelected;

        public DefaultRenderer(JList referenceList) {
            setOpaque(true);
            setComponentOrientation(referenceList.getComponentOrientation());
        }

        @Override public Component getListCellRendererComponent(
                JList   list,
                Object  value,
                int     index,
                boolean isSelected,
                boolean cellHasFocus) {
            this.isSelected = isSelected;
            if (value instanceof CustomChoice) {
                currentCustomChoice = (CustomChoice) value;
            } else {
                currentCustomChoice = null;
            }

            prepareComponentLook(this, isSelected, currentCustomChoice);
            setText((value == null) ? "" : value.toString());

            return this;
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (currentCustomChoice != null) {
                editor.getLook()
                    .getCustomChoiceDecorator()
                    .decorateComponent(currentCustomChoice, editor, isSelected,
                        this, g);
            }
        }
    }

    /**
     * Specific cellRenderer for the TableFilter, taking care of {@link
     * CustomChoice} components.
     */
    private ListCellRenderer defaultRenderer;

    public FilterListCellRenderer(IFilterEditor editor, JList mainList) {
        setUserRenderer(null);
        setDoubleBuffered(true);
        this.editor = editor;
        this.referenceList = mainList;
        this.defaultRenderer = new DefaultRenderer(mainList);
    }

    /**
     * Indicates that the focus is currently on the list.<br>
     * Selected cells are selected-focused cells
     */
    public void setFocusOnList(boolean set) {
        this.focusOnList = set;
    }

    public boolean isFocusOnList() {
        return focusOnList;
    }

    public void setUserRenderer(ChoiceRenderer cellRenderer) {
        renderer = cellRenderer;
    }

    public ChoiceRenderer getUserRenderer() {
        return renderer;
    }

    @Override public Component getListCellRendererComponent(
            JList   list,
            Object  value,
            int     index,
            boolean isSelected,
            boolean cellHasFocus) {
        setupRenderer(list, value, index, focusOnList && isSelected,
            cellHasFocus);
        width = referenceList.isShowing() ? referenceList.getWidth()
                                          : list.getWidth();
        showArrow = isSelected;
        arrowColor = list.getSelectionBackground();
        xDeltaBase = WIDTH_ARROW + (2 * X_MARGIN_ARROW);

        return this;
    }

    /** Method used to render the content on the rendered editor. */
    public Component getCellRendererComponent(Object  value,
                                              int     finalWidth,
                                              boolean focused) {
        setupRenderer(referenceList, value, -1, focused, false);
        width = finalWidth;
        showArrow = false;
        xDeltaBase = 0;

        return this;
    }

    /**
     * Prepares the component look.
     *
     * @param   c         the component to update
     * @param   selected
     * @param   cc        the {@link CustomChoice} instance held in the
     *                    component, if any
     *
     * @return  the applied look
     */
    public Look prepareComponentLook(Component    c,
                                     boolean      selected,
                                     CustomChoice cc) {
        Look look = editor.getLook();
        Color background;
        Color foreground;
        CustomChoiceDecorator decorator = (cc == null)
            ? null : look.getCustomChoiceDecorator();
        Font font = (decorator == null)
            ? look.getFont() : decorator.getFont(cc, editor, selected);
        if (!c.isEnabled()) {
            foreground = look.getDisabledForeground();
            background = look.getDisabledBackground();
        } else {
            if (decorator != null) {
                background = decorator.getBackground(cc, editor, selected);
                foreground = decorator.getForeground(cc, editor, selected);
            } else if (selected) {
                background = look.getSelectionBackground();
                foreground = look.getSelectionForeground();
            } else {
                background = look.getBackground();
                foreground = look.getForeground();
            }
        }

        if (foreground != c.getForeground()) {
            c.setForeground(foreground);
        }

        if (background != c.getBackground()) {
            c.setBackground(background);
        }

        if (font != c.getFont()) {
            c.setFont(font);
        }

        return look;
    }

    private void setupRenderer(JList   list,
                               Object  value,
                               int     index,
                               boolean selected,
                               boolean cellHasFocus) {
        inner = null;
        if (renderer != null) {
            try {
                inner = renderer.getRendererComponent(editor, value, selected);
            } catch (Exception ex) {
                // inner still null
            }
        }

        if (inner == null) {
            inner = defaultRenderer.getListCellRendererComponent(list, value,
                    index, selected, cellHasFocus);
        }
    }

    @Override protected void paintComponent(Graphics g) {
        int height = getHeight();
        int xDelta = xDeltaBase;
        int yDelta = 0;
        if (showArrow) {
            Object old = ((Graphics2D) g).getRenderingHint(
                    RenderingHints.KEY_ANTIALIASING);
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            yDelta = (height / 2) + 1 - (HEIGHT_ARROW / 2);
            xDelta -= X_MARGIN_ARROW;
            g.translate(X_MARGIN_ARROW, yDelta);
            g.setColor(arrowColor);
            g.fillPolygon(X, Y, X.length);
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                old);
        }

        g.translate(xDelta, -yDelta);
        {
            boolean resetEnabled = inner.isEnabled();
            inner.setEnabled(isEnabled());
            painter.paintComponent(g, inner, this, 0, 0, width - xDeltaBase,
                height);
            inner.setEnabled(resetEnabled);
        }
    }

    @Override public Dimension getPreferredSize() {
        return inner.getPreferredSize();
    }

    @Override public boolean isShowing() {
        return true;
    }

}
