package de.monoped.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Bitbuttons
        extends JPanel
        implements ActionListener {
    private JButton[] bitbuttons;
    private long bitwert;
    private int nBits, nActiveBits;
    private boolean selectable;
    private ActionListener listener;
    private Font buttonFont;
    static private final String TIP = "Click to invert";

    //----------------------------------------------------------------------

    public Bitbuttons(int nBits, int style, int fsize) {
        this.nBits = this.nActiveBits = nBits;
        selectable = true;
        setBackground(Color.WHITE);
        ((FlowLayout) getLayout()).setHgap(0);
        bitbuttons = new JButton[64];

        // Buttons erzeugen

        buttonFont = new Font("Monospaced", style, fsize);

        Insets nullInsets = new Insets(0, 0, 0, 0);
        Color bg1 = new Color(0xffff00),
                bg2 = new Color(0x00ffff);

        for (int i = 0; i < nBits; ++i) {
            JButton bitbutton = new JButton("0");

            bitbutton.addActionListener(this);
            bitbutton.setBorderPainted(false);
            bitbutton.setMargin(nullInsets);
            bitbutton.setFont(buttonFont);
            bitbutton.setToolTipText(TIP);

            if (i % 16 < 8)
                bitbutton.setBackground(bg1);
            else
                bitbutton.setBackground(bg2);

            add(bitbuttons[i] = bitbutton);
        }
    }

    //----------------------------------------------------------------------

    public Bitbuttons(int nBits) {
        this(nBits, Font.PLAIN, 10);
    }

    //----------------------------------------------------------------------

    /**
     * ActionEvent-Handler für Bitknöpfe.
     */

    public void actionPerformed(ActionEvent e) {
        if (!selectable)
            return;

        Object src = e.getSource();

        for (int i = 0; i < nBits; ++i)
            if (src == bitbuttons[i]) {
                // Bit invertieren

                if (bitbuttons[i].getText().equals("0"))
                    bitbuttons[i].setText("1");
                else
                    bitbuttons[i].setText("0");

                // neuer long-Wert

                long bit = 1L << (nBits - 1 - i);

                bitwert ^= bit;
                break;
            }

        processActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Bitbuttons"));
    }

    //----------------------------------------------------------------------

    public void addActionListener(ActionListener ls) {
        listener = AWTEventMulticaster.add(listener, ls);
    }

    //----------------------------------------------------------------------

    public int getNActiveBits() {
        return nActiveBits;
    }

    //----------------------------------------------------------------------

    public int getNBits() {
        return nBits;
    }

    //----------------------------------------------------------------------

    public Font getFont() {
        return buttonFont;
    }

    //----------------------------------------------------------------------

    public JLabel getScale() {
        String txt =
                "              56              48              40              32" +
                        "              24              16               8 7 6 5 4 3 2 1 0";
        if (nBits > 8)
            txt = "Bit" + txt.substring(67 - nBits);
        else
            txt = txt.substring(64 - nBits);

        JLabel lab = new JLabel(txt);

        lab.setFont(buttonFont);
        return lab;
    }

    //----------------------------------------------------------------------

    public long getValue() {
        return bitwert;
    }

    //----------------------------------------------------------------------

    public void processActionEvent(ActionEvent e) {
        if (listener != null)
            listener.actionPerformed(e);
    }

    //----------------------------------------------------------------------

    public void removeActionListener(ActionListener ls) {
        listener = AWTEventMulticaster.remove(listener, ls);
    }

    //----------------------------------------------------------------------

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;

        for (int i = 0; i < nBits; ++i)
            bitbuttons[i].setToolTipText(selectable ? TIP : null);
    }

    //----------------------------------------------------------------------

    public void setValue(long wert, int n) {
        nActiveBits = n;
        this.bitwert = wert;

        int imax = nBits - n;

        for (int i = nBits - 1; i >= imax; --i) {
            bitbuttons[i].setEnabled(true);
            bitbuttons[i].setText((wert & 1) == 0 ? "0" : "1");
            wert >>>= 1;
        }

        for (int i = 0; i < imax; ++i) {
            bitbuttons[i].setEnabled(false);
            bitbuttons[i].setText(" ");
        }
    }

    //----------------------------------------------------------------------

    public void setValue(long wert) {
        setValue(wert, nBits);
    }

    //----------------------------------------------------------------------

    public void setValue(Number x) {
        if (x == null)
            setValue(0L);
        else {
            Class c = x.getClass();

            if (c == Integer.class)
                setValue(x.intValue(), 32);
            else if (c == Short.class)
                setValue(x.shortValue(), 16);
            else if (c == Byte.class)
                setValue(x.byteValue(), 8);
            else if (c == Long.class)
                setValue(x.longValue());
            else if (c == Float.class)
                setValue(Float.floatToIntBits(x.floatValue()), 32);
            else if (c == Double.class)
                setValue(Double.doubleToLongBits(x.doubleValue()));
        }
    }
}

