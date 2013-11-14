package de.monoped.swing;

/* This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * monoped@users.sourceforge.net
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Modal dialog prompting user for a password.
 */

public class PasswordDialog
        extends JDialog
        implements ActionListener, KeyListener {
    static final int DIALOG_WIDTH = 600, DIALOG_HEIGHT = 150;

    private JButton okButton, cancelButton;
    private JPasswordField pwField;
    private String password;

    //----------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param parent Dialog is centered in this component. If null,
     *               dialog is centered in the screen.
     * @param title  Dialog title.
     * @param text   Text left of input field.
     * @param passwd Initial password, may be null.
     */

    public PasswordDialog(Component parent, String title, String text, String passwd) {
        setBounds(Utilities.centerComponent(parent, DIALOG_WIDTH, DIALOG_HEIGHT));
        setTitle(title);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setModal(true);

        JPanel content = new JPanel(),
                center = new JPanel(),
                bottom = new JPanel();

        pwField = new JPasswordField(8);

        if (passwd != null)
            pwField.setText(passwd);

        content.setLayout(new BorderLayout());

        center.add(new JLabel(text));
        center.add(pwField);
        pwField.requestFocus();
        pwField.addKeyListener(this);
        content.add(center);

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        bottom.add(okButton);
        bottom.add(cancelButton);

        content.add(bottom, BorderLayout.SOUTH);
        setContentPane(content);
    }

    //----------------------------------------------------------------------

    /**
     * Show dialog and return the password.
     */

    public String getPassword() {
        setVisible(true);
        return password;
    }

    //----------------------------------------------------------------------
    // ActionListener method
    //----------------------------------------------------------------------

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == okButton) {
            password = new String(pwField.getPassword());
            setVisible(false);
        } else if (src == cancelButton) {
            password = null;
            setVisible(false);
        }
    }

    //----------------------------------------------------------------------
    // KeyListener methods
    //----------------------------------------------------------------------

    public void keyTyped(KeyEvent keyevent) {
        if (keyevent.getKeyChar() == '\n') {
            password = new String(pwField.getPassword());
            setVisible(false);
        }
    }

    public void keyPressed(KeyEvent keyevent) {
    }

    public void keyReleased(KeyEvent keyevent) {
    }

}

