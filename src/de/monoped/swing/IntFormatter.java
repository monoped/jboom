package de.monoped.swing;

import javax.swing.*;
import java.text.ParseException;

public class IntFormatter
        extends JFormattedTextField.AbstractFormatter {
    public String valueToString(Object value) {
        return value == null ? "0" : value.toString();
    }

    //----------------------------------------------------------------------

    public Object stringToValue(String s)
            throws ParseException {
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            throw new ParseException("Int", 0);
        }

    }

}

