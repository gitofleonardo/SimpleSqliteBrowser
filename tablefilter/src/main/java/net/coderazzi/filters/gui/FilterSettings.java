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

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.coderazzi.filters.IParser;
import net.coderazzi.filters.gui.TableFilterHeader.Position;


/**
 * Class to define some common settings to the TableFilter library.<br>
 * It is just a sugar replacement to using directly system properties (which
 * could be not available, anyway)
 */
public class FilterSettings {

    /** Properties must be defined with this prefix. */
    public final static String PROPERTIES_PREFIX = "net.coderazzi.filters.";

    /** Whether to enable adaptive choices, true by default. */
    public static boolean adaptiveChoices = Boolean.parseBoolean(getString(
                "AdaptiveChoices", "true"));

    /** If and how to provide content to the editor field's choices. */
    public static AutoChoices autoChoices;

    /** Whether to enable auto completion, true by default. */
    public static boolean autoCompletion = Boolean.parseBoolean(getString(
                "AutoCompletion", "true"));

    /**
     * Set to true to perform automatically the selection of a row that is
     * uniquely identified by the existing filter. It is true by default.
     */
    public static boolean autoSelection = Boolean.parseBoolean(getString(
                "AutoSelection", "true"));

    /** Header's background color. */
    public static Color backgroundColor = getColor("BackgroundColor", null);

    /**
     * The class defining the generic {@link CustomChoiceDecorator}<br>
     * It must have a default constructor.<br>
     */
    public static Class<? extends CustomChoiceDecorator> customChoiceDecoratorClass;

    /** The default date format, used on the default filter model. */
    public static String dateFormat = getString("DateFormat", null);

    /** Header's disabled color. */
    public static Color disabledBackgroundColor = getColor(
            "DisabledBackgroundColor", null);

    /** Header's disabled color. */
    public static Color disabledColor = getColor("DisabledColor", null);

    /** Header's error color. */
    public static Color errorColor = getColor("ErrorColor", null);

    /** If true, table updates trigger filter and sort updates. */
    public static boolean filterOnUpdates = Boolean.parseBoolean(getString(
                "FilterOnUpdates", "true"));

    /**
     * Setting to add / decrease height to the filter row.<br>
     * This setting could be specifically required on specific Look And Feels
     * -Substance seems to require additional height.<br>
     */
    public static int filterRowHeightDelta = getInteger("FilterRowHeightDelta",
            0);

    /** Header's font. */
    public static Font font;

    /** Header's foreground color. */
    public static Color foregroundColor = getColor("ForegroundColor", null);

    /** Header's grid color. */
    public static Color gridColor = getColor("GridColor", null);

    /** The header position, {@link Position#INLINE} by default. */
    public static Position headerPosition = Position.valueOf(getString(
                "Header.Position", "INLINE"));

    /**
     * Set to true to automatically hide any filter popups during table updates.
     * It is false by default.
     */
    public static boolean hidePopupsOnTableUpdates = Boolean.parseBoolean(
            getString("HidePopupsOnTableUpdates", "false"));

    /** Whether to ignore case or not, false by default (case sensitive). */
    public static boolean ignoreCase = Boolean.parseBoolean(getString(
                "IgnoreCase", "true"));

    /** Whether to enable instant filtering, true by default. */
    public static boolean instantFiltering = Boolean.parseBoolean(getString(
                "InstantFiltering", "true"));

    /** Whether to allow vanishing during instant filtering, false by default. */
    public static boolean allowInstantVanishing = Boolean.parseBoolean(getString(
            "AllowInstantVanishing", "false"));

    /** The default icon used to represent null/empty values. */
    public static Icon matchEmptyFilterIcon = new ImageIcon(IParser.class
                .getResource("resources/matchEmptyIcon.png"));

    /**
     * The default string associated to a nop operation.
     *
     * <p>It is chosen as = because that is the expression that the default text
     * parser can use to find null/empty values. If any other parse is chosen,
     * it could be meaningful to update this string.</p>
     */
    public static String matchEmptyFilterString = "=";

    /** The maximum size of the history when no choices are present. */
    public static int maxPopupHistory = getInteger("Popup.MaxHistory", 2);

    /** The maximum number of visible tows on the popup menus. */
    public static int maxVisiblePopupRows = getInteger("Popup.MaxVisibleRows",
            8);

    /**
     * The class defining the generic {@link IParserModel}<br>
     * It must have a default constructor.<br>
     * It corresponds to the property ParserModel.class
     */
    public static Class<? extends IParserModel> parserModelClass;

    /** Header's selection background color. */
    public static Color selectionBackgroundColor = getColor(
            "SelectionBackgroundColor", null);

    /** Header's selection color. */
    public static Color selectionColor = getColor("SelectionColor", null);

    /** Header's selection foreground color. */
    public static Color selectionForegroundColor = getColor(
            "SelectionForegroundColor", null);

    /** Header's warning color. */
    public static Color warningColor = getColor("WarningColor", null);

    /** Creates a TextParser as defined by default. */
    public static IParserModel newParserModel() {
        try {
            return parserModelClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Error creating parser model of type "
                    + parserModelClass, ex);
        }
    }

    /** Creates a CustomChoiceDecorator as defined by default. */
    public static CustomChoiceDecorator newCustomChoiceDecorator() {
        try {
            return customChoiceDecoratorClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Error creating decorator of type "
                    + customChoiceDecoratorClass, ex);
        }
    }

    static {
        try {
            font = Font.decode(getString("Font"));
        } catch (Exception ex) {
            // font remains null
        }

        try {
            autoChoices = AutoChoices.valueOf(getString("AutoChoices",
                        "ENUMS"));
        } catch (Exception ex) {
            autoChoices = AutoChoices.ENUMS;
        }

        parserModelClass = ParserModel.class;

        String cl = getString("ParserModel.Class", null);
        if (cl != null) {
            try {
                parserModelClass = (Class<? extends IParserModel>) Class
                        .forName(cl);
            } catch (ClassNotFoundException cne) {
                throw new RuntimeException(
                    "Error finding filter model of class " + cl, cne);
            } catch (ClassCastException cce) {
                throw new RuntimeException("Filter model of class " + cl
                        + " is not a valid IParserModel class");
            }
        }

        customChoiceDecoratorClass =
            CustomChoiceDecorator.DefaultDecorator.class;
        cl = getString("CustomChoiceDecorator.Class", null);
        if (cl != null) {
            try {
                customChoiceDecoratorClass =
                    (Class<? extends CustomChoiceDecorator>) Class.forName(cl);
            } catch (ClassNotFoundException cne) {
                throw new RuntimeException(
                    "Error finding choice decorator of class " + cl, cne);
            } catch (ClassCastException cce) {
                throw new RuntimeException(
                    "CustomChoiceDecorator model of class " + cl
                        + " is not a valid CustomChoiceDecorator class");
            }
        }
    }

    private static String getString(String name, String defaultValue) {
        String ret = getString(name);

        return (ret == null) ? defaultValue : ret;
    }

    private static String getString(String name) {
        try {
            return System.getProperty(PROPERTIES_PREFIX + name);
        } catch (Exception ex) {
            return null;
        }
    }

    private static int getInteger(String name, int defaultValue) {
        String ret = getString(name);
        if (ret != null) {
            try {
                return Integer.valueOf(ret);
            } catch (Exception ex) {
                // return defaultValue
            }
        }

        return defaultValue;
    }

    private static Color getColor(String name, Color defaultValue) {
        String prop = getString(name);
        if (prop != null) {
            try {
                return Color.decode(prop);
            } catch (Exception ex) {
                // return defaultValue
            }
        }

        return defaultValue;
    }

}
