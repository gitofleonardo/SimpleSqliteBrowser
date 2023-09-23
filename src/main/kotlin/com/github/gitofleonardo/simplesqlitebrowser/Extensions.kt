package com.github.gitofleonardo.simplesqlitebrowser

import com.intellij.openapi.ui.ComboBox
import java.awt.event.*
import javax.swing.JComponent
import javax.swing.text.JTextComponent

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

        override fun mousePressed(e: MouseEvent?) {}

        override fun mouseReleased(e: MouseEvent?) {}

        override fun mouseEntered(e: MouseEvent?) {}

        override fun mouseExited(e: MouseEvent?) {}
    })
}

fun JComponent.addOnTouchListener(listener: (MouseEvent) -> Unit) {
    addMouseListener(object : MouseListener {
        override fun mouseClicked(e: MouseEvent?) {}

        override fun mousePressed(e: MouseEvent?) {
            e?.let(listener)
        }

        override fun mouseReleased(e: MouseEvent?) {}

        override fun mouseEntered(e: MouseEvent?) {}

        override fun mouseExited(e: MouseEvent?) {}
    })
}

fun Any?.toStringOr(placeHolder: String = ""): String {
    return this?.toString() ?: placeHolder
}

private const val BYTE_SIZE = 1024
private const val K_BYTE_SIZE = 1024 * 1024

fun ByteArray.toSizeString(): String {
    val siz = size
    return if (siz <= BYTE_SIZE) {
        "$siz Bytes"
    } else if (siz <= K_BYTE_SIZE) {
        String.format("%.2f KB", siz / BYTE_SIZE.toFloat())
    } else{
        String.format("%.2f MB", siz / K_BYTE_SIZE.toFloat())
    }
}
