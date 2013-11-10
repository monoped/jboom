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

/** Some string utilities. */

public class Strings
{
    static private final String[]   xmlChars =      { "<",    ">",    "\"",     "&" };  
    static private final String[]   xmlCharsExt =   { "<",    ">",    "\"",     "&",     " " };  
    static private final String[]   xmlEntities =   { "&lt;", "&gt;", "&quot;", "&amp;", "&nbsp;" };

    //----------------------------------------------------------------------

    /** Replace all occurences of a substring.
     *
     *  @param s        Source string.
     *  @param sOld     Substring to replace.
     *  @param sNew     Replacement.
     *
     *  @return         Changed string.
     */

    public static String replaceString(String s, String sOld, String sNew)
    {
        if (s == null)
            return null;

        int             k = 0,
                        lOld = sOld.length(),
                        lNew = sNew.length();
        StringBuilder    buf = new StringBuilder(s);

        while ((k = buf.indexOf(sOld, k)) >= 0)
        {
            buf.replace(k, k + lOld, sNew);
            k += lNew;
        }
        
        return buf.toString();
    }

    //----------------------------------------------------------------------

    /** Replace different substrings.
     *
     *  @param s        Source string.
     *  @param sOld     Substrings to replace.
     *  @param sNew     Replacements.
     *
     *  @return         Changed string.
     */

    public static String replaceStrings(String s, String[] sOld, String[] sNew)
    {
        if (s == null)
            return null;

        StringBuilder    buf = new StringBuilder(s);

        for (int i = 0; i < sOld.length; ++i)
        {
            String  so = sOld[i],
                    sn = i < sNew.length ? sNew[i] : "";
            int     k = 0,
                    lo = so.length(),
                    ln = sn.length(); 

            while ((k = buf.indexOf(so, k)) >= 0)
            {
                buf.replace(k, k + lo, sn);
                k += ln;
            }
        }
        
        return buf.toString();
    }

    //----------------------------------------------------------------------

    /** Replace XML-relevant characters by entities
     *
     *  @param  s   Source string.
     *
     *  @return     Changed string.
     */

    static public String xmlCharsToEntities(String s)
    {
        return replaceStrings(s, xmlChars, xmlEntities);
    }
    
    //----------------------------------------------------------------------

    /** Replace XML-relevant entities by chars
     *
     *  @param  s   Source string.
     *
     *  @return     Changed string.
     */

    static public String xmlEntitiesToChars(String s)
    {
        s = replaceStrings(s, xmlEntities, xmlCharsExt);
        
        return s == null ? null : s.replaceAll("&[^;]*;", "");
    }
}
