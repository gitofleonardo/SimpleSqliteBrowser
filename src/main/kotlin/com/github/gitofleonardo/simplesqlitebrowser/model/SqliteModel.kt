package com.github.gitofleonardo.simplesqlitebrowser.model

import com.github.gitofleonardo.simplesqlitebrowser.data.DbColumn
import com.github.gitofleonardo.simplesqlitebrowser.data.DbRow
import com.github.gitofleonardo.simplesqlitebrowser.data.DbTableInstance
import com.github.gitofleonardo.simplesqlitebrowser.data.SqliteMetadata
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SqliteModel {
    private const val NULL = "null"

    suspend fun loadMetaData(file: VirtualFile) : SqliteMetadata = withContext(context = Dispatchers.IO) {
        val connection = ConnectionManager.createConnection(file)
        val metadata = SqliteMetadata()
        connection?.let {
            val md = it.metaData
            metadata.isValidSqliteDatabase = true
            metadata.version = md.databaseMajorVersion
            metadata.driverVersion = md.driverVersion
        }
        ConnectionManager.disposeConnection(connection)
        metadata
    }

    suspend fun loadTables(file: VirtualFile) : List<String> = withContext(context = Dispatchers.IO){
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
        if (connection != null) {
            val columnResult = connection.metaData.getColumns(null, null, tableName, null)
            while (columnResult.next()) {
                val columnName = columnResult.getString("COLUMN_NAME")
                columns.add(DbColumn(columnName))
            }

            val statement = connection.createStatement()
            val rowResult = statement.executeQuery("SELECT * FROM $tableName LIMIT $pageCount OFFSET ${pageCount * (page - 1)}")
            while (rowResult.next()) {
                val dbRows = mutableListOf<String>()
                val dbRow = DbRow(dbRows)
                for (column in columns) {
                    val obj = rowResult.getObject(column.name)
                    val columnData = obj ?: NULL
                    dbRows.add(columnData.toString())
                }
                rows.add(dbRow)
            }

            val countResult = statement.executeQuery("SELECT COUNT(*) FROM $tableName")
            countResult.next()
            totalCount = countResult.getInt(1)
        }
        DbTableInstance(columns, rows, rows.size, page, totalCount)
    }
}
