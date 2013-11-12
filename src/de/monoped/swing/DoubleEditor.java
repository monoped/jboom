package de.monoped.swing;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import java.text.ParseException;

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

}



