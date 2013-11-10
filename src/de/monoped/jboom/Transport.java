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
import java.awt.*;
import java.awt.event.*;

/** Abstract connection base class. */

public abstract class Transport
    implements Cloneable
{
    static final String     CANCELLED = "cancelled";

    private String          message, name;
    private boolean         active, writeable;

    //----------------------------------------------------------------------

    protected Transport(String name, boolean active)
    {  
        this.name = name;
        this.active = active;
    }

    //----------------------------------------------------------------------

    public void dump()
    { }

    //----------------------------------------------------------------------

    public String getMessage()
    {
        return message;
    }

    //----------------------------------------------------------------------

    public String getName()
    {
        return name;
    }
    
    //----------------------------------------------------------------------

    boolean isActive()
    {
        return active;
    }

    //----------------------------------------------------------------------

    void setActive(boolean active)
    {
        this.active = active;
    }
    
    //----------------------------------------------------------------------

    protected void setMessage(String message)
    {
        this.message = message;
    }

    //----------------------------------------------------------------------

    void setName(String name)
    {
        this.name = name;
    }
    
    //----------------------------------------------------------------------

    static void optAppendXML(StringBuilder buf, String key, String value)
    {
        if (value != null)
            buf.append("    <").append(key).append(">")
                .append(Strings.replaceString(value, "&", "&amp;"))
                .append("</").append(key).append(">\n");
    }
    
    //----------------------------------------------------------------------
    
    String getPasswd()
    {
        return null;
    }

    //----------------------------------------------------------------------

    public String toString()
    { 
        return name + " " + getSpec() + " active=" + active + " writeable=" + writeable;
    }
    
    //----------------------------------------------------------------------

    abstract public String  getSpec();
    abstract TransportUI    getUI();
    abstract boolean        isWriteable();
    abstract boolean        needsDownloadPasswd();
    abstract boolean        needsUploadPasswd();
    abstract String         readFile(String passwd);
    abstract boolean        writeFile(String xml, String passwd);
    abstract void           appendXML(DOMOut out);
}

