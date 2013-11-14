package de.monoped.swing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatterFactory;
import java.text.ParseException;

class HexEditor
        extends JFormattedTextField
        implements ChangeListener {
    private int n, maxval;
    private HexSpinner spinner;

    //----------------------------------------------------------------------

    class HexVerifier
            extends InputVerifier {
        public boolean verify(JComponent input) {
            AbstractFormatter formatter = getFormatter();

            if (formatter != null) {
                String text = getText();

                try {
                    formatter.stringToValue(text);
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

    HexEditor(HexSpinner spinner, int n) {
        this.n = n;
        maxval = (1 << 4 * n) - 1;
        this.spinner = spinner;
        setColumns(n);
        spinner.addChangeListener(this);
        setInputVerifier(new HexVerifier());
        setValue(new Integer(0));
        setFormatterFactory(new DefaultFormatterFactory(new HexFormatter(n)));
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
        Number num = model.getNumber();
        int x = num.intValue() & maxval;
        String s = Integer.toHexString(x);

        int l = s.length();

        for (int i = l; i < n; ++i)
            s = '0' + s;

        setText(s);
    }
}


