package com.github.gitofleonardo.simplesqlitebrowser.ui.window

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBTabbedPane
import java.awt.BorderLayout
import javax.swing.JPanel

class SqliteBrowserMainWindow(dbFile: VirtualFile) : JPanel(BorderLayout()) {
    private val tabbedChildViews = arrayListOf(
        SqliteTablesWindow(dbFile),
        SqliteMetaDataWindow(dbFile)
    )
    private val tabbedPane: JBTabbedPane = JBTabbedPane()

    init {
        for (child in tabbedChildViews) {
            tabbedPane.addTab(child.title, child.icon, child)
        }

        add(tabbedPane)
    }
}
