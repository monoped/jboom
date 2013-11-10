package de.monoped.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;

public class EditableHeader
    extends JTableHeader 
    implements CellEditorListener 
{
    public final int HEADER_ROW = -10;

    transient protected int             editingColumn;
    transient protected TableCellEditor cellEditor;
    transient protected Component       editorComp;

    //----------------------------------------------------------------------

    public EditableHeader(TableColumnModel columnModel) 
    {
        super(columnModel);
        setReorderingAllowed(false);
        cellEditor = null;
        recreateTableColumn(columnModel);
    }

    //----------------------------------------------------------------------

    public void updateUI() 
    {
        setUI(new EditableHeaderUI());
        resizeAndRepaint();
        invalidate();
    }

    //----------------------------------------------------------------------

    protected void recreateTableColumn(TableColumnModel columnModel) 
    {
        int                         n = columnModel.getColumnCount();
        EditableHeaderTableColumn[] newCols = new EditableHeaderTableColumn[n];
        TableColumn[]               oldCols = new TableColumn[n];

        for (int i = 0; i < n; i++) 
        {
            oldCols[i] = columnModel.getColumn(i);
            newCols[i] = new EditableHeaderTableColumn();
            newCols[i].copyValues(oldCols[i]);
        }

        for (int i = 0; i < n; i++)
            columnModel.removeColumn(oldCols[i]);
        
        for (int i = 0; i < n; i++)
            columnModel.addColumn(newCols[i]);
    }

    //----------------------------------------------------------------------

    public boolean editCellAt(int index) 
    {
        return editCellAt(index);
    }

    //----------------------------------------------------------------------

    public boolean editCellAt(int index, EventObject e) 
    {
        if (cellEditor != null && ! cellEditor.stopCellEditing()) 
            return false;
    
        if (! isCellEditable(index)) 
            return false;
    
        TableCellEditor editor = getCellEditor(index);

        if (editor != null && editor.isCellEditable(e)) 
        {
            editorComp = prepareEditor(editor, index);
            editorComp.setBounds(getHeaderRect(index));
            add(editorComp);
            editorComp.validate();
            setCellEditor(editor);
            setEditingColumn(index);
            editor.addCellEditorListener(this);
            return true;
        }

        return false;
    }

    //----------------------------------------------------------------------

    public boolean isCellEditable(int index) 
    {
        if (getReorderingAllowed())
            return false;
    
        int                         columnIndex = columnModel.getColumn(index).getModelIndex();
        EditableHeaderTableColumn   col = (EditableHeaderTableColumn) columnModel.getColumn(columnIndex);
        
        return col.isHeaderEditable();
    }

    //----------------------------------------------------------------------

    public TableCellEditor getCellEditor(int index) 
    {
        int                         columnIndex = columnModel.getColumn(index).getModelIndex();
        EditableHeaderTableColumn   col = (EditableHeaderTableColumn) columnModel.getColumn(columnIndex);

        return col.getHeaderEditor();
    }

    //----------------------------------------------------------------------

    public void setCellEditor(TableCellEditor newEditor) 
    {
        TableCellEditor oldEditor = cellEditor;
    
        cellEditor = newEditor;

        // firePropertyChange

        if (oldEditor != null && oldEditor instanceof TableCellEditor) 
            ((TableCellEditor)oldEditor).removeCellEditorListener((CellEditorListener)this);
        
        if (newEditor != null && newEditor instanceof TableCellEditor)
            ((TableCellEditor) newEditor).addCellEditorListener((CellEditorListener)this);
    }

    //----------------------------------------------------------------------

    public Component prepareEditor(TableCellEditor editor, int index) 
    {
        Object      value = columnModel.getColumn(index).getHeaderValue();
        boolean     isSelected = true;
        int         row = HEADER_ROW;
        JTable      table = getTable();
        Component   comp = editor.getTableCellEditorComponent(table, value, isSelected, row, index);

        if (comp instanceof JComponent) 
            ((JComponent) comp).setNextFocusableComponent(this);
        
        return comp;
    }

    //----------------------------------------------------------------------

    public TableCellEditor getCellEditor()     
    {
        return cellEditor;
    }

    //----------------------------------------------------------------------

    public Component getEditorComponent() 
    {
        return editorComp;
    }

    //----------------------------------------------------------------------

    public void setEditingColumn(int aColumn) 
    {
        editingColumn = aColumn;
    }

    //----------------------------------------------------------------------

    public int getEditingColumn() 
    {
        return editingColumn;
    }

    //----------------------------------------------------------------------

    public void removeEditor() 
    {
        TableCellEditor editor = getCellEditor();

        if (editor != null) 
        {
            editor.removeCellEditorListener(this);

            requestFocus();
            remove(editorComp);

            int         index = getEditingColumn();
            Rectangle   cellRect = getHeaderRect(index);

            setCellEditor(null);
            setEditingColumn(-1);
            editorComp = null;

            repaint(cellRect);
        }
    }

    //----------------------------------------------------------------------

    public boolean isEditing() 
    {
        return (cellEditor == null) ? false : true;
    }

    //----------------------------------------------------------------------

    // CellEditorListener
    
    public void editingStopped(ChangeEvent e) 
    {
        TableCellEditor editor = getCellEditor();
    
        if (editor != null) 
        {
            Object  value = editor.getCellEditorValue();
            int     index = getEditingColumn();

            columnModel.getColumn(index).setHeaderValue(value);
            removeEditor();
        }
    }

    //----------------------------------------------------------------------

    public void editingCanceled(ChangeEvent e) 
    {
        removeEditor();
    }

    //======================================================================

//    public class EditableHeaderTableColumn extends TableColumn 
//    {
//        protected TableCellEditor headerEditor;
//        protected boolean isHeaderEditable;
//
//        //----------------------------------------------------------------------
//
//        public EditableHeaderTableColumn() 
//        {
//            setHeaderEditor(createDefaultHeaderEditor());
//            isHeaderEditable = true;
//        }
//
//        //----------------------------------------------------------------------
//
//        public void setHeaderEditor(TableCellEditor headerEditor) 
//        {
//            this.headerEditor = headerEditor;
//        }
//
//        //----------------------------------------------------------------------
//
//        public TableCellEditor getHeaderEditor() 
//        {
//            return headerEditor;
//        }
//
//        //----------------------------------------------------------------------
//
//        public void setHeaderEditable(boolean isEditable) 
//        {
//            isHeaderEditable = isEditable;
//        }
//
//        //----------------------------------------------------------------------
//
//        public boolean isHeaderEditable() 
//        {
//            return isHeaderEditable;
//        }
//
//        //----------------------------------------------------------------------
//
//        public void copyValues(TableColumn base) 
//        {
//            modelIndex = base.getModelIndex();
//            identifier = base.getIdentifier();
//            width = base.getWidth();
//            minWidth = base.getMinWidth();
//            setPreferredWidth(base.getPreferredWidth());
//            maxWidth = base.getMaxWidth();
//            headerRenderer = base.getHeaderRenderer();
//            headerValue = base.getHeaderValue();
//            cellRenderer = base.getCellRenderer();
//            cellEditor = base.getCellEditor();
//            isResizable = base.getResizable();
//        }
//
//        //----------------------------------------------------------------------
//
//        protected TableCellEditor createDefaultHeaderEditor() 
//        {
//            return new DefaultCellEditor(new JTextField());
//        }
//    }

    //======================================================================

    class EditableHeaderUI extends BasicTableHeaderUI 
    {
        protected MouseInputListener createMouseInputListener() 
        {
            return new MouseInputHandler((EditableHeader) header);
        }

        //----------------------------------------------------------------------

        public class MouseInputHandler 
            extends BasicTableHeaderUI.MouseInputHandler 
        {
            private Component           dispatchComponent;
            protected EditableHeader    header;

            //----------------------------------------------------------------------
            public MouseInputHandler(EditableHeader header) 
            {
                this.header = header;
            }

            //----------------------------------------------------------------------
            
            private void setDispatchComponent(MouseEvent e) 
            {
                Component   editorComponent = header.getEditorComponent();
                Point       p = e.getPoint();
                Point       p2 = SwingUtilities.convertPoint(header, p, editorComponent);
      
                dispatchComponent = SwingUtilities.getDeepestComponentAt(editorComponent, p2.x, p2.y);
            }

            //----------------------------------------------------------------------

            private boolean repostEvent(MouseEvent e) 
            {
                if (dispatchComponent == null) 
                    return false;
      
                MouseEvent e2 = SwingUtilities.convertMouseEvent(header, e, dispatchComponent);

                dispatchComponent.dispatchEvent(e2);
                return true;
            }

            //----------------------------------------------------------------------

            public void mousePressed(MouseEvent e) 
            {
                if (! SwingUtilities.isLeftMouseButton(e)) 
                    return;
     
                super.mousePressed(e);

                if (header.getResizingColumn() == null) 
                {
                    Point               p = e.getPoint();
                    TableColumnModel    columnModel = header.getColumnModel();
                    int                 index = columnModel.getColumnIndexAtX(p.x);

                    if (index != -1 && header.editCellAt(index, e)) 
                    {
                        setDispatchComponent(e);
                        repostEvent(e);
                    }
                }
            }

            //----------------------------------------------------------------------

            public void mouseReleased(MouseEvent e) 
            {
                super.mouseReleased(e);

                if (! SwingUtilities.isLeftMouseButton(e)) 
                    return;
      
                repostEvent(e);
                dispatchComponent = null;
            }

        }
    }
}

