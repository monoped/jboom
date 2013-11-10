package de.monoped.swing;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

public class IntSpinnerModel
    implements SpinnerModel
{
    int             wert, min, max;
    ArrayList       listener;

    //----------------------------------------------------------------------

    IntSpinnerModel(int wert, int min, int max)
    {
        this.wert = wert;
        this.min = min;
        this.max = max;
        listener = new ArrayList();
    }

    //----------------------------------------------------------------------

    public Object getNextValue()
    {   
        return new Integer(wert < max ? ++wert : wert);
    }

    //----------------------------------------------------------------------

    public Object getPreviousValue()
    {   
        return new Integer(wert > min ? --wert : wert);
    }

    //----------------------------------------------------------------------

    public Object getValue()
    {   
        return new Integer(wert);
    }

    //----------------------------------------------------------------------

    public int getIntValue()
    {
        return wert;
    }
    
    //----------------------------------------------------------------------

    public void setValue(Object obj)
    {   
        wert = ((Number)obj).intValue();

        if (listener.size() > 0)
        {
            ChangeEvent e = new ChangeEvent(this);
                
            for (Iterator it = listener.iterator(); it.hasNext(); )
                ((ChangeListener)it.next()).stateChanged(e);
        }
    }

    //----------------------------------------------------------------------

    public void addChangeListener(ChangeListener changelistener)
    {   
        listener.add(changelistener);
    }

    //----------------------------------------------------------------------

    public void removeChangeListener(ChangeListener changelistener)
    {   
        listener.remove(changelistener);
    }
}


