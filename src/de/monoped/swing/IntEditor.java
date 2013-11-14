package de.monoped.swing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatterFactory;
import java.text.ParseException;

class IntEditor
        extends JFormattedTextField
        implements ChangeListener {
    private int minval, maxval;
    private IntSpinner spinner;

    //----------------------------------------------------------------------

    class IntVerifier
            extends InputVerifier {
        public boolean verify(JComponent input) {
            AbstractFormatter formatter = getFormatter();

            if (formatter != null) {
                String text = getText();

                try {
                    int x = ((Number) formatter.stringToValue(text)).intValue();

                    return x >= minval && x <= maxval;
                } catch (ParseException pe) {
                    return false;
                }
            }

            return true;
        }

        //----------------------------------------------------------------------

        public boolean shouldYieldFocus(JComponent input) {
            return verify(input);
        }
    }

    //----------------------------------------------------------------------

    IntEditor(IntSpinner spinner, int n, int wert, int minval, int maxval) {
        this.spinner = spinner;
        this.minval = minval;
        this.maxval = maxval;
        setColumns(n);
        spinner.addChangeListener(this);
        setInputVerifier(new IntVerifier());
        setFormatterFactory(new DefaultFormatterFactory(new IntFormatter()));
        spinner.setValue(new Integer(wert));
    }

    //----------------------------------------------------------------------

    IntEditor(IntSpinner spinner, int n) {
        this(spinner, 0, n, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    //----------------------------------------------------------------------

    public void commitEdit()
            throws ParseException {
        super.commitEdit();
        spinner.setValue(super.getValue());
    }

    //----------------------------------------------------------------------

    public Object getValue() {
        return spinner.getValue();
    }

    //----------------------------------------------------------------------

    public void stateChanged(ChangeEvent e) {
        SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
        Number number = model.getNumber();
        int x = number.intValue();

        if (x >= minval && x <= maxval)
            setValue(number);
    }
}


