package com.github.gitofleonardo.simplesqlitebrowser.ui.window

import com.github.gitofleonardo.simplesqlitebrowser.ui.TabbedChildView
import com.github.gitofleonardo.simplesqlitebrowser.ui.viewmodel.MetadataViewModel
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

private const val TITLE = "Database Metadata"

class SqliteMetaDataWindow(private val file: VirtualFile) : TabbedChildView() {
    override val title: String = TITLE
    override val icon: Icon? = null

    private val viewModel = MetadataViewModel()

    init {
        viewModel.metadata.observe {
            if (!it.isValidSqliteDatabase) {
                return@observe
            }
        }

        viewModel.loadMetaData(file)
    }
}
