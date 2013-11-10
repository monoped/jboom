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

/** Expand wildcards in file names
 *
 * @author      Bernd Eggink, RRZ Uni Hamburg (Bernd.Eggink@rrz.uni-hamburg.de)  
 *
 */

public class FileExpand
{
    private FileExpand()
    { }

    //----------------------------------------------------------------------

    /** Get List of files matching a wildcard expression.
     *
     *  @param basedir  Directory part, may be null.
     *  @param expr     File name containing wildcards
     */

    static public File[] getList(File basedir, String expr)
    {
        try
        {
            File file = (basedir != null ? new File(basedir, expr) : new File(expr));
            File dir = file.getParentFile();

            if (! dir.exists())
                return null;

            return dir.listFiles(new OSFilter(file.getName()));
        }
        catch (Exception ex)
        {
            System.err.println(ex);
            return null;
        }
    }
}
