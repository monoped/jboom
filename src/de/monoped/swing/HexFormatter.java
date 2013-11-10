package de.monoped.swing;

import java.text.*;
import javax.swing.*;

public class HexFormatter
    extends JFormattedTextField.AbstractFormatter
{
    private int n, maxval;

    //----------------------------------------------------------------------

    HexFormatter(int n)
    {
        this.n = n;
        maxval = (1 << 4 * n) - 1;
    }

    //----------------------------------------------------------------------

    public String valueToString(Object value)
    {
        int x = value == null ? 0 : ((Number)value).intValue();

        x &= maxval;
        
        String s = Integer.toHexString(x);

        int l = s.length();

        for (int i = l; i < n; ++i)
            s = '0' + s;

        return s;
    }

    //----------------------------------------------------------------------

    public Object stringToValue(String s)
        throws ParseException
    {
        try
        {
            Integer k = Integer.valueOf(s, 16);
            int     i = k.intValue();

            if (i < 0 || i > maxval)
                throw new ParseException("Hex", 0);

            return k;
        }
        catch (NumberFormatException e)
        {
            throw new ParseException("Hex", 0);
        }
        
    }
    
}

