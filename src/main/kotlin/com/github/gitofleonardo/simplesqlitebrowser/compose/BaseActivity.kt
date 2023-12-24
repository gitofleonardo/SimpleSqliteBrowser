package com.github.gitofleonardo.simplesqlitebrowser.compose

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.awt.ComposePanel
import com.intellij.util.ui.components.BorderLayoutPanel
import java.awt.BorderLayout
import java.awt.Component

abstract class BaseActivity : BorderLayoutPanel() {

    init {
        val composePanel = ComposePanel()
        composePanel.setContent {
            MaterialTheme {
                ComposeContent()
            }
        }
        add(composePanel, BorderLayout.CENTER)
    }

    @Composable
    abstract fun ComposeContent()

    final override fun add(comp: Component): Component {
        return super.add(comp)
    }

    final override fun add(comp: Component, constraints: Any) {
        super.add(comp, constraints)
    }

    final override fun add(comp: Component, index: Int): Component {
        return super.add(comp, index)
    }

    final override fun add(comp: Component, constraints: Any, index: Int) {
        super.add(comp, constraints, index)
    }

    final override fun add(name: String, comp: Component): Component {
        return super.add(name, comp)
    }
}