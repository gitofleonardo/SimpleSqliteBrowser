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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JComponent;


/**
 * Interface that allows customizing the appearance of CustomChoices in those
 * {@link IFilterEditor}s without associated {@link ChoiceRenderer}.
 */
public interface CustomChoiceDecorator {

    /** Returns the background color. */
    Color getBackground(CustomChoice  choice,
                        IFilterEditor editor,
                        boolean       isSelected);

    /** Returns the foreground color. */
    Color getForeground(CustomChoice  choice,
                        IFilterEditor editor,
                        boolean       isSelected);

    /** Returns the font. */
    Font getFont(CustomChoice choice, IFilterEditor editor, boolean isSelected);

    /** Decorates the choice on the given editor. */
    void decorateComponent(CustomChoice  choice,
                           IFilterEditor editor,
                           boolean       isSelected,
                           JComponent    c,
                           Graphics      g);

    /**
     * Default decorator, delegating always to the associated methods on the
     * {@link CustomChoice} instances. The font, by default, will be cursive
     */
    public class DefaultDecorator implements CustomChoiceDecorator {

        private Font baseFont;
        private Font italicFont;

        @Override public void decorateComponent(CustomChoice  choice,
                                                IFilterEditor editor,
                                                boolean       isSelected,
                                                JComponent    c,
                                                Graphics      g) {
            choice.decorateComponent(editor, isSelected, c, g);
        }

        @Override public Font getFont(CustomChoice  choice,
                                      IFilterEditor editor,
                                      boolean       isSelected) {
            Font ret = choice.getFont(editor, isSelected);
            if (ret == null) {
                ret = editor.getLook().getFont();
                if (ret != baseFont) {
                    baseFont = ret;
                    italicFont = baseFont.deriveFont(Font.ITALIC);
                }

                ret = italicFont;
            }

            return ret;
        }

        @Override public Color getBackground(CustomChoice  choice,
                                             IFilterEditor editor,
                                             boolean       isSelected) {
            Color color = choice.getBackground(editor, isSelected);
            if (color == null) {
                Look look = editor.getLook();
                color = isSelected ? look.getSelectionBackground() 
                		           : look.getBackground();
            }
            return color;
        }

        @Override public Color getForeground(CustomChoice  choice,
                                             IFilterEditor editor,
                                             boolean       isSelected) {
            Color color = choice.getForeground(editor, isSelected);
            if (color == null) {
                Look look = editor.getLook();
                color = isSelected ? look.getSelectionForeground() 
                		           : look.getForeground();
            }
            return color;
        }
    }

}
