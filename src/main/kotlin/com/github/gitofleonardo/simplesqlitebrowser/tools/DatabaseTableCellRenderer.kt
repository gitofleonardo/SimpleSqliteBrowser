package com.github.gitofleonardo.simplesqlitebrowser.tools

import com.github.gitofleonardo.simplesqlitebrowser.data.DbRow
import com.github.gitofleonardo.simplesqlitebrowser.model.SqliteModel
import com.github.gitofleonardo.simplesqlitebrowser.toStringOr
import java.awt.Component
import java.sql.Types
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer

class DatabaseTableCellRenderer : DefaultTableCellRenderer() {
    override fun getTableCellRendererComponent(
        table: JTable,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        val newValue: String = when (value) {
            null -> {
                SqliteModel.NULL
            }
            is DbRow.RowData -> {
                when (value.type) {
                    Types.BLOB -> {
                        if (value.data == null) {
                            SqliteModel.NULL
                        } else {
                            SqliteModel.BLOB
                        }
                    }
                    else -> value.data.toStringOr(SqliteModel.NULL)
                }
            }
            else -> {
                value.toString()
            }
        }
        return super.getTableCellRendererComponent(table, newValue, isSelected, hasFocus, row, column)
    }
}