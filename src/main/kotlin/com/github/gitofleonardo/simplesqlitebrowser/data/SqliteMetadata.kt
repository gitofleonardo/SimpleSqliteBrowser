package com.github.gitofleonardo.simplesqlitebrowser.data

class SqliteMetadata {
    var isValidSqliteDatabase: Boolean = false
    var version: Int = -1
    var driverVersion: String? = null
    val tables = ArrayList<DbTable>()
}
