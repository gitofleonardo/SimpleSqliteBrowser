package com.github.gitofleonardo.simplesqlitebrowser.ui.window

import com.github.gitofleonardo.simplesqlitebrowser.data.SqliteMetadata
import com.github.gitofleonardo.simplesqlitebrowser.tools.DatabaseTreeCellRenderer
import com.github.gitofleonardo.simplesqlitebrowser.tools.DatabaseTreeModel
import com.github.gitofleonardo.simplesqlitebrowser.ui.TabbedChildView
import com.github.gitofleonardo.simplesqlitebrowser.ui.viewmodel.MetadataViewModel
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBTreeTable
import com.intellij.ui.treeStructure.Tree
import java.awt.BorderLayout
import javax.swing.Icon
import javax.swing.JPanel
import javax.swing.JTree

private const val TITLE = "Database Metadata"

class SqliteMetaDataWindow(private val file: VirtualFile) : TabbedChildView() {
    override val title: String = TITLE
    override val icon: Icon? = null

    private val viewModel = MetadataViewModel()
    private val emptyMetadata = SqliteMetadata()
    private var treeModel: DatabaseTreeModel = DatabaseTreeModel(emptyMetadata)

    // Auto-generated components {@
    private lateinit var rootTree: Tree
    private lateinit var rootContainer: JPanel
    // @}

    init {
        setupUI()
        initObserve()
        viewModel.loadMetaData(file)
    }

    private fun initObserve() {
        viewModel.metadata.observe {
            if (!it.isValidSqliteDatabase) {
                return@observe
            }
            treeModel = DatabaseTreeModel(it)
            rootTree.model = treeModel
        }
    }

    // UI Setup {@
    private fun setupUI() {
        rootContainer = JPanel()
        rootContainer.layout = BorderLayout(0, 0)
        rootTree = Tree()
        rootTree.cellRenderer = DatabaseTreeCellRenderer()
        rootContainer.add(rootTree, BorderLayout.CENTER)

        layout = BorderLayout()
        add(rootContainer)
    }
    // @}
}
