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
import com.enterprisedt.net.ftp.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import org.w3c.dom.*;

/** Ftp connection. */

class FtpTransport
    extends Transport
{
    static KeyBundle    bundle = (KeyBundle)ResourceBundle.getBundle("de.monoped.jboom.Resources");

    private String      host, user, passwd, file;

    //----------------------------------------------------------------------

    /** Construct a connection object.
     *
     *  @param name     Connection name.
     *  @param user     User name.
     *  @param host     Hostname.
     *  @param passwd   Password. If null, user will be prompted.
     *  @param file     Remote file.
     *  @param active   Connections is active.
     */

    FtpTransport(String name, String user, String host, String passwd, String file, boolean active)
    {
        super(name, active);
        this.host = host;
        this.user = user;
        this.file = file;
        this.passwd = passwd;
    }

    //----------------------------------------------------------------------

    /** Construct an empty connection object.
     *
     *  @param name     Connection name.
     */

    FtpTransport(String name)
    {
        this(name, null, null, null, null, false);
    }
    
    //----------------------------------------------------------------------

    /** Append XML representation of this connection to a Buffer. 
     *
     *  @param  buf     The string buffer to append to.
     */
    
    void appendXML(DOMOut out)
    {
        out.openElement(Tags.CONN)
            .addAttribute(Tags.NAME, getName())
            .addAttribute(Tags.MODE, "ftp")
            .addAttribute(Tags.ACTIVE, String.valueOf(isActive()))
            .endElement()
            .textElement(Tags.HOST, host)
            .textElement(Tags.USER, user);

        if (passwd != null)
            out.textElement(Tags.PASSWD, passwd);

        out.textElement(Tags.FILE, file)
            .closeElement();
    }

    //----------------------------------------------------------------------

    /** Create an FtpTransport object from a DOM element,
     *  using attributes "name", "user", "host", "passwd", "file", "active".
     *
     *  @param  el  The DOM Element.
     *  @return     The correspondig FtpTransport object.
     */

    static FtpTransport createTransport(Element el)
    {
        return new FtpTransport(DOMUtils.getExtAttribute(el, Tags.NAME),
                                DOMUtils.getExtAttribute(el, Tags.USER),
                                DOMUtils.getExtAttribute(el, Tags.HOST),
                                DOMUtils.getExtAttribute(el, Tags.PASSWD),
                                DOMUtils.getExtAttribute(el, Tags.FILE),
                                "true".equals(DOMUtils.getExtAttribute(el, Tags.ACTIVE)));
    }

    //----------------------------------------------------------------------

    public void dump()
    {
        System.out.println("Ftp name=" + getName() + " user=" + user + " host=" + host + " passwd=" + passwd
                        + " file=" + file + " active=" + isActive());
    }
    
    //----------------------------------------------------------------------

    String getHost()
    {
        return host;
    }

    //----------------------------------------------------------------------

    String getPasswd()
    {
        return passwd;
    }

    //----------------------------------------------------------------------

    String getFile()
    {
        return file;
    }

    //----------------------------------------------------------------------

    public String getSpec()
    {
        return "ftp://" + user + "@" + host;
    }

    //----------------------------------------------------------------------

    String getUser()
    {
        return user;
    }

    //----------------------------------------------------------------------

    /** Return a UI for this connection. */

    TransportUI getUI()
    {
        return new FtpTransportUI(this);
    }

    //----------------------------------------------------------------------

    boolean isWriteable()
    {
        return true;
    }
    
    //----------------------------------------------------------------------

    boolean needsDownloadPasswd()
    {
        return true;
    }
    
    //----------------------------------------------------------------------

    boolean needsUploadPasswd()
    {
        return true;
    }
    
    //----------------------------------------------------------------------

    /** Read bookmark file from remote host using Downloader. 
     *
     *  @param passwd   The password.
     */

    public String readFile(String passwd)
    {
        Downloader downloader = new Downloader(passwd);

        try
        {
            String result = downloader.start();

            if (downloader.isCancelled())
                setMessage(CANCELLED);
                
            return result;
        }
        catch (Exception e)
        {
            setMessage(e.getMessage());
            return null;
        }
    }

    //----------------------------------------------------------------------

    void setFile(String file)
    {
        this.file = file;
    }
    
    //----------------------------------------------------------------------

    void setHost(String host)
    {
        this.host  = host;
    }

    //----------------------------------------------------------------------

    void setPasswd(String passwd)
    {
        this.passwd = passwd;
    }
    
    //----------------------------------------------------------------------

    void setUser(String user)
    {
        this.user = user;
    }

    //----------------------------------------------------------------------

    /** Send bookmark file to the remote host using Uploader. */

    public boolean writeFile(String s, String passwd)
    {
        Uploader    uploader = new Uploader(s, passwd);
        boolean     ok = false;

        try
        {
            ok = uploader.start() != null;
        }
        catch (Exception e)
        {
            setMessage(e.toString());
        }
        
        if (uploader.isCancelled())
            setMessage(CANCELLED);

        return ok;
    }

    //----------------------------------------------------------------------
    // Downloader
    //----------------------------------------------------------------------

    /** Blocks input and downloads bookmarks from server. */

    class Downloader
        extends RunWait<String>
    {
        private String passwd;

        //----------------------------------------------------------------------

        Downloader(String passwd)
        {
            super(null, bundle.getText("downloading", getName()),
                bundle.getText("wait"), bundle.getText("cancel"));
            this.passwd = passwd;
        }

        //----------------------------------------------------------------------

        public String result()
            throws Exception
        {
            FTPClient ftp = new FTPClient();

            try
            {
                ftp.setRemoteHost(host);
                ftp.connect();
                ftp.login(user, passwd);
                ftp.setType(FTPTransferType.BINARY);
                return new String(ftp.get(file), "UTF-8");
            }
            finally
            {
                ftp.quit();
            }
        }
    }
    
    //----------------------------------------------------------------------
    // Uploader
    //----------------------------------------------------------------------

    /** Blocks input and uploads bookmarks to the remote host. */
    
    class Uploader
        extends RunWait<Object>
    {
        private String text, passwd;

        //----------------------------------------------------------------------

        /** Constructs an Uploader
         *
         *  @param  text    The XML source.
         *  @param  passwd  Password for FTP authentication.
         */

        Uploader(String text, String passwd)
        {
            super(null, bundle.getText("uploading", getName()), 
                    bundle.getText("wait"), bundle.getText("cancel"));
            this.text = text;
            this.passwd = passwd;
        }

        //----------------------------------------------------------------------

        public Object result()
            throws Exception
        {
            FTPClient ftp = new FTPClient();

            try
            {
                ftp.setRemoteHost(host);
                ftp.connect();
                ftp.login(user, passwd);
                ftp.setType(FTPTransferType.BINARY);
                ftp.put(text.getBytes("UTF-8"), file);
            }
            finally
            {
                try
                {
                    ftp.quit();
                }
                catch (Exception cle)
                { }
            }

            return "ok";
        }
    }
}

