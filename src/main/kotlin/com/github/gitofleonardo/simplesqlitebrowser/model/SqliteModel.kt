package com.github.gitofleonardo.simplesqlitebrowser.model

import com.github.gitofleonardo.simplesqlitebrowser.data.*
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.ResultSet
import java.sql.Types

object SqliteModel {
    const val NULL = "null"
    const val BLOB = "BLOB"

    suspend fun loadMetaData(file: VirtualFile) : SqliteMetadata = withContext(context = Dispatchers.IO) {
        val connection = ConnectionManager.createConnection(file)
        val metadata = SqliteMetadata()
        connection?.let {
            val md = it.metaData
            metadata.isValidSqliteDatabase = true
            metadata.version = md.databaseMajorVersion
            metadata.driverVersion = md.driverVersion

            val tables = ArrayList<DbTable>()
            val tableResult = md.getTables(null, null, "%", null)
            while (tableResult.next()) {
                val tb = DbTable()
                tb.tableName = tableResult.getString("TABLE_NAME")
                val columnResult = md.getColumns(null, null, tb.tableName, null)
                while (columnResult.next()) {
                    val columnName = columnResult.getString("COLUMN_NAME")
                    val type = columnResult.getInt("DATA_TYPE")
                    val typeName = columnResult.getString("TYPE_NAME")
                    val schema = getAllSchema(columnResult)
                    tb.columns.add(DbColumn(columnName, type, typeName, schema))
                }
                tables.add(tb)
            }
            metadata.tables.addAll(tables)
        }
        ConnectionManager.disposeConnection(connection)
        metadata
    }

    suspend fun loadTables(file: VirtualFile) : List<String> = withContext(context = Dispatchers.IO) {
        val connection = ConnectionManager.createConnection(file)
        val result = mutableListOf<String>()
        connection?.let {
            val resultSet = it.metaData.getTables(null, null, "%", null)
            while (resultSet.next()) {
                val table = resultSet.getString("TABLE_NAME")
                result.add(table)
            }
        }
        ConnectionManager.disposeConnection(connection)
        result
    }

    suspend fun loadTableData(file: VirtualFile, tableName: String, pageCount: Int, page: Int) : DbTableInstance = withContext(context = Dispatchers.IO) {
        val columns = mutableListOf<DbColumn>()
        val rows = mutableListOf<DbRow>()
        var totalCount = 0
        val connection = ConnectionManager.createConnection(file)
        connection?.let {
            val columnResult = it.metaData.getColumns(null, null, tableName, null)
            while (columnResult.next()) {
                val columnName = columnResult.getString("COLUMN_NAME")
                val type = columnResult.getInt("DATA_TYPE")
                val typeName = columnResult.getString("TYPE_NAME")
                val schema = getAllSchema(columnResult)
                columns.add(DbColumn(columnName, type, typeName, schema))
            }

            val statement = it.createStatement()
            val rowResult = statement.executeQuery("SELECT * FROM \"$tableName\" LIMIT $pageCount OFFSET ${pageCount * (page - 1)}")
            val rowMeta = rowResult.metaData
            while (rowResult.next()) {
                val dbRows = mutableListOf<DbRow.RowData>()
                val dbRow = DbRow(dbRows)
                for (columnIndex in columns.indices) {
                    val type = rowMeta.getColumnType(columnIndex + 1)
                    val typeName = rowMeta.getColumnTypeName(columnIndex + 1)
                    val rowData = when (type) {
                        Types.BLOB -> {
                            DbRow.RowData(type, typeName, rowResult.getBytes(columnIndex + 1))
                        }
                        else -> {
                            DbRow.RowData(type, typeName, rowResult.getObject(columnIndex + 1))
                        }
                    }
                    dbRows.add(rowData)
                }
                rows.add(dbRow)
            }

            val countResult = statement.executeQuery("SELECT COUNT(*) FROM \"$tableName\"")
            countResult.next()
            totalCount = countResult.getInt(1)
        }
        ConnectionManager.disposeConnection(connection)
        DbTableInstance(columns, rows, rows.size, page, totalCount)
    }

    private fun getAllSchema(resultSet: ResultSet): String {
        val nullable = resultSet.getBoolean("NULLABLE")
        val nullableString = if (nullable) "" else "NOT NULL"
        var def = resultSet.getString("COLUMN_DEF")
        def = if (def == null || def.isEmpty()) {
            ""
        } else {
            "DEFAULT $def"
        }
        val autoIncrement = resultSet.getBoolean("IS_AUTOINCREMENT")
        val autoIncString = if (autoIncrement) "AUTO INCREMENT" else ""
        return "$nullableString $def $autoIncString"
    }
}
