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
import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;

/** UI for FTP connection. */

class FtpTransportUI
    extends TransportUI
{
    private JTextField          hostField, userField, passwdField, fileField;
    private GridBagConstraints  gbc;
    private FtpTransport        trans;
    private JPanel              panel;
    
    //----------------------------------------------------------------------

    /** Construct UI from connection object. 
     *
     *  @param trans    Connection object.
     */
    
    FtpTransportUI(FtpTransport trans)
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

        hostField = new JTextField(FIELDLEN);
        userField = new JTextField(FIELDLEN);
        passwdField = new JPasswordField(FIELDLEN);
        fileField = new JTextField(FIELDLEN);

        panel.add(new JLabel("<html><font color=\"blue\">Ftp"), gbc);
        ++gbc.gridx;
        panel.add(activeCheck, gbc);
        ++gbc.gridy; gbc.gridx = 0;

        hostField.getDocument().addDocumentListener(this);
        userField.getDocument().addDocumentListener(this);
        passwdField.getDocument().addDocumentListener(this);
        fileField.getDocument().addDocumentListener(this);

        Utils.addTextField(panel, gbc, nameField, 2, "name", trans.getName());
        Utils.addTextField(panel, gbc, hostField, 2, "host", trans.getHost());
        Utils.addTextField(panel, gbc, userField, 2, "user", trans.getUser());
        Utils.addTextField(panel, gbc, passwdField, 2, "passwd", trans.getPasswd());
        Utils.addTextField(panel, gbc, fileField, 2, "remFile", trans.getFile());

        checkInput();
    }

    //----------------------------------------------------------------------

    /** Get the UI panel. */

    public JPanel getPanel()
    {
        return panel;
    }

    //----------------------------------------------------------------------

    /** Is input complete? */

    boolean isComplete()
    {
        return nameField.getText().trim().length() > 0 &&
            hostField.getText().trim().length() > 0 &&
            userField.getText().trim().length() > 0 &&
            fileField.getText().trim().length() > 0;
    }

    //----------------------------------------------------------------------

    /** Set and check connection data from UI. */

    public void setData()
    {
        trans.setName(Utils.textParam(nameField));
        trans.setHost(Utils.textParam(hostField));
        trans.setUser(Utils.textParam(userField));
        trans.setPasswd(Utils.textParam(passwdField));
        trans.setFile(Utils.textParam(fileField));

//        trans.setActive(activeCheck.isSelected());
    }

}

