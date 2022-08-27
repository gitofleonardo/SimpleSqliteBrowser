package com.github.gitofleonardo.simplesqlitebrowser.tools

import com.github.gitofleonardo.simplesqlitebrowser.data.DbColumn
import com.github.gitofleonardo.simplesqlitebrowser.data.DbTable
import com.github.gitofleonardo.simplesqlitebrowser.data.SqliteMetadata
import javax.swing.event.TreeModelListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.MutableTreeNode
import javax.swing.tree.TreeModel
import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath

class DatabaseTreeModel(private val metadata: SqliteMetadata) : TreeModel {
    private val root = DefaultMutableTreeNode("Tables")

    init {
        for (table in metadata.tables) {
            root.add(buildTableNode(table))
        }
    }

    private fun buildTableNode(table: DbTable): MutableTreeNode {
        val tableNode = DefaultMutableTreeNode(table, true)
        for (col in table.columns) {
            tableNode.add(buildColumnNode(col))
        }
        return tableNode
    }

    private fun buildColumnNode(col: DbColumn): MutableTreeNode {
        return DefaultMutableTreeNode(col, false)
    }

    override fun getRoot(): Any {
        return root
    }

    override fun getChild(parent: Any?, index: Int): Any? {
        if (parent == null) {
            return null
        }
        if (parent !is MutableTreeNode) {
            return null
        }
        if (index >= parent.childCount) {
            return null
        }
        return parent.getChildAt(index)
    }

    override fun getChildCount(parent: Any?): Int {
        if (parent == null) {
            return 0
        }
        if (parent !is MutableTreeNode) {
            return 0
        }
        return parent.childCount
    }

    override fun isLeaf(node: Any?): Boolean {
        if (node == null || node !is DefaultMutableTreeNode) {
            return false
        }
        return node.isLeaf
    }

    override fun valueForPathChanged(path: TreePath?, newValue: Any?) {
    }

    override fun getIndexOfChild(parent: Any?, child: Any?): Int {
        if (parent == null || child == null) {
            return -1
        }
        if (parent !is TreeNode || child !is TreeNode) {
            return -1
        }
        return parent.getIndex(child)
    }

    override fun addTreeModelListener(l: TreeModelListener?) {
    }

    override fun removeTreeModelListener(l: TreeModelListener?) {
    }
}