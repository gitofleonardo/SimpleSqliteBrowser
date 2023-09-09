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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;

import net.coderazzi.filters.gui.Look;


/** Custom implementation of the arrow used to display the popup menu. */
final class FilterArrowButton extends JButton {
    private static final long serialVersionUID = -777416843479142582L;
    private final static int MIN_X = 6;
    private final static int MIN_Y = 6;
    private final static int FILL_X[] = { 0, 3, 6 };
    private final static int FILL_Y[] = { 0, 5, 0 };

    private boolean focus;
    private Look look;

    public void setLook(Look look) {
        this.look = look;
        repaint();
    }

    public Look getLook() {
        return look;
    }

    public void setFocused(boolean focus) {
        this.focus = focus;
        repaint();
    }


    @Override public void paint(Graphics g) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        int height = getHeight();
        int width = getWidth();

        if (isEnabled()) {
            g.setColor(focus ? look.getSelectionBackground()
                             : look.getBackground());
        } else {
            g.setColor(look.getDisabledBackground());
        }

        g.fillRect(0, 0, width, height);

        width = (width - MIN_X) / 2;
        height = Math.min(height / 2, height - MIN_Y);
        g.translate(width, height);

        if (isEnabled()) {
            g.setColor(focus ? look.getSelectionForeground()
                             : look.getForeground());
        } else {
            g.setColor(look.getDisabledForeground());
        }

        g.fillPolygon(FILL_X, FILL_Y, FILL_X.length);
    }

    @Override protected void paintBorder(Graphics g) {
        super.paintBorder(g);
    }

    @Override public boolean isFocusable() {
        return false;
    }

    @Override public Dimension getPreferredSize() {
        return new Dimension(12, 12);
    }
}
