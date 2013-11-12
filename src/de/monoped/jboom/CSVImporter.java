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

import java.io.*;
import java.util.*;

public class CSVImporter
{
    public ArrayList<String[]> importCSV(Reader in, char sep)
        throws IOException
    {
        BufferedReader      reader = new BufferedReader(in);
        String              line;
        ArrayList<String[]> lines = new ArrayList<String[]>();
        
        while ((line = reader.readLine()) != null)
        {
            line = line.trim();

            ArrayList<String>   vals = new ArrayList<String>();
            boolean             inquote = false, wasquote = false;
            StringBuilder       word = new StringBuilder();

            for (int i = 0; i < line.length(); ++i)
            {
                char c = line.charAt(i);

                if (c == '"')
                    if (wasquote)
                    {
                        word.append('"');
                        wasquote = false;
                    }
                    else
                        wasquote = true;
                else 
                {    
                    if (wasquote)
                    {
                        if (inquote)
                            inquote = false;
                        else
                            inquote = true;
                    }

                    wasquote = false;

                    if (c == sep)
                        if (inquote)
                            word.append(c);
                        else
                        {
                            vals.add(word.toString());
                            word = new StringBuilder();
                            inquote = wasquote = false;
                        }
                    else
                        word.append(c);
                }
            }

            vals.add(word.toString());
            lines.add(vals.toArray(new String[0]));
        }

        in.close();

        return lines;
    }

}

