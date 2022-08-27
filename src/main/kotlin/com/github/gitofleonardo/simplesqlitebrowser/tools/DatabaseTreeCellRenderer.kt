package com.github.gitofleonardo.simplesqlitebrowser.tools

import com.github.gitofleonardo.simplesqlitebrowser.data.DbColumn
import com.github.gitofleonardo.simplesqlitebrowser.data.DbTable
import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.GridLayoutManager
import java.awt.Component
import java.awt.Dimension
import java.awt.Insets
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeCellRenderer

class DatabaseTreeCellRenderer : TreeCellRenderer {
    override fun getTreeCellRendererComponent(
        tree: JTree,
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ): Component {
        if (value == null || value !is DefaultMutableTreeNode) {
            return JPanel()
        }
        return when (val data = value.userObject) {
            null -> {
                JPanel()
            }
            is DbTable -> {
                buildTreeNodeComponent( data.tableName, "", "")
            }
            is DbColumn -> {
                buildTreeNodeComponent(data.name, data.typeName, "\"${data.name}\" ${data.typeName} ${data.schema}")
            }
            else -> {
                buildTreeNodeComponent(data.toString(), "", "")
            }
        }
    }

    // Auto-generated code {@
    private fun buildTreeNodeComponent(name: String, type: String, schema: String): Component {
        val rootPanel = JPanel()
        rootPanel.layout = GridLayoutManager(1, 3, Insets(0, 0, 0, 0), -1, -1, true, false)
        rootPanel.minimumSize = Dimension(28, 26)
        rootPanel.preferredSize = Dimension(28, 26)
        val nameLabel = JLabel()
        nameLabel.text = name
        rootPanel.add(nameLabel, GridConstraints(0, 0, 1, 1,
            GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
            null, null, null, 0, false))
        val typeLabel = JLabel()
        typeLabel.text = type
        rootPanel.add(typeLabel, GridConstraints(0, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null, 0, false))
        val schemaLabel = JLabel()
        schemaLabel.text = schema
        rootPanel.add(schemaLabel, GridConstraints(0, 2, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false))
        return rootPanel
    }
    // @}
}
