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
 * Bernd.Eggink@rrz.uni-hamburg.de
 */

import java.io.*;
import java.util.*;

/** File filter for names with wildcards
 *
 * @author      Bernd Eggink, RRZ Uni Hamburg (Bernd.Eggink@rrz.uni-hamburg.de)  
 *
 */

public class OSFilter
    implements FilenameFilter
{
    private String expr;

    //----------------------------------------------------------------------

    /** Contructor
     *
     *  @param expr File path  containing wildcards
     */

    public OSFilter(String expr)
    {
        this.expr = expr;
    }

    //----------------------------------------------------------------------

    public boolean accept(File dir, String path)
    {
        if (expr == null)
            return true;
        
        int     iExpr = 0, 
                iPath = 0,
                lenExpression = expr.length(),
                lenPath = path.length();

        while (iExpr < lenExpression)
        {
            char charExpr = expr.charAt(iExpr);

            switch (charExpr)
            {
                case '?':
                {
                    // any character
                    
                    if (iPath >= lenPath)
                        return false;

                    ++iExpr;
                    ++iPath;
                    break;
                }

                case '*':
                {
                    // character sequence 
                    
                    int nqu = 0;
                    
                    // find normal chars in expr after '*', count '?'s

                    while (++iExpr < lenExpression)
                    {
                        char c = expr.charAt(iExpr);

                        if (c == '?')
                            ++nqu;
                        else if (c != '*')
                            break;
                    }
                     
                    if (iExpr >= lenExpression)
                    {
                        // at end, no normal chars after *, just check '?'s
                           
                        return lenPath - iPath >= nqu;
                    }
                    else
                    {
                        // extract normal part

                        int jex = iExpr;

                        while (++jex < lenExpression)
                        {
                            char c = expr.charAt(jex);

                            if (c == '*' || c == '?')
                                break;
                        }
                    
                        String  normal = expr.substring(iExpr, jex);
                        int     ifound = path.indexOf(normal, iPath);

                        if (ifound < 0 || ifound - iPath < nqu)
                        {
                            // not found in path, or too early
                               
                            return false;
                        }

                        // adjust indices

                        iPath = ifound + normal.length();
                        iExpr = jex;

                        break;
                    }
                }
                
                default:
                {
                    // normal char
                       
                    if (iPath >= lenPath || charExpr != path.charAt(iPath))
                        return false;

                    ++iExpr;
                    ++iPath;
                }
            }
        }

        return iPath == lenPath;
    }
    
}

