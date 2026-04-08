package com.github.gitofleonardo.simplesqlitebrowser.tools

import com.github.gitofleonardo.simplesqlitebrowser.data.DbRow
import com.github.gitofleonardo.simplesqlitebrowser.data.DbTableInstance
import javax.swing.table.AbstractTableModel

class DatabaseTableModel(
        private var dbTableData: DbTableInstance,
) : AbstractTableModel() {

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

    fun checkIndexRange(rowIndex: Int, columnIndex: Int): Boolean {
        return rowIndex in IntRange(0, rowCount - 1) && columnIndex in IntRange(0, columnCount - 1)
    }

    fun canReuseWith(newTableData: DbTableInstance): Boolean {
        if (dbTableData.columns.size != newTableData.columns.size) {
            return false
        }
        return dbTableData.columns.zip(newTableData.columns).all { (current, next) ->
            current.name == next.name && current.type == next.type
        }
    }

    fun updateTableData(newTableData: DbTableInstance) {
        dbTableData = newTableData
        fireTableDataChanged()
    }
}