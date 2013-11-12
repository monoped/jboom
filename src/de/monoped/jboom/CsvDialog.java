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

import de.monoped.swing.UIAction;
import de.monoped.utils.KeyBundle;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.ResourceBundle;

/** Dialog for address import from CSV file */

public class CsvDialog
    extends JDialog
    implements ActionListener
{
    static KeyBundle                bundle = (KeyBundle)ResourceBundle.getBundle("de.monoped.jboom.Resources");

    private JRadioButton            commaButton, semiButton, tabButton;
    private JCheckBox               skipFirstBox;
    private boolean                 skipFirstLine;
    private int                     separator, tableRow;
    private ArrayList<String[]>     recordList;
    private String[][]              records;
    private ArrayList<String>       lines;
    private String[]                valueNames;
    private GridBagConstraints      gbc;
    private JScrollPane             tableScroll;
    private JTable                  table;
    private String                  dontUse = "-";

    //----------------------------------------------------------------------

    public CsvDialog(File file, String[] valueNames)
    {
        super((Frame) null, "CSV", true);
        this.valueNames = valueNames;

        setSize(new Dimension(700, 500));
        lines = new ArrayList<String>();

        StringBuilder sb = new StringBuilder();

        // Read file into "lines" array and also into string 

        try
        {
            BufferedReader  in = new BufferedReader(new FileReader(file));
            String          line;

            while ((line = in.readLine()) != null)
            {
                lines.add(line);
                sb.append(line).append("\n");
            }
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(this, ex.toString(), bundle.getText("error"), JOptionPane.ERROR_MESSAGE);

            if (JBoom.isDebug())
                ex.printStackTrace();

            setVisible(false);
            return;
        }

        if (lines.size() == 0)
        {
            // empty

            JOptionPane.showMessageDialog(this, bundle.getText("emptyFile"), bundle.getText("warn"), JOptionPane.WARNING_MESSAGE);
            setVisible(false);
            return;
        }

        findSeparator();        // try to guess separator
        splitFile();            // split file accordingly

        // set up GUI

        JTextArea linesArea = new JTextArea(sb.toString());
        new JButton(new CancelAction());

        setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(new JLabel(file.getPath()), gbc);
        ++gbc.gridy;

        gbc.weighty = 1;

        JScrollPane scroll = new JScrollPane(linesArea);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        add(scroll, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        ++gbc.gridy; gbc.gridx = 0; gbc.gridwidth = 1;
        add(new JLabel(bundle.getText("separator")), gbc);

        ++gbc.gridx; 
        commaButton = new JRadioButton(" , ");
        commaButton.addActionListener(this);
        semiButton = new JRadioButton(" ; ");
        semiButton.addActionListener(this);
        tabButton = new JRadioButton("TAB");
        tabButton.addActionListener(this);

        ButtonGroup group = new ButtonGroup();

        group.add(commaButton);
        group.add(semiButton);
        group.add(tabButton);

        commaButton.setSelected(separator == ',');
        semiButton.setSelected(separator == ';');
        tabButton.setSelected(separator == '\t');

        add(commaButton, gbc);
        ++gbc.gridx;
        add(semiButton, gbc);
        ++gbc.gridx;
        add(tabButton, gbc);

        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 1;
        skipFirstBox = new JCheckBox(bundle.getText("skip1st"));
        skipFirstBox.setSelected(skipFirstLine);
        skipFirstBox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                skipFirstLine = skipFirstBox.isSelected();
                createData();   
            }
        });

        ++gbc.gridx;
        add(skipFirstBox, gbc);

        ++gbc.gridy; gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(new JLabel(bundle.getText("mailTableExpl")), gbc);

        tableRow = ++gbc.gridy; 

        JPanel okPanel = new JPanel();

        okPanel.add(new JButton(new CancelAction()));
        okPanel.add(new JButton(new OKAction()));

        gbc.fill = GridBagConstraints.NONE;
        ++gbc.gridy; gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(okPanel, gbc);

        createData();
    }

    //----------------------------------------------------------------------

    public void actionPerformed(ActionEvent e)
    {
        if (commaButton.isSelected())
            separator = ',';
        else if (semiButton.isSelected())
            separator = ';';
        else if (tabButton.isSelected())
            separator = '\t';

        splitFile();
        skipFirstBox.setSelected(skipFirstLine);
        createData();
    }

    //----------------------------------------------------------------------

    // create data structure from splitted file

    private void createData()
    {
        int                 nvals = 0;
        
        for (String[] record: recordList)
        {
            int nv = record.length;

            if (nv > nvals)
                nvals = nv;
        }
        
        int[]               indices = new int[nvals];
        int                 istart = skipFirstLine ? 1 : 0,
                            ncols = 0;
        ArrayList<String>   cols = new ArrayList<String>();

        // get indices of non-empty columns

        for (int i = 0; i < nvals; ++i)
        {
            for (int irec = istart; irec < recordList.size(); ++irec)
            {
                String[] record = recordList.get(irec);
            
                if (record.length > i && record[i].length() != 0)
                {
                    indices[ncols++] = i;
                    cols.add(dontUse);
                    break;
                }
            }
        }

        // create records array containing only non-empty columns

        int nrecords = recordList.size() - istart;

        if (nrecords == 0)
            return;

        records = new String[nrecords][ncols];

        // copy result fields from recordList to record array

        for (int irec = istart; irec < recordList.size(); ++irec)
        {
            String[] record = recordList.get(irec);

            for (int icol = 0; icol < ncols; ++icol)
            {
                // if field not present, use null

                int ifrom = indices[icol];

                records[irec - istart][icol] = record.length > ifrom ? record[ifrom] : null;
            }
        }

        // (re-)create table

        if (tableScroll != null)
            remove(tableScroll);

        table = new JTable(records, cols.toArray(new String[0]));

        // header reacts to right-clicks. A popup with unused column names appears

        table.getTableHeader().addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    JTableHeader        header = (JTableHeader)e.getSource();
                    Point               p = e.getPoint();   
                    int                 icol = header.columnAtPoint(p);
                    JPopupMenu          popup = new JPopupMenu();
                    TableColumnModel    model = header.getColumnModel();
                    TableColumn         column = model.getColumn(icol);
                    String              headval = (String)column.getHeaderValue();
                    
                    if (! headval.equals(dontUse))
                        popup.add(new JMenuItem(new HeaderAction(dontUse, column)));

                    for (String name: valueNames)
                    {
                        boolean ok = true;

                        for (int i = 0; i < model.getColumnCount(); ++i)
                        {
                            TableColumn col = model.getColumn(i);
                            String      colname = (String)col.getHeaderValue();

                            if (name.equals(colname))
                            {
                                ok = false;
                                break;
                            }
                        }

                        if (ok)
                            popup.add(new JMenuItem(new HeaderAction(name, column)));
                    }

                    popup.show(header, p.x, p.y);
                }
            }
        });

        tableScroll = new JScrollPane(table);

        gbc.gridy = tableRow; gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 3;
        add(tableScroll, gbc);
        getContentPane().doLayout();
    }

    //----------------------------------------------------------------------

    /** true if max 1 empty field */

    private boolean fullLine(int k)
    {
        String[]    first = recordList.get(k);
        int nempty = 0;

        for (String val: first)
            if (val.length() == 0)
                ++nempty;

        return nempty <= 1;
    }

    //----------------------------------------------------------------------

    /** Simple heuristic to find separator (comma, semicolon, or tab) */

    private void findSeparator()
    {
        String  line = lines.get(0);
        int     nComma = 0, nSemi = 0, nTab = 0;

        for (int i = 0; i < line.length(); ++i)
        {
            char c = line.charAt(i);

            if (c == ',')
                ++nComma;
            else if (c == ';')
                ++nSemi;
            else if (c == '\t')
                ++nTab;
        }

        if (nComma >= nSemi)
            separator = nComma > nTab ? ',' : '\t';
        else 
            separator = nSemi > nTab ? ';'  : '\t';
    }

    //----------------------------------------------------------------------

    public String[][] getValues()
    {
        int                 ncols = valueNames.length,
                            tcols = table.getColumnCount();
        String[][]          values = new String[records.length][ncols];
        int[]               colindex = new int[ncols];
        TableColumnModel    model = table.getColumnModel();
        
        for (int index = 0; index < ncols; ++index)
        {
            colindex[index] = -1;

            for (int i = 0; i < tcols; ++i)
            {
                String colname = (String)model.getColumn(i).getHeaderValue();

                if (valueNames[index].equals(colname))
                {
                    colindex[index] = i;
                    break;
                }
            }
        }

        for (int irec = 0; irec < records.length; ++irec)
        {
            String[]    record = records[irec],
                        fields = new String[ncols];

            for (int index = 0; index < ncols; ++index)
                fields[index] = colindex[index] < 0 ? null : record[colindex[index]];

            values[irec] = fields;
        }

        return values;
    }

    //----------------------------------------------------------------------

    private void splitFile()
    {
        recordList = new ArrayList<String[]>();

        for (String line: lines)
            recordList.add(splitLine(line));

        if (! fullLine(0))
            skipFirstLine = false;
        else
        {
            skipFirstLine = true;
            
            for (int i = 1; i < recordList.size(); ++i)
                if (fullLine(i))
                {
                    skipFirstLine = false;
                    break;
                }
        }
    }

    //----------------------------------------------------------------------

    private String[] splitLine(String line)
    {
        ArrayList<String>   fields = new ArrayList<String>();
        boolean             inquote = false, 
                            wasquote = false;
        StringBuilder       word = new StringBuilder();

        for (int i = 0; i < line.length(); ++i)
        {
            char c = line.charAt(i);

            if (c == '"')
            {
                if (inquote)
                    if (wasquote)
                    {
                        word.append('"');
                        wasquote = false;
                    }
                    else
                        wasquote = true;
                else
                    inquote = true;

                continue;
            }

            // not apostrophe

            if (inquote)
            {    
                if (wasquote)
                    inquote = wasquote = false;
                else
                {
                    wasquote = false;
                    word.append(c);
                    continue;
                }
            }
            
            if (c == separator)
            {
                fields.add(word.toString());
                word = new StringBuilder();
                inquote = wasquote = false;
            }
            else
                word.append(c);
        }

        fields.add(word.toString());
        
        return fields.toArray(new String[0]);
    }

    //======================================================================

    class CancelAction
        extends UIAction
    {
        CancelAction()
        {
            super(bundle, "cancel");
        }

        //----------------------------------------------------------------------

        public void actionPerformed(ActionEvent e)
        {
            lines = null;
            setVisible(false);
        }
    }

    //======================================================================

    class HeaderAction
        extends AbstractAction
    {
        private String      name;
        private TableColumn column;

        //----------------------------------------------------------------------

        HeaderAction(String name, TableColumn column)
        {
            putValue(Action.NAME, name);
            this.name = name;
            this.column = column;
        }

        //----------------------------------------------------------------------

        public void actionPerformed(ActionEvent e)
        {
            column.setHeaderValue(name);   
            table.getTableHeader().repaint();
        }

    }

    //======================================================================

    class OKAction
        extends UIAction
    {
        OKAction()
        {
            super(bundle, "ok");
        }

        //----------------------------------------------------------------------

        public void actionPerformed(ActionEvent e)
        {
            setVisible(false);
        }
    }


}

