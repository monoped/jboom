package de.monoped.swing;

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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;

/** Start a thread and show a modal "wait" dialog with a cancel button. 
 *  Derived classes must implement T result().
 *  Pressing "cancel" removes the dialog.
 */

abstract public class RunWait<T>
    implements Callable<T>
{
    protected WaitDialog        dialog;
    private static int          DEF_WIDTH = 400, DEF_HEIGHT = 150;
    private int                 dialogWidth, dialogHeight;
    private Component           parent;
    private String              title, cancelText, errorMsg;
    private JComponent          component;
    private FutureTask<T>       task;

    //----------------------------------------------------------------------

    /** Constructor
     *
     *  @param parent       Parent component, use for centering the dialog.
     *  @param component    Component to show in the dialog.
     *  @param title        Dialog title
     *  @param cancelText   Cancel button text.
     */
    
    public RunWait(Component parent, JComponent component, String title, String cancelText)
    {
        this.parent = parent;
        this.component = component;
        this.title = title;
        this.cancelText = cancelText;
        dialogWidth = DEF_WIDTH;
        dialogHeight = DEF_HEIGHT;
    }

    //----------------------------------------------------------------------

    /** Constructor
     *
     *  @param parent       Parent component, use for centering the dialog.
     *  @param text         Label to show in the dialog.
     *  @param title        Dialog title
     *  @param cancelText   Cancel button text.
     */
    
    public RunWait(Component parent, String text, String title, String cancelText)
    {
        this.parent = parent;

        setLabel(text);
        this.title = title;
        this.cancelText = cancelText;
        dialogWidth = DEF_WIDTH;
        dialogHeight = DEF_HEIGHT;
    }

    //----------------------------------------------------------------------

    /** Constructor
     *
     *  @param text         Label to show in the dialog.
     *  @param title        Dialog title
     *  @param cancelText   Cancel button text.
     */
    
    public RunWait(String text, String title, String cancelText)
    {
        this(null, text, title, cancelText);
    }

    //----------------------------------------------------------------------

    /** Constructor
     *
     *  @param text         Label to show in the dialog.
     */
    
    public RunWait(String text)
    {
        this(null, text, "Please wait", "cancel");
    }

    //----------------------------------------------------------------------

    public RunWait()
    {
        this(null);
    }

    //----------------------------------------------------------------------

    /** Cancel task and remove dialog. */

    public void cancelTask()
    {
        task.cancel(true);
    }

    //----------------------------------------------------------------------

    /** Cancel task and remove dialog. */

    public void cancelTask(String err)
    {
        errorMsg = err;
        task.cancel(true);
    }

    //----------------------------------------------------------------------

    public String getErrorMsg()
    {
        return errorMsg;
    }

    //----------------------------------------------------------------------

    public boolean isCancelled()
    {
        return task.isCancelled();
    }

    //----------------------------------------------------------------------

    /** Remove the wait dialog. */
    
    public void removeDialog()
    {
        if (dialog == null)
            return;

        if (SwingUtilities.isEventDispatchThread())
            dialog.setVisible(false);
        else
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    dialog.setVisible(false);
                    dialog.dispose();
                }
            });
    }

    //----------------------------------------------------------------------

    abstract public T result() throws Exception;

    //----------------------------------------------------------------------

    public void setCancelText(String cancelText)
    {
        this.cancelText = cancelText;
    }

    //----------------------------------------------------------------------

    public static void setDefaultSize(int w, int h)
    {
        DEF_WIDTH = w;
        DEF_HEIGHT = h;
    }

    //----------------------------------------------------------------------

    public void setLabel(String text)
    {
        component = new JPanel();
        component.add(new JLabel(text));
    }

    //----------------------------------------------------------------------

    public void setParent(Component parent)
    {
        this.parent = parent;
    }

    //----------------------------------------------------------------------

    public void setTitle(String title)
    {
        this.title = title;
    }

    //----------------------------------------------------------------------

    public void setDialogSize(int dialogWidth, int dialogHeight)
    {
        this.dialogWidth = dialogWidth;
        this.dialogHeight = dialogHeight;
    }

    //----------------------------------------------------------------------

    public void setComponent(JComponent component)
    {
        this.component = component;
    }

    //----------------------------------------------------------------------

    /** Start thread and show dialog. 
     *
     *  @return The result of the task, or null if an exceptin was thrown
     */

    public T start()
    {
        dialog = new WaitDialog(parent, component, title, cancelText);
        dialog.setSize(dialogWidth, dialogHeight);

        ExecutorService executor = Executors.newFixedThreadPool(1);

        task = new FutureTask<T>(this);
        executor.execute(task);
        dialog.setVisible(true);

        try
        {
            return task.get();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    //----------------------------------------------------------------------
    // Callable method
    //----------------------------------------------------------------------

    public T call()
    {
        T   res;

        try
        {
            res = result();     // call user supplied function
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            res = null;
        }

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                removeDialog();
            }
        });

        return res;
    }

    //======================================================================

    /** Modal wait dialog with a cancel button. */

    class WaitDialog
        extends JDialog
        implements ActionListener
    {
        private JButton cancelButton;

        //----------------------------------------------------------------------

        WaitDialog(Component parent, JComponent component, String title, String cancelText)
        {
            setModal(true);
            setBounds(Utilities.centerComponent(parent, dialogWidth, dialogHeight));
            setTitle(title);
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

            JPanel   center = new JPanel();

            center.add(component);
            add(center);
            cancelButton = new JButton(cancelText);
            cancelButton.addActionListener(this);

            JPanel south = new JPanel();

            south.add(cancelButton);
            add(south, BorderLayout.SOUTH);
        }

        //----------------------------------------------------------------------

        /** ActionListener method handling cancel button. */

        public void actionPerformed(ActionEvent e)
        {
            Object src = e.getSource();

            if (src == cancelButton)
            {
                cancelTask();
                removeDialog();
            }
        }
    }
}    

