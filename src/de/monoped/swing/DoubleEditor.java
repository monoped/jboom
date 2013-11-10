package de.monoped.swing;

import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

public class DoubleEditor
    extends JFormattedTextField
{
    private int         maxval;

    //----------------------------------------------------------------------

    class DoubleVerifier
        extends InputVerifier 
    {
        public boolean verify(JComponent input) 
        {
            AbstractFormatter formatter = getFormatter();

            if (formatter != null) 
            {
                String text = getText();
                 
                try 
                {
                    formatter.stringToValue(text);
                } 
                catch (ParseException pe) 
                {
                    return false;
                }
            }
            
            return true;
        }

        //----------------------------------------------------------------------

        public boolean shouldYieldFocus(JComponent input) 
        {
            return verify(input);
        }
    }        
    
    //----------------------------------------------------------------------

    public DoubleEditor(int n)
    {
        setColumns(n);
        setInputVerifier(new DoubleVerifier());
        setValue(new Double(0.0));
        setFormatterFactory(new DefaultFormatterFactory(new DoubleFormatter()));
    }

    //----------------------------------------------------------------------
    
    public void commitEdit()
        throws ParseException
    {
        super.commitEdit();
    }

}



