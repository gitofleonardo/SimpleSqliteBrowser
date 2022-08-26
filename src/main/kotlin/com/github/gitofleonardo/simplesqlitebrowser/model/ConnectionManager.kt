package com.github.gitofleonardo.simplesqlitebrowser.model

import com.intellij.openapi.vfs.VirtualFile
import java.sql.Connection
import java.sql.DriverManager

object ConnectionManager {
    // Ensure driver is loaded
    private val clazz = Class.forName("org.sqlite.JDBC")

    fun createConnection(file: VirtualFile): Connection? {
        return try {
            val connection = DriverManager.getConnection("jdbc:sqlite:${file.canonicalPath}")
            connection
        } catch (e : Exception) {
            e.printStackTrace()
            null
        }
    }

    fun disposeConnection(connection: Connection?) {
        connection?.close()
    }
}
