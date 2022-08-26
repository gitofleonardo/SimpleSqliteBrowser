package com.github.gitofleonardo.simplesqlitebrowser.ui

import com.intellij.ui.components.JBPanel
import javax.swing.Icon
import javax.swing.JPanel

abstract class TabbedChildView : JBPanel<TabbedChildView>() {
    abstract val title: String
    abstract val icon: Icon?
}
