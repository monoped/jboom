package de.monoped.swing;

import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

public class IntInput
    extends JFormattedTextField
{
    private int minval, maxval;

    //----------------------------------------------------------------------

    class IntVerifier
        extends InputVerifier 
    {
        public boolean verify(JComponent input) 
        {
            try 
            {
                int x = ((Number)getFormatter().stringToValue(getText())).intValue();

                return x >= minval && x <= maxval;
            } 
            catch (ParseException e) 
            {
                return false;
            }
        }

        //----------------------------------------------------------------------

        public boolean shouldYieldFocus(JComponent input) 
        {
            return verify(input);
        }
    }        
    
    //----------------------------------------------------------------------

    public IntInput(int n, int minval, int maxval)
    {
        super(new Integer(0));
        this.minval = minval;
        this.maxval = maxval;
        setColumns(n);
        setInputVerifier(new IntVerifier());
    }

    //----------------------------------------------------------------------

    public IntInput(int n)
    {
        this(n, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    
    //----------------------------------------------------------------------
    
    public void commitEdit()
        throws ParseException
    {
        super.commitEdit();
        setValue(super.getValue());
    }

    //----------------------------------------------------------------------

    public boolean isValid()
    {
        return isEditValid() && getInputVerifier().verify(this);
    }
}


