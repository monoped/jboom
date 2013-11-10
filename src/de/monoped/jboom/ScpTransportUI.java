package de.monoped.jboom;

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

import de.monoped.utils.*;
import de.monoped.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/** Transport UI for scp connections with public key authentication. */

class ScpTransportUI
    extends TransportUI
{
    static KeyBundle            bundle = (KeyBundle)ResourceBundle.getBundle("de.monoped.jboom.Resources");

    private JButton             browseButton;
    private JTextField          hostField, userField, passwdField, keyfileField, fileField;
    private GridBagConstraints  gbc;
    private ScpTransport        trans;
    private JPanel              panel;
    private JRadioButton        passwdButton, keyButton;
    private ButtonGroup         scpGroup;
    
    //----------------------------------------------------------------------

    ScpTransportUI(final ScpTransport trans)
    {
        super(trans);
        this.trans = trans;

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());

        gbc = new GridBagConstraints();  

        gbc.gridx = gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        hostField = new JTextField();
        hostField.getDocument().addDocumentListener(this);
        userField = new JTextField();
        userField.getDocument().addDocumentListener(this);
        passwdField = new JPasswordField(FIELDLEN);
        passwdField.getDocument().addDocumentListener(this);
        passwdField.setText(trans.getPasswd());
        fileField = new JTextField();
        fileField.getDocument().addDocumentListener(this);

        browseButton = new JButton(new UIAction(bundle, "browse")
        {
            public void actionPerformed(ActionEvent e)
            {
                String  file = Utils.browseFile(keyfileField.getText());
                
                if (file != null)
                {
                    trans.setKeyfile(file);
                    keyfileField.setText(file);
                }

                checkInput();
            }
        });

        passwdButton = new JRadioButton(new UIAction(bundle, "passwd")
        {
            public void actionPerformed(ActionEvent e)
            {
                passwdField.setEnabled(true);
                passwdField.requestFocus();
                keyfileField.setEnabled(false);
                browseButton.setEnabled(false);
                checkInput();
            }
        });

        keyButton = new JRadioButton(new UIAction(bundle, "keyFile")
        {
            public void actionPerformed(ActionEvent e)
            {
                passwdField.setEnabled(false);
                keyfileField.setEnabled(true);
                keyfileField.requestFocus();
                browseButton.setEnabled(true);
                checkInput();
            }
        });

        scpGroup = new ButtonGroup();
        scpGroup.add(passwdButton);
        scpGroup.add(keyButton);
        
        panel.add(new JLabel("<html><font color=\"blue\">Ssh"), gbc);
        ++gbc.gridx;
        panel.add(activeCheck, gbc);
        ++gbc.gridy; gbc.gridx = 0;

        Utils.addTextField(panel, gbc, nameField, 2, "name", trans.getName());
        Utils.addTextField(panel, gbc, hostField, 2, "host", trans.getHost());
        Utils.addTextField(panel, gbc, userField, 2, "user", trans.getUser());

        ++gbc.gridy; gbc.gridx = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(passwdButton, gbc);
        ++gbc.gridx; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(passwdField, gbc);
        gbc.gridwidth = 1;

        ++gbc.gridy; gbc.gridx = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(keyButton, gbc);
        ++gbc.gridx; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        keyfileField = new JTextField(FIELDLEN);
        keyfileField.getDocument().addDocumentListener(this);
        keyfileField.setText(trans.getKeyfile());
        panel.add(keyfileField, gbc);
        
        ++gbc.gridx; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(browseButton, gbc);
    
        if (trans.getMethod() == ScpTransport.PASSWD)
        {
            passwdButton.setSelected(true);
            keyfileField.setEnabled(false);
            browseButton.setEnabled(false);
        }
        else
        {
            keyButton.setSelected(true);
            passwdField.setEnabled(false);
        }

        Utils.addTextField(panel, gbc, fileField, 2, "remFile", trans.getFile());
    }

    //----------------------------------------------------------------------

    public JPanel getPanel()
    {
        return panel;
    }

    //----------------------------------------------------------------------

    boolean isComplete()
    {
        boolean result = true;

        if (nameField.getText().trim().length() == 0 ||
                hostField.getText().trim().length() == 0 ||
                userField.getText().trim().length() == 0 ||
                fileField.getText().trim().length() == 0)
            result = false;
        else if (keyButton.isSelected())
            result = keyfileField.getText().trim().length() > 0;

        return result;
    }

    //----------------------------------------------------------------------

    public void setData()
    {
        trans.setName(Utils.textParam(nameField));
        trans.setHost(Utils.textParam(hostField));
        trans.setUser(Utils.textParam(userField));
        trans.setKeyfile(Utils.textParam(keyfileField));
        trans.setFile(Utils.textParam(fileField));

//        trans.setActive(activeCheck.isSelected());
        trans.setPasswd(Utils.textParam(passwdField));
        trans.setMethod(passwdButton.isSelected() ? ScpTransport.PASSWD : ScpTransport.PUBKEY);
    }
}
