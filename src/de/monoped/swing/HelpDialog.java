package de.monoped.swing;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/** Help dialog containing the help text, and buttons for moving
 *  forward an backward in the list of already visited pages.
 *  There is only one dialog for the running program.
 */

public class HelpDialog
    extends JDialog
{
    private ArrayList<String>       pageList;
    private boolean                 externalLinkTip, internalLinkTip;
    private HashMap<String, String> pageMap;
    private int                     index, lastIndex;
    private JButton                 prevButton, nextButton;
    private JEditorPane             htmlPane;
    private ClassLoader             loader = getClass().getClassLoader();
 
    private static HelpDialog       helpDialog;

    //----------------------------------------------------------------------

    private HelpDialog(JFrame parent, String title)
    {
        super(parent);
        setTitle(title);

        setModalityType(Dialog.ModalityType.MODELESS);
        setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);

        setSize(new Dimension(600, 600));
        pageMap = new HashMap<String, String>();
        pageList = new ArrayList<String>();
        index = lastIndex = -1;

        htmlPane = new JEditorPane();
        htmlPane.setEditorKit(new HTMLEditorKit());
        htmlPane.setEditable(false);
        htmlPane.addHyperlinkListener(new HyperListener());
        htmlPane.setAutoscrolls(true);

        Action nextAction = new NextAction();
        nextButton = new JButton(nextAction);
        Action okAction = new OKAction();
        Action prevAction = new PrevAction();
        prevButton = new JButton(prevAction);

        Box footPanel = new Box(BoxLayout.X_AXIS);

        footPanel.add(Box.createHorizontalGlue());
        footPanel.add(prevButton);
        footPanel.add(Box.createHorizontalStrut(20));
        footPanel.add(new JButton(okAction));
        footPanel.add(Box.createHorizontalStrut(20));
        footPanel.add(nextButton);
        footPanel.add(Box.createHorizontalGlue());
        add(new JScrollPane(htmlPane));
        add(footPanel, BorderLayout.SOUTH);

        ActionMap   actionMap = new ActionMap();
        InputMap    inputMap = new ComponentInputMap(getRootPane());

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "quit");
        actionMap.put("quit", okAction);
        getRootPane().setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
        getRootPane().setActionMap(actionMap);
    }

    //----------------------------------------------------------------------

    /** Create the dialog
     *
     */

    public static HelpDialog createHelpDialog()
    {
        return new HelpDialog(null, "help");
    }

    //----------------------------------------------------------------------

    /** Return the help dialog */

    static public HelpDialog getHelpDialog()
    {
        return helpDialog;
    }

    //----------------------------------------------------------------------

    public boolean isExternalLinkTip()
    {
        return externalLinkTip;
    }

    //----------------------------------------------------------------------

    public boolean isInternalLinkTip()
    {
        return internalLinkTip;
    }

    //----------------------------------------------------------------------

    private String readHelpText(String resourceName)
        throws IOException
    {
        Reader          textReader = new InputStreamReader(loader.getResourceAsStream(resourceName), "utf-8");
        StringBuilder   builder = new StringBuilder("<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");

        // Read help file content

        BufferedReader  reader = new BufferedReader(textReader);
        String          line;
    
        // <img src=...> URLs pointing to files reachable via class path are of
        // the form reso:filename. 'reso' must be replaced by the actual URL,
        // which can be an absolute path or a path pointing into a jar file.

        while ((line = reader.readLine()) != null)
            if (! line.startsWith("#"))
            {
                int             from = 0, index, indexAlt = 0;

                // Search line for src="reso: 

                while ((index = line.indexOf("src=\"reso:", from)) >= 0)
                {
                    // Change reso:... to resource URL

                    builder.append(line.substring(indexAlt, index + 5));

                    int apo = line.indexOf('"', index + 10);

                    if (apo < 0)
                        break;

                    String  urlString = line.substring(index + 10, apo);
                    URL     url = loader.getResource(urlString);

                    if (url != null) {
                        builder.append(url.toString()).append('"');
                    }
                    from = index + 11;
                    indexAlt = apo + 1;
                }

                builder.append(line.substring(indexAlt)).append("\n");
            }

        builder.append("</html>\n");
        return builder.toString();
    }

    //----------------------------------------------------------------------

    public void setExternalLinkTip(boolean externalLinkTip)
    {
        this.externalLinkTip = externalLinkTip;
    }

    //----------------------------------------------------------------------

    public void setInternalLinkTip(boolean internalLinkTip)
    {
        this.internalLinkTip = internalLinkTip;
    }

    //----------------------------------------------------------------------

    private void setText(String name, int where)
    {
        if (where > 0)                          // forward
            name = pageList.get(++index);
        else if (where < 0)                     // back
            name = pageList.get(--index);
        else
        {
            // normal link 
            // already in history list?

            boolean found = false;

            for (int i = 0; i <= lastIndex; ++i)
                if (pageList.get(i).equals(name))
                {
                    found = true;
                    index = i;
                    break;
                }

            if (! found)
            {
                if (++index < pageList.size())
                    pageList.set(index, name);
                else
                    pageList.add(name);

                lastIndex = index;
            }
        }

        String          pageText = pageMap.get(name);

        try
        {
            // text already read?

            if (pageText == null)
            {
                // no, read from file

                pageText = readHelpText(name);
            }
        }
        catch (Exception ioe)
        {
            ioe.printStackTrace();

            // show stack trace in window

            ByteArrayOutputStream   bout = new ByteArrayOutputStream();
            PrintWriter             out = new PrintWriter(bout);

            ioe.printStackTrace(out);
            out.close();
            pageText += "<h3>Fehler beim Lesen des Hilfetextes '" + name + "'</h3><pre>" + bout + "'</pre>";
        }

        if (pageText == null)
            pageMap.put(name, pageText);        // put text into map

        htmlPane.setText(pageText);             // set html content
        htmlPane.setCaretPosition(0);

        prevButton.setVisible(index > 0);
        nextButton.setVisible(index < lastIndex);
    }

    //----------------------------------------------------------------------

    public void setText(int where)
    {
        setText(null, where);
    }

    //----------------------------------------------------------------------

    public void setText(String name)
    {
        setText(name, 0);
    }

    //======================================================================

    class HyperListener 
        implements HyperlinkListener
    {
        public void hyperlinkUpdate(HyperlinkEvent e)
        {
            try
            {   
                Element         srcEl = e.getSourceElement();
                AttributeSet    attributes = srcEl.getAttributes(),
                                attrA = (AttributeSet)attributes.getAttribute(HTML.Tag.A);
                String          name = (String)attrA.getAttribute(HTML.Attribute.HREF);

                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                {
                    if (name.startsWith("http://"))
                    {
                        // external link 

                        if (Desktop.isDesktopSupported())
                        {       
                            Desktop desktop = Desktop.getDesktop();

                            if (desktop.isSupported(Desktop.Action.BROWSE))
                            {
                                try
                                {
                                    desktop.browse(new URI(name));
                                }
                                catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                    else
                        setText(name);
                }
                else if (e.getEventType() == HyperlinkEvent.EventType.ENTERED)
                {
                    // Show link in tooltip

                    if (name.startsWith("http://"))
                    {
                        if (externalLinkTip)
                            htmlPane.setToolTipText(name);   
                    }
                    else if (internalLinkTip)
                        htmlPane.setToolTipText(name);   
                }
                else 
                    htmlPane.setToolTipText(null);   

            }
            catch (Exception ioe)
            {
                ioe.printStackTrace();
            }
        }
    }

    //======================================================================

    class NextAction
        extends AbstractAction
    {
        NextAction()
        {
            super("Vor");
            putValue(SMALL_ICON, new ImageIcon(loader.getResource("img/next.png")));
            setVisible(false);
        }

        //----------------------------------------------------------------------

        public void actionPerformed(ActionEvent e)
        {
            setText(1);
        }
    }

    //======================================================================

    class OKAction
        extends AbstractAction
    {
        OKAction()
        {
            super("OK");
            putValue(SMALL_ICON, new ImageIcon(loader.getResource("img/ok.png")));
        }

        //----------------------------------------------------------------------

        public void actionPerformed(ActionEvent e)
        {
            setVisible(false);   
        }
    }

    //======================================================================

    class PrevAction
        extends AbstractAction
    {
        PrevAction()
        {
            super("Zurück");
            putValue(SMALL_ICON, new ImageIcon(loader.getResource("img/prev.png")));
            setVisible(false);
        }

        //----------------------------------------------------------------------

        public void actionPerformed(ActionEvent e)
        {
            setText(-1);
        }

    }

}

