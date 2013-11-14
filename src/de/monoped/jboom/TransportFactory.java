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

import de.monoped.utils.DOMUtils;
import de.monoped.utils.KeyBundle;
import org.w3c.dom.Element;

import javax.swing.*;
import java.util.ResourceBundle;

/**
 * Factory class for connections.
 */

class TransportFactory {
    private static int number;

    //----------------------------------------------------------------------

    /**
     * Create connection from an DOM element.
     */

    static Transport createTransport(Element el) {
        String mode = DOMUtils.getAttributeValue(el, "mode");

        if (mode.equals("ftp"))
            return FtpTransport.createTransport(el);

        if (mode.equals("ssh"))
            return ScpTransport.createTransport(el);

        if (mode.equals("http"))
            return HttpTransport.createTransport(el);

        return null;
    }

    //----------------------------------------------------------------------

    /**
     * Create connection from a URL string.
     */

    static Transport createTransport(String mode) {
        String name = ((KeyBundle) ResourceBundle.getBundle("de.monoped.jboom.Resources")).getText("newConn")
                + " " + ++number;

        if (mode.equals("ftp"))
            return new FtpTransport(name);

        if (mode.equals("ssh"))
            return new ScpTransport(name);

        if (mode.equals("http"))
            return new HttpTransport(name);

        return null;
    }

    //----------------------------------------------------------------------

    /**
     * Return array of available connection menu items.
     */

    static JMenuItem[] createNewItems() {
        JMenuItem ftpItem = new JMenuItem("ftp"),
                scpItem = new JMenuItem("ssh"),
                httpItem = new JMenuItem("http");

        ftpItem.setActionCommand("ftp");
        scpItem.setActionCommand("ssh");
        httpItem.setActionCommand("http");

        return new JMenuItem[]{ftpItem, scpItem, httpItem};
    }

}

