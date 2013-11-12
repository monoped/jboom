package de.monoped.swing;

import info.clearthought.layout.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/** Liste in einer Scrollpane. Einfacher Klick auf Item markiert,
 *  doppelter schickt ActionEvent an Listener mit Zeilen-Komponente als
 *  Source. Benötigt TableLayout.
 */

public class VList
    extends JScrollPane
    implements MouseListener
{
    private String                  action;
    private boolean                 selectable;
    private int                     marked;
    private Color                   selectionColor;
    private java.util.List<JComponent> zeilen;
    private ActionListener          actionListener;
    private ArrayList<ListSelectionListener> selectionListener;
    private JPanel                  panel;

    //----------------------------------------------------------------------

    class AuswahlAction
        extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            processActionEvent(new ActionEvent(zeilen.get(marked), ActionEvent.ACTION_PERFORMED, action));
        }
    }

    //----------------------------------------------------------------------

    class DownAction
        extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            selectItem(marked < zeilen.size() - 1 ? marked + 1 : 0);           
        }
    }
    
    //----------------------------------------------------------------------

    class UpAction
        extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            selectItem(marked == 0 ?  zeilen.size() - 1 : marked - 1);           
        }
    }
    
    //----------------------------------------------------------------------

    /** Konstruktor.
     *
     *  @param  zeilen      ArrayList mit JComponents für die Zeilen.
     *  @param  selectable  wenn true, können Zeilen markiert werden.
     */

    public VList(ArrayList<JComponent> zeilen, boolean selectable)
    {
        this.zeilen = zeilen;
        this.selectable = selectable;
        selectionColor = new Color(0xa0efbe);
        action = "";
        panel = new JPanel();

        SingleFiledLayout layout = new SingleFiledLayout(SingleFiledLayout.COLUMN,
                SingleFiledLayout.FULL, 2);
        
        panel.setLayout(layout);

        setViewportView(panel);
        setItems(zeilen);       
        marked = -1;

        if (selectable)
        {
            panel.addMouseListener(this);

            InputMap    inputMap = getInputMap();
            ActionMap   actionMap = getActionMap();

            inputMap.put(KeyStroke.getKeyStroke("SPACE"), "auswahl");
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "runter");
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "rauf");

            actionMap.put("auswahl", new AuswahlAction());
            actionMap.put("runter", new DownAction());
            actionMap.put("rauf", new UpAction());
        }
    }

    //----------------------------------------------------------------------

    /** Konstruktor für markierbare Liste.
     *
     *  @param  zeilen      ArrayList mit JComponents für die Zeilen.
     */

    public VList(ArrayList<JComponent> zeilen)
    {
        this(zeilen, true);
    }

    //----------------------------------------------------------------------

    public VList(boolean selectable)
    {
        this(new ArrayList<JComponent>(), selectable);
    }

    //----------------------------------------------------------------------

    public VList()
    {
        this(true);
    }

    //----------------------------------------------------------------------

    public Component add(Component comp)
    {
        throw new UnsupportedOperationException("Don't use add(). Use addLine() to add items to the list.");
    }
    
    //----------------------------------------------------------------------

    public void addActionListener(ActionListener ls)
    {
        actionListener = AWTEventMulticaster.add(actionListener, ls);
    }
    
    //----------------------------------------------------------------------

    /** Zeile anhängen */

    public void addLine(JComponent comp)
    {
        zeilen.add(comp);   
        panel.add(comp);
        comp.setBackground(getBackground());
        setBg(comp, getBackground());
        comp.setOpaque(true);
    }

    //----------------------------------------------------------------------

    public void addListSelectionListener(ListSelectionListener lisel)
    {
        if (selectionListener == null)
            selectionListener = new ArrayList<ListSelectionListener>();

        selectionListener.add(lisel);   
    }
    
    //----------------------------------------------------------------------

    /** MouseListener an das Zeilenpanel hängen.
     *
     *  @param  actionListener    Listener
     */

    public void addMouseListener(MouseListener actionListener)
    {
        panel.addMouseListener(actionListener);
    }

    //----------------------------------------------------------------------

    public void clear()
    {
        zeilen.clear();
        panel.removeAll();
        marked = -1;
    }
    
    //----------------------------------------------------------------------

    /** Gib die markierte Zeilenkomponente oder null */

    public JComponent getSelectedItem()
    {
        return marked >= 0 ? zeilen.get(marked) : null;
    }
    
    //----------------------------------------------------------------------

    /** Gib Index der markierten Zeile */

    public int getSelection()
    {
        return marked;
    }

    //----------------------------------------------------------------------

    /** MouseListener. Einfache Klicks markieren. Bei Doppelklick wird ActionEvent an
     *  angemeldete ActionListener geschickt.
     */

    public void mouseClicked(MouseEvent mouseevent)
    {   
        Point   p = mouseevent.getPoint();
        boolean ok = false;
        int     i = 0;
        
        for (Iterator<JComponent> it = zeilen.iterator(); it.hasNext(); i++)
            if ((ok = (it.next()).getBounds().contains(p)))
                break;
                    
        if (! ok)
            return;

        switch (mouseevent.getClickCount())
        {
            case 1:
            {
                selectItem(i);
                break;
            }

            case 2:
                processActionEvent(new ActionEvent(zeilen.get(i), ActionEvent.ACTION_PERFORMED, action));
                break;
        }
    }

    public void mouseEntered(MouseEvent mouseevent)
    {   }

    public void mouseExited(MouseEvent mouseevent)
    {   }

    public void mousePressed(MouseEvent mouseevent)
    {   }

    public void mouseReleased(MouseEvent mouseevent)
    {   }

    //----------------------------------------------------------------------

    /** Zeile um 1 nach unten schieben */
    
    public void moveLineDown(int index)
    {
        if (index < 0)
            return;

        int         indexNeu = index >= zeilen.size() - 1 ? 0 : index + 1;
        JComponent  comp = zeilen.remove(index);

        zeilen.add(indexNeu, comp);
        
        panel.remove(index);
        panel.add(comp, indexNeu);

        if (index == marked)
            selectIntern(indexNeu);
        else if (indexNeu == marked) 
            selectIntern(index);

        refresh();
    }
    
    //----------------------------------------------------------------------

    /** Zeile um 1 nach oben schieben */
    
    public void moveLineUp(int index)
    {
        if (index < 0)
            return;

        int         indexNeu = index == 0 ? zeilen.size() - 1 : index - 1;
        JComponent  comp = zeilen.remove(index);

        zeilen.add(indexNeu, comp);
        panel.remove(index);
        panel.add(comp, indexNeu);
        
        if (index == marked)
            selectIntern(indexNeu);
        else if (indexNeu == marked) 
            selectIntern(index);

        refresh();
    }

    //----------------------------------------------------------------------

    /** Markierte Zeile um 1 nach unten schieben */
    
    public void moveLineDown()
    {
        moveLineDown(marked);
    }
    
    //----------------------------------------------------------------------

    /** Markierte Zeile um 1 nach oben schieben */
    
    public void moveLineUp()
    {
        moveLineUp(marked);
    }
    
    //----------------------------------------------------------------------

    /** ActionEvent an die Listener schicken.
     *
     * @param e     Das Eventobjekt.
     */

    public void processActionEvent(ActionEvent e)
    {
        if (actionListener != null)
            actionListener.actionPerformed(e);
    }

    //----------------------------------------------------------------------

    public void processListSelectionEvent()
    {
        if (selectionListener == null)
            return;

        ListSelectionEvent lise = new ListSelectionEvent(zeilen.get(marked), marked, marked, false);

        for (Iterator<ListSelectionListener> it = selectionListener.iterator(); it.hasNext(); )
            (it.next()).valueChanged(lise);
    }

    //----------------------------------------------------------------------

    /** Neu darstellen */

    public void refresh()
    {
        panel.validate();
        panel.repaint();
        validate();
        selectIntern(marked);
//        repaint();
    }
    
    //----------------------------------------------------------------------

    public void remove(Component comp)
    {
        throw new UnsupportedOperationException("Don't use remove(). Use removeLine() to remove items from the list.");
    }
    
    //----------------------------------------------------------------------

    /** ActionListener entfernen.
     *
     *  @param ls   Der Listener.
     */

    public void removeActionListener(ActionListener ls)
    {
        actionListener = AWTEventMulticaster.remove(actionListener, ls);
    }

    //----------------------------------------------------------------------

    /** Zeile entfernen */

    public void removeLine(int index)
    {
        if (index < 0 || index >= zeilen.size())
            return;

        zeilen.remove(index);
        panel.remove(index);
        
        if (index == marked)
            marked = -1;
        else if (index > marked)
            selectItem(index - 1);
    }

    //----------------------------------------------------------------------

    /** Markierte Zeile entfernen */

    public void removeLine()
    {
        removeLine(marked);
    }
    
    //----------------------------------------------------------------------

    public void removeListSelectionListener(ListSelectionListener lisel)
    {
        selectionListener.remove(lisel);
    }
    
    //----------------------------------------------------------------------

    private void selectIntern(int index)
    {
        Component comp;

        if (marked >= 0 && marked != index)
        {
            comp = zeilen.get(marked);
            comp.setBackground(getBackground());
            setBg(comp, getBackground());
        }
            
        marked = index;

        if (marked >= 0)
        {
            comp = zeilen.get(marked);
            comp.setBackground(selectionColor);
            setBg(comp, selectionColor);
            panel.scrollRectToVisible(comp.getBounds());
        }
    }
    
    //----------------------------------------------------------------------

    /** Markiere eine Zeile.
     *
     *  @param index Index der Zeile.
     */

    public void selectItem(int index)
    {
        if (index >= zeilen.size() || index < 0)
            return;

        selectIntern(index);
        processListSelectionEvent();
    }

    //----------------------------------------------------------------------

    public void selectItem(Component comp)
    {
        int index = zeilen.indexOf(comp);
        
        selectItem(index);
    }
    
    //----------------------------------------------------------------------

    public void setActionCommand(String action)
    {
        this.action = action;
    }

    //----------------------------------------------------------------------

    /** Hintergrundfarbe setzen */

    public void setBackground(Color bgcolor)
    {
        super.setBackground(bgcolor);

        if (panel != null)
            panel.setBackground(bgcolor);

        getViewport().setBackground(bgcolor);

        if (zeilen != null)
        {
            for (Iterator<JComponent> it = zeilen.iterator(); it.hasNext(); )
            {
                Component comp = it.next();

                comp.setBackground(bgcolor);
                setBg(comp, bgcolor);
            }
            
            selectItem(marked);
        }
    }
        
    //----------------------------------------------------------------------

    /** Rekursives Setzen der Hintergrundfarbe einer Zeile, wenn es
     *  sich um einen Swing-Container handelt. 
     */
    
    private void setBg(Component comp, Color color)
    {
        if (comp instanceof JPanel)
        {
            comp.setBackground(color);
            
            Container cont = (Container)comp;

            for (int i = 0; i < cont.getComponentCount(); ++i)
                setBg(cont.getComponent(i), color);
        }
    }

    //----------------------------------------------------------------------

    public void setItems(ArrayList<JComponent> zeilen)
    {
        panel.removeAll();

        this.zeilen = zeilen;
        marked = -1;

        if (zeilen != null)
        {
            for (Iterator<JComponent> it = zeilen.iterator(); it.hasNext(); )
            {
                JComponent comp = it.next();
                
                panel.add(comp);
                comp.setBackground(getBackground());
                setBg(comp, getBackground());
                comp.setOpaque(true);
            }
        }

        panel.doLayout();
        doLayout();
    }

    //----------------------------------------------------------------------

    public void setLine(int index, JComponent comp)
    {
        panel.remove(index);
        zeilen.set(index, comp);

        panel.add(comp, index);
        comp.setBackground(getBackground());
        setBg(comp, getBackground());
        comp.setOpaque(true);
    }
    
    //----------------------------------------------------------------------

    public void setSelectionColor(Color selectionColor)
    {
        this.selectionColor = selectionColor;
    }

    //----------------------------------------------------------------------

    /** Setze Markierbarkeit
     *
     *  @param selectable   Wenn true, können Zeilen markiert werden.
     */

    public void setUserSelectable(boolean selectable)
    {
        this.selectable = selectable;
    }

}

