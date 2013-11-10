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

import de.monoped.swing.RunWait;
import de.monoped.utils.DOMOut;
import de.monoped.utils.DOMUtils;
import de.monoped.utils.KeyBundle;
import org.w3c.dom.Element;
import com.trilead.ssh2.*;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

/** SSH connection. */

class ScpTransport
    extends Transport
{
    static final int    PASSWD = 0, PUBKEY = 1;
    static KeyBundle    bundle = (KeyBundle)ResourceBundle.getBundle("de.monoped.jboom.Resources");

    private int         meth;
    private String      host, user, passwd, keyfile, file;
    private Connection  conn;
    
    //----------------------------------------------------------------------

    ScpTransport(String name, String user, String host, int meth, String passwd, String keyfile, String file, 
                    boolean active)
    {
        super(name, active);
        this.meth = meth;
        this.passwd = passwd;
        this.keyfile = keyfile;  
        this.host = host;
        this.user = user;
        this.file = file;
    }

    //----------------------------------------------------------------------

    ScpTransport(String name)
    {
        this(name, null, null, PASSWD, null, null, null, false);
    }

    //----------------------------------------------------------------------

    private boolean authenticate(String passwd)
    {
        boolean ok = false;

        conn = new Connection(host);
    
        try
        {
            conn.connect();
            
            if (meth == PASSWD)
                ok = conn.authenticateWithPassword(user, passwd);
            else    
                ok = conn.authenticateWithPublicKey(user, new File(keyfile), null);
        }
        catch (IOException ex)
        {
            setMessage(ex.toString());
            return false;
        }
        
        if (! ok)
            setMessage(bundle.getText("authfail"));

        return ok;
    }
    
    //----------------------------------------------------------------------

    static ScpTransport createTransport(Element el)
    {
        String m = DOMUtils.getExtAttribute(el, "meth");
        
        return new ScpTransport(DOMUtils.getExtAttribute(el, Tags.NAME),
                                DOMUtils.getExtAttribute(el, Tags.USER),
                                DOMUtils.getExtAttribute(el, "host"),
                                "pubkey".equals(m) ? PUBKEY : PASSWD,
                                DOMUtils.getExtAttribute(el, Tags.PASSWD),
                                DOMUtils.getExtAttribute(el, Tags.KEYFILE),
                                DOMUtils.getExtAttribute(el, Tags.FILE),
                                "true".equals(DOMUtils.getExtAttribute(el, Tags.ACTIVE)));
    }

    //----------------------------------------------------------------------

    int getMethod()
    {
        return meth;
    }

    //----------------------------------------------------------------------

    public String getSpec()
    {
        return "ssh://" + user + "@" + host;
    }

    //----------------------------------------------------------------------

    boolean isWriteable()
    {
        return true;
    }
    
    //----------------------------------------------------------------------

    boolean needsDownloadPasswd()
    {
        return meth == PASSWD;
    }
    
    //----------------------------------------------------------------------

    boolean needsUploadPasswd()
    {
        return meth == PASSWD;
    }
    
    //----------------------------------------------------------------------

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

    void setKeyfile(String keyfile)
    {
        this.keyfile = keyfile;
    }

    //----------------------------------------------------------------------

    void setMethod(int meth)
    {
        this.meth = meth;
    }

    //----------------------------------------------------------------------

    void setUser(String user)
    {
        this.user = user;
    }

    //----------------------------------------------------------------------

    void setPasswd(String passwd)
    {
        this.passwd = passwd;
    }
    
    //----------------------------------------------------------------------

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
            setMessage(e.getMessage());
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
            if (! authenticate(passwd))
                return null;

            try
            {
                SCPClient               scp = new SCPClient(conn);
                ByteArrayOutputStream   out = new ByteArrayOutputStream();

                scp.get(file, out);
                return out.toString("UTF-8");
            }
            finally
            {
                conn.close();
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
            if (! authenticate(passwd))
                return null;

            try
            {
                SCPClient   scp = new SCPClient(conn);

                File        remfile = new File(file);
                String      remdir = remfile.getParent(),
                            remname = remfile.getName();
                
                if (remdir == null)
                    remdir = "~";
                else if (! remdir.startsWith("/"))
                    remdir = "~/" + remdir;

                scp.put(text.getBytes("UTF-8"), remname, remdir, "0600");
            }
            finally
            {
                conn.close();
            }

            return "ok";
        }
    }
    
    //----------------------------------------------------------------------

    void appendXML(DOMOut out)
    {
        out.openElement(Tags.CONN)
            .addAttribute(Tags.NAME, getName())
            .addAttribute(Tags.MODE, "ssh")
            .addAttribute(Tags.ACTIVE, String.valueOf(isActive()))
            .addAttribute(Tags.METH, meth == PASSWD ? "passwd" : "pubkey")
            .endElement()
            .textElement(Tags.HOST, host)
            .textElement(Tags.USER, user);

        if (meth == PASSWD && passwd != null)
            out.textElement(Tags.PASSWD, passwd);
        else
            out.textElement(Tags.KEYFILE, keyfile);

        out.textElement(Tags.FILE, file)
            .closeElement();
    }

    //----------------------------------------------------------------------

    public void dump()
    {
        System.out.println("Scp name=" + getName() + " user=" + user + " host=" + host + " passwd=" + passwd
                        + " file=" + file);
    }

    //----------------------------------------------------------------------

    String getFile()
    {
        return file;
    }
    
    //----------------------------------------------------------------------

    String getHost()
    {
        return host;
    }

    //----------------------------------------------------------------------

    String getKeyfile()
    {
        return keyfile;
    }
    
    //----------------------------------------------------------------------

    String getUser()
    {
        return user;
    }

    //----------------------------------------------------------------------

    String getPasswd()
    {
        return passwd;
    }


    //----------------------------------------------------------------------

    TransportUI getUI()
    {
        return new ScpTransportUI(this);
    }
}

