package de.monoped.swing;

import javax.swing.*;
import java.text.ParseException;

public class DoubleFormatter
        extends JFormattedTextField.AbstractFormatter {
    public String valueToString(Object value) {
        return value == null ? "0.0" : value.toString();
    }

    //----------------------------------------------------------------------

    public Object stringToValue(String s)
            throws ParseException {
        try {
            return Double.valueOf(s);
        } catch (NumberFormatException e) {
            throw new ParseException("Double", 0);
        }

    }

}


