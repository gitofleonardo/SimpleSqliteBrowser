package com.github.gitofleonardo.simplesqlitebrowser

import com.github.gitofleonardo.simplesqlitebrowser.mvvm.ViewModel
import com.intellij.openapi.ui.ComboBox
import kotlinx.coroutines.*
import java.awt.event.*
import javax.swing.JComponent
import javax.swing.text.JTextComponent

val ViewModel.viewModelScope
    get() = MainScope()


inline fun <reified T> ComboBox<T>.addOnItemChangeListener(crossinline listener: (T) -> Unit) {
    addItemListener {
        if (it.stateChange == ItemEvent.SELECTED) {
            val item = it.item
            if (item is T) {
                listener.invoke(item)
            }
        }
    }
}

fun JTextComponent.addOnKeyEventListener(listener: (KeyEvent) -> Unit) {
    addKeyListener(object : KeyListener {
        override fun keyTyped(e: KeyEvent?) {
            e?.let(listener)
        }

        override fun keyPressed(e: KeyEvent?) {
        }

        override fun keyReleased(e: KeyEvent?) {
        }
    })
}

fun JComponent.addOnClickListener(listener:(MouseEvent) -> Unit) {
    addMouseListener(object : MouseListener {
        override fun mouseClicked(e: MouseEvent?) {
            e?.let(listener)
        }

        override fun mousePressed(e: MouseEvent?) {
        }

        override fun mouseReleased(e: MouseEvent?) {
        }

        override fun mouseEntered(e: MouseEvent?) {
        }

        override fun mouseExited(e: MouseEvent?) {
        }
    })
}