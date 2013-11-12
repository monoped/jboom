package de.monoped.swing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

public class IntSpinnerModel
    implements SpinnerModel
{
    int             wert, min, max;
    ArrayList<ChangeListener> listener;

    //----------------------------------------------------------------------

    IntSpinnerModel(int wert, int min, int max)
    {
        this.wert = wert;
        this.min = min;
        this.max = max;
        listener = new ArrayList<ChangeListener>();
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
                
            for (Iterator<ChangeListener> it = listener.iterator(); it.hasNext(); )
                (it.next()).stateChanged(e);
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


