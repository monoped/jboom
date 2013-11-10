package de.monoped.utils;

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

import java.util.*;
import javax.swing.*;

/** Wrapper for ResourceBundle with some convenience methods. */

public class Resource
{
    private ResourceBundle  bundle;
    private String          mark;

    //----------------------------------------------------------------------

    /** @param resClass     Class representing resourceBundle. */
     
    public Resource(String resClass)
    {
        bundle = ResourceBundle.getBundle(resClass, Locale.getDefault());
        mark = "~";
    }

    //----------------------------------------------------------------------

    /** Create a localized button. A character prefixed by the marker
     *  will be used as mnemonic, the marker will be deleted.
     *
     *  @param key          Resource key for button text.
     *
     *  @return             The created button.
     */
     
    public JButton createButton(String key)
    {
        return (JButton)nameButton(new JButton(), key);
    }

    //----------------------------------------------------------------------

    /** Create a localized checkbox. A character prefixed by the marker
     *  will be used as mnemonic, the marker will be deleted.
     *
     *  @param key          Resource key for button text.
     *  @param selected     Initial selection status.
     *
     *  @return             The created checkbox.
     */
     
    public JCheckBox createCheckBox(String key, boolean selected)
    {
        JCheckBox box = new JCheckBox();

        box.setSelected(selected);
        return (JCheckBox)nameButton(box, key);
    }

    //----------------------------------------------------------------------

    /** Create a localized label for an input field. A character prefixed by the marker
     *  will be used as mnemonic, the marker will be deleted.
     *
     *  @param key      Resource key for label text.
     *  @param input    Text field connected with this label.
     *
     *  @return         The created label.
     */
     
    public JLabel createLabel(String key, JTextField input)
    {
        JLabel  label = new JLabel();

        setLabelText(label, key);
        label.setLabelFor(input);
        return label;
    }

    //----------------------------------------------------------------------

    /** Create a localized label.
     *
     *  @param key      Resource key for label text.
     *
     *  @return         The created label.
     */
     
    public JLabel createLabel(String key)
    {
        return createLabel(key, null);
    }

    //----------------------------------------------------------------------

    /** Create a localized radio button. A character prefixed by the marker
     *  will be used as mnemonic, the marker will be deleted.
     *
     *  @param key          Resource key for button text.
     *
     *  @return             The created button.
     */
     
    public JRadioButton createRadioButton(String key)
    {
        return (JRadioButton)nameButton(new JRadioButton(), key);
    }

    //----------------------------------------------------------------------

    /** Get string denoted by resource key.
     *
     *  @param key      Resource key.
     *  @param delete   Delete markers.
     *
     *  @return         Resource value.
     */

    public String getString(String key, boolean delete)
    {
        String  text = bundle.getString(key);

        if (delete)
        {
            int     i = text.indexOf(mark);

            if (i >= 0)
                return text.substring(0, i) + text.substring(i + 1);
        }

        return text;
    }

    //----------------------------------------------------------------------

    /** Get string denoted by resource key with markers deleted.
     *
     *  @param key      Resource key.
     *
     *  @return         Resource value.
     */

    public String getString(String key)
    {
        return getString(key, true);
    }

    //----------------------------------------------------------------------

    /** Get string denoted by resource key with "%s" replaced.
     *
     *  @param key      Resource key.
     *  @param repl     Replacement string.
     *
     *  @return         Resource value, "%s" replaced by repl.
     */

    public String getString(String key, String repl)
    {
        return Strings.replaceString(bundle.getString(key), "%s", repl != null ? repl : "");
    }

    //----------------------------------------------------------------------

    /** Get string denoted by resource key with "%s1" and "%s2" replaced.
     *
     *  @param key      Resource key.
     *  @param repl1    Replacement string 1.
     *  @param repl2    Replacement string 2.
     *
     *  @return         Resource value, "%sX" replaced by replX.
     */

    public String getString(String key, String repl1, String repl2)
    {
        String s = Strings.replaceString(bundle.getString(key), "%s1", repl1 != null ? repl1 : "");

        return Strings.replaceString(s, "%s2", repl2 != null ? repl2 : "");
    }

    //----------------------------------------------------------------------

    /** Get string denoted by resource key with "%s1", "%s2", and "%s3" replaced.
     *
     *  @param key      Resource key.
     *  @param repl1    Replacement string 1.
     *  @param repl2    Replacement string 2.
     *  @param repl3    Replacement string 3.
     *
     *  @return         Resource value, "%sX" replaced by replX.
     */

    public String getString(String key, String repl1, String repl2, String repl3)
    {
        String s = Strings.replaceString(bundle.getString(key), "%s1", repl1 != null ? repl1 : "");

        s = Strings.replaceString(s, "%s2", repl2 != null ? repl2 : "");

        return Strings.replaceString(s, "%s3", repl3 != null ? repl3 : "");
    }

    //----------------------------------------------------------------------

    /** Set text of an existing button. A character prefixed by the marker
     *  will be used as mnemonic, the marker will be deleted.
     *
     *  @param button   The button.
     *  @param key      Resource key for button text.
     *
     *  @return         The changed button.
     */

    public AbstractButton nameButton(AbstractButton button, String key)
    {
        String  text = bundle.getString(key);
        int     i = text.indexOf(mark);

        if (i >= 0)
        {
            button.setMnemonic(text.charAt(i + 1));
            button.setText(text.substring(0, i) + text.substring(i + 1));
        }
        else
        {
            button.setMnemonic(0);
            button.setText(text);
        }

        return button;
    }

    //----------------------------------------------------------------------

    /** Set localized text for an existing label. A character prefixed by the marker
     *  will be used as mnemonic, the marker will be deleted.
     *
     *  @param label    The label.
     *  @param key      Resource key for label text.
     */

    public void setLabelText(JLabel label, String key)
    {
        String  text = bundle.getString(key);
        int     i = text.indexOf(mark);

        if (i >= 0)
        {
            label.setDisplayedMnemonic(text.charAt(i + 1));
            label.setText(text.substring(0, i) + text.substring(i + 1));
        }
        else
            label.setText(text);
    }

    //----------------------------------------------------------------------

    /** Change mnemo marker. 
     *
     *  @param mark     New marker.
     */

    public void setMark(String mark)
    {
        this.mark = mark;
    }

}

