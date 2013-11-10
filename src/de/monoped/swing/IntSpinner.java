package de.monoped.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

public class IntSpinner
    extends JSpinner
{
    public IntSpinner(int n)
    {
        this(n, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
    }

    //----------------------------------------------------------------------

    public IntSpinner(int n, int wert, int minval, int maxval, int step)
    {
        super(new SpinnerNumberModel(wert, minval, maxval, step));
        setEditor(new IntEditor(this, n, wert, minval, maxval));   
    }

    //----------------------------------------------------------------------

    public Object getValue()
    {
        if (getEditor() instanceof IntEditor)
        {
            IntEditor editor = (IntEditor)getEditor();

            if (! editor.getInputVerifier().verify(editor))
                editor.setText(getModel().getValue().toString());
        }

        return super.getValue();
    }
    
}


