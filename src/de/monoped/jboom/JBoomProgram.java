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

import de.monoped.utils.Getopt;
import de.monoped.utils.KeyBundle;
import de.monoped.utils.Strings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URI;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/** JBoom program class. */

public class JBoomProgram
    extends JFrame
    implements ActionListener, URLHandler
{
    static KeyBundle    bundle = (KeyBundle)ResourceBundle.getBundle("de.monoped.jboom.Resources");
    private static final boolean    WITH_TRAY = true;

    private JBoom       jboom;
    private TrayIcon    trayIcon;
    private Image       trayImage;
    private Preferences prefs;

    //----------------------------------------------------------------------

    /** Constructor.
     *
     *  @param configFile    Path or URL of config file. 
     */
    
    JBoomProgram(String configFile, File privateKey)
    {
        setTitle("jBoom " + Version.VERSION);
        trayImage = Icons.JBOOM.getImage();
        setIconImage(trayImage);
        jboom = new JBoom(configFile, this, true, privateKey);
        setContentPane(jboom);
        addWindowListener(new JBoomWindowAdapter());
        jboom.downloadActive();

        if (SystemTray.isSupported())
        {
            boolean wantTray = jboom.getModel().isToTray();

            if (wantTray)
                try
                {
                    SystemTray tray = SystemTray.getSystemTray();
                               
                    tray.add(getTrayIcon());
                }
                catch (AWTException ex)
                {
                    if (JBoom.isDebug())
                        ex.printStackTrace();

                    return;
                }
        }
        else if (JBoom.isDebug())
            System.out.println("tray not supported");

        jboom.addActionListener(this);

        int x = 0, y = 0, w = 315, h = 420;

        if (jboom.getModel().isRememberPosition())
        {
            prefs = Preferences.userRoot().node("jboom");

            x = prefs.getInt("x", 0);
            y = prefs.getInt("y", 0);
            w = prefs.getInt("width", 315);
            h = prefs.getInt("height", 420);
        }

        setBounds(x, y, w, h);

        if (jboom.getModel().isStartMinimized())
            setExtendedState(Frame.ICONIFIED);
    }

    //----------------------------------------------------------------------

    private void finish()
    {
        jboom.finish();

        if (jboom.getModel().isRememberPosition())
        {
            Point p = getLocationOnScreen();

            prefs = Preferences.userRoot().node("jboom");

            prefs.put("x", String.valueOf(p.x));
            prefs.put("y", String.valueOf(p.y));
            prefs.put("width", String.valueOf(getWidth()));
            prefs.put("height", String.valueOf(getHeight()));
        }

        System.exit(0);
    }

    //----------------------------------------------------------------------

    /** Return tray icon. If null, create it first. */

    private TrayIcon getTrayIcon()
    {
        if (trayIcon == null)
        {
            trayIcon = new TrayIcon(trayImage, Utils.html2text(jboom.getRootNode().getName()), 
                    new JBoomPopupMenu(JBoomProgram.this));
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    if (e.getButton() == MouseEvent.BUTTON1)
                        setExtendedState(Frame.NORMAL);
                }
            });
        }

        return trayIcon;
    }

    //----------------------------------------------------------------------

    JBoom getJboom()
    {
        return jboom;
    }

    //----------------------------------------------------------------------

    public void handleURL(String url)
    {
        String cmd = jboom.getModel().getURLCommand(),
               errmsg = null;

        if (cmd != null)
        {
            // Ecplicit command given

            cmd = Strings.replaceString(cmd, "${URL}", url);
            
            try
            {
                Runtime.getRuntime().exec(cmd); 
                return;
            }
            catch (Exception ex)
            {
                if (JBoom.isDebug())
                    ex.printStackTrace();

                errmsg = ex.toString();
            }
        }

        // No command or failed, try Desktop

        if (Desktop.isDesktopSupported())
        {
            Desktop desktop = Desktop.getDesktop();

            if (desktop.isSupported(Desktop.Action.BROWSE))
            {
                try
                {
                    desktop.browse(new URI(url));
                    return;
                }
                catch (Exception ex)
                {
                    if (JBoom.isDebug())
                        ex.printStackTrace();

                    errmsg = ex.toString();
                }
            }
        }

        // Fallback

        try
        {
            Runtime.getRuntime().exec(JBoom.DEFAULT_BROWSER_CMD + url);
            return;
        }
        catch (Exception ex)
        {
            if (JBoom.isDebug())
                ex.printStackTrace();

            errmsg = ex.toString();
        }

        JOptionPane.showMessageDialog(this, errmsg, bundle.getText("error"), JOptionPane.ERROR_MESSAGE);
    }

    //----------------------------------------------------------------------
    
    static public void main(String[] args)
        throws Exception
    {
        String      configFile = null;
        Getopt      getopts = new Getopt(args, "c:dp:");
        int         opt;
        boolean     debug = false,
                    iconified = false;
        File        privateKey = null;

        // Handle options
        
        while ((opt = getopts.getOption()) >= 0)
            switch (opt)
            {
                case 'c':   configFile = getopts.getOptarg(); 
                            break;
                case 'd':   debug = true;
                            break;
                case 'p':   privateKey = new File(getopts.getOptarg());
                            break;
                case '?':   JOptionPane.showMessageDialog(null, bundle.getText("opterr", String.valueOf(getopts.getOptopt())),
                                    bundle.getText("warn"), JOptionPane.ERROR_MESSAGE);
            }

        JBoom.setDebug(debug);
            
        JBoomProgram prog = new JBoomProgram(configFile, privateKey);

        prog.setVisible(true);
        prog.jboom.getTree().requestFocusInWindow();
    }

    //----------------------------------------------------------------------

    void removeTray()
    {
        if (trayIcon != null)
        {
            SystemTray.getSystemTray().remove(trayIcon);

            if ((getExtendedState() & Frame.ICONIFIED) != 0)
                setExtendedState(Frame.NORMAL);
        }
    }

    //----------------------------------------------------------------------
    // ActionListener
    //----------------------------------------------------------------------

    public void actionPerformed(ActionEvent e)
    {
        if (trayIcon != null)
        {
            trayIcon.setPopupMenu(new JBoomPopupMenu(JBoomProgram.this));
            trayIcon.setToolTip(Utils.html2text(jboom.getRootNode().getName()));
        }
    }

    //======================================================================
    // JBoomWindowAdapter class
    //======================================================================

    private class JBoomWindowAdapter
        extends WindowAdapter
    {
        public void windowClosing(WindowEvent event)
        {
            finish();
        }

        //----------------------------------------------------------------------

        public void windowIconified(WindowEvent event)
        {
            if (jboom.getModel().isToTray() && SystemTray.getSystemTray().getTrayIcons().length == 0)
            {
                try
                {
                    SystemTray.getSystemTray().add(getTrayIcon());
                }
                catch (AWTException ex)
                {
                    if (JBoom.isDebug())
                        ex.printStackTrace();

                    return;
                }
            }
        }
    }
}
