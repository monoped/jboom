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

import java.awt.*;
import java.util.*;

public class PasswordManager
{
    private Map<String, String> map;
    private Component           parent;
    private String              text, title;

    //----------------------------------------------------------------------

    public PasswordManager(Component parent, String title, String text)
    {
        this.parent = parent;
        this.title = title;
        this.text = text;
        map = new HashMap<String, String>();
    }

    //----------------------------------------------------------------------

    public void remove(String spec)
    {
        map.remove(spec);
    }

    //----------------------------------------------------------------------

    public String getPassword(String spec)
    {
        String pwd = map.get(spec);

        if (pwd == null)
        {
            pwd = new PasswordDialog(parent, title, text + spec, null).getPassword();

            if (pwd != null)
                map.put(spec, pwd);
        }

        return pwd;
    }
}

