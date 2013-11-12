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

/** UI for HTTP connection. */

class HttpTransportUI
    extends TransportUI
{
    private JTextField          recvField, sendField, passwdField;
    private HttpTransport       trans;
    private JPanel              panel;
    
    //----------------------------------------------------------------------

    /** Construct UI from connection object. 
     *
     *  @param trans    Connection object.
     */
    
    HttpTransportUI(HttpTransport trans)
    {
        super(trans);
        this.trans = trans;

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        recvField = new JTextField(FIELDLEN);
        recvField.getDocument().addDocumentListener(this);
        sendField = new JTextField(FIELDLEN);
        passwdField = new JPasswordField(FIELDLEN);

        panel.add(new JLabel("<html><font color=\"blue\">Http"), gbc);
        ++gbc.gridx;
        panel.add(activeCheck, gbc);
        ++gbc.gridy; gbc.gridx = 0;

        Utils.addTextField(panel, gbc, nameField, 2, "name", trans.getName());
        Utils.addTextField(panel, gbc, recvField, 2, "recvUrl", trans.getRecvURL());
        Utils.addTextField(panel, gbc, sendField, 2, "sendUrl", trans.getSendURL());
        Utils.addTextField(panel, gbc, passwdField, 2, "passwd", trans.getPasswd());
    }

    //----------------------------------------------------------------------

    /** Get the UI panel. */

    public JPanel getPanel()
    {
        return panel;
    }

    //----------------------------------------------------------------------

    boolean isComplete()
    {
        return nameField.getText().trim().length() > 0 &&
               recvField.getText().trim().length() > 0; 
    }

    //----------------------------------------------------------------------

    /** Set connection data from UI. */

    public void setData()
    {
        trans.setName(Utils.textParam(nameField));
        trans.setRecvURL(Utils.textParam(recvField));
        trans.setSendURL(Utils.textParam(sendField));
        trans.setPasswd(Utils.textParam(passwdField));

//        trans.setActive(true);
    }

}

