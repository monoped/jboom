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

import de.monoped.utils.KeyBundle;

import javax.swing.*;
import java.net.URL;

public abstract class UIAction
    extends AbstractAction
{
    public UIAction(ImageIcon icon, KeyBundle bundle, String key, boolean enabled)
    {
        putValue(NAME, bundle.getText(key));
        putValue(MNEMONIC_KEY, bundle.getMnemonic(key));
        putValue(SHORT_DESCRIPTION, bundle.getTip(key));
        setEnabled(enabled);

        if (icon != null)
            putValue(SMALL_ICON, icon);
    }

    //----------------------------------------------------------------------

    public UIAction(ImageIcon icon, KeyBundle bundle, String key)
    {
        this(icon, bundle, key, true);
    }

    //----------------------------------------------------------------------

    public UIAction(URL iconurl, KeyBundle bundle, String key)
    {
        this(new ImageIcon(iconurl), bundle, key);
    }

    //----------------------------------------------------------------------

    public UIAction(String iconname, KeyBundle bundle, String key)
    {
        this(UIAction.class.getClassLoader().getResource(iconname),
                bundle, key);
    }

    //----------------------------------------------------------------------

    public UIAction(ImageIcon icon, KeyBundle bundle, String key, String accel)
    {
        this(icon, bundle, key);

        if (accel != null)
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accel));
    }

    //----------------------------------------------------------------------

    public UIAction(String iconname, KeyBundle bundle, String key, String accel)
    {
        this(new ImageIcon(UIAction.class.getClassLoader().getResource(iconname)),
                bundle, key, accel);
    }

    //----------------------------------------------------------------------

    public UIAction(ImageIcon icon, KeyBundle bundle, String key, KeyStroke accel)
    {
        this(icon, bundle, key);

        if (accel != null)
            putValue(ACCELERATOR_KEY, accel);
    }

    //----------------------------------------------------------------------

    public UIAction(String iconname, KeyBundle bundle, String key, KeyStroke accel)
    {
        this(new ImageIcon(UIAction.class.getClassLoader().getResource(iconname)),
                bundle, key, accel);
    }

    //----------------------------------------------------------------------

    public UIAction(KeyBundle bundle, String key)
    {
        this((ImageIcon)null, bundle, key);
    }

    //----------------------------------------------------------------------

    public UIAction(KeyBundle bundle, String key, boolean selected)
    {
        this((ImageIcon)null, bundle, key);
        putValue(SELECTED_KEY, selected);
    }
}

