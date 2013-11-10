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
import de.monoped.swing.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;

/** Read page title from URL. */

class TitleReader
    extends RunWait<String>
{
    static KeyBundle    bundle = (KeyBundle)ResourceBundle.getBundle("de.monoped.jboom.Resources");

    private URL         url;

    //----------------------------------------------------------------------

    TitleReader(URL url)
    {
        super(null);
        
        setComponent(new JLabel( bundle.getText("downloading", url.toString()))); 
        setTitle(bundle.getText("wait"));
        setCancelText(bundle.getText("cancel"));
        this.url = url;
    }

    //----------------------------------------------------------------------

    public String result()
        throws Exception
    {
        BufferedReader  reader = null;

        try
        {
            InputStream in = url.openStream();

            reader = new BufferedReader(new InputStreamReader(in));

            StringBuilder    buf = new StringBuilder();

            outer: while (true)
            {
                String  line = reader.readLine();

                if (line == null)
                    break;
                
                String lowline = line.toLowerCase();
                
                int ka = lowline.indexOf("<title>");

                if (ka < 0)
                    continue;

                // found <title>
                
                int ke = lowline.indexOf("</title>", ka + 7);

                if (ke >= 0)
                {
                    buf.append(line.substring(ka + 7, ke));
                    break;
                }
                
                buf.append(line.substring(ka + 7, line.length()));

                while ((line = reader.readLine()) != null)
                {
                    lowline = line.toLowerCase(); 
                    ke = lowline.indexOf("</title>");

                    if (ke < 0)
                        buf.append(line);
                    else
                    {
                        // found </title>

                        buf.append(line.substring(0, ke));
                        break outer;
                    }
                }
            }

            return buf.toString().trim();
        }
        finally
        {
            try
            {
                if (reader != null)
                    reader.close();
            }
            catch (IOException e)
            {
                if (JBoom.isDebug())
                    e.printStackTrace();
            }
        }
    }
}

