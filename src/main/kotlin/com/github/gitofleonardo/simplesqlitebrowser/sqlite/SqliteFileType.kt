package com.github.gitofleonardo.simplesqlitebrowser.sqlite

import com.github.gitofleonardo.simplesqlitebrowser.DESC
import com.github.gitofleonardo.simplesqlitebrowser.EXTENSION
import com.github.gitofleonardo.simplesqlitebrowser.NAME
import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

class SqliteFileType : LanguageFileType(SqliteLanguage) {
    companion object {
        @JvmStatic
        val INSTANCE = SqliteFileType()
    }

    override fun getName(): String = NAME

    override fun getDescription(): String = DESC

    override fun getDefaultExtension(): String = EXTENSION

    override fun getIcon(): Icon? {
        return null
    }
}
