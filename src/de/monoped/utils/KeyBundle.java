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

import java.awt.event.*;
import java.util.*;

/** Abstract base class for resources (text, mnemonic, tooltip) */

abstract public class KeyBundle
    extends ResourceBundle
{
    private HashMap<String, Entry> map;

    //----------------------------------------------------------------------

    protected KeyBundle()
    {
        map = new HashMap<String, Entry>();
    }

    //----------------------------------------------------------------------

    public Enumeration<String> getKeys() 
    { 
        return Collections.enumeration(map.keySet());
    }

    //----------------------------------------------------------------------

    public String keyText(int code, int modifiers)
    {
        return " [" + KeyEvent.getKeyModifiersText(modifiers)
            + "+" + KeyEvent.getKeyText(code) + "]";
    }

    //----------------------------------------------------------------------

    public String keyText(int code)
    {
        return " [" + KeyEvent.getKeyText(code) + "]";
    }

    //----------------------------------------------------------------------

    public int getMnemonic(String key)
    {
        return map.get(key).mnemo;
    }

    //----------------------------------------------------------------------

    public String getText(String key)
    {
        return map.get(key).text;
    }

    //----------------------------------------------------------------------

    public String getText(String... key)
    {
        String[]    ss = new String[key.length - 1],
                    sr = new String[key.length - 1];

        for (int i = 0; i < ss.length; ++i)
        {
            ss[i] = "%s" + i;
            sr[i] = key[i + 1];
        }

        return Strings.replaceStrings(map.get(key[0]).text, ss, sr);
    }

    //----------------------------------------------------------------------

    public String getText(String key, String repl)
    {
        return Strings.replaceString(map.get(key).text, "%s", repl != null ? repl : "");
    }

    //----------------------------------------------------------------------

    public String getText(String key, String repl1, String repl2)
    {
        String s = Strings.replaceString(map.get(key).text, "%s1", repl1 != null ? repl1 : "");

        return Strings.replaceString(s, "%s2", repl2 != null ? repl2 : "");
    }

    //----------------------------------------------------------------------

    public String getTip(String key)
    {
        return map.get(key).tip;
    }

    //----------------------------------------------------------------------

    public Object handleGetObject(String key)
    { 
        return map.get(key);
    }

    //----------------------------------------------------------------------

    protected void p(String key, String text, int mnemo, String tip)
    {
        map.put(key, new Entry(text, mnemo, tip));
    }

    //----------------------------------------------------------------------

    protected void p(String key, String text, int mnemo)
    {
        map.put(key, new Entry(text, mnemo, null));
    }

    //----------------------------------------------------------------------

    protected void p(String key, String text, String tip)
    {
        map.put(key, new Entry(text, 0, tip));
    }

    //----------------------------------------------------------------------

    protected void p(String key, String text)
    {
        map.put(key, new Entry(text, 0, null));
    }

    //----------------------------------------------------------------------

    static class Entry
    {
        String  text, tip;
        int     mnemo;

        //----------------------------------------------------------------------

        Entry(String text, int mnemo, String tip)
        {
            this.text = text;
            this.mnemo = mnemo;
            this.tip = tip;
        }
    }

}

