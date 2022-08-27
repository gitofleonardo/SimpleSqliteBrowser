package com.github.gitofleonardo.simplesqlitebrowser.tools

import com.github.gitofleonardo.simplesqlitebrowser.data.DbRow
import com.github.gitofleonardo.simplesqlitebrowser.data.DbTableInstance
import java.sql.Types
import javax.swing.JTable
import javax.swing.event.TableModelListener
import javax.swing.table.TableModel

class DatabaseTableModel(
    private val dbTableData: DbTableInstance,
) : TableModel {
    private val modelListeners: ArrayList<TableModelListener?> = ArrayList()

    override fun getRowCount(): Int = dbTableData.rows.size

    override fun getColumnCount(): Int = dbTableData.columns.size

    override fun getColumnName(columnIndex: Int): String {
        return dbTableData.columns[columnIndex].name
    }

    override fun getColumnClass(columnIndex: Int): Class<*> = DbRow.RowData::class.java

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = false

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        return dbTableData.rows[rowIndex].rowData[columnIndex]
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        // TODO Modify database
    }

    override fun addTableModelListener(l: TableModelListener?) {
        modelListeners.add(l)
    }

    override fun removeTableModelListener(l: TableModelListener?) {
        modelListeners.remove(l)
    }

    fun checkIndexRange(rowIndex: Int, columnIndex: Int): Boolean {
        return rowIndex in IntRange(0, rowCount - 1) && columnIndex in IntRange(0, columnCount - 1)
    }
}