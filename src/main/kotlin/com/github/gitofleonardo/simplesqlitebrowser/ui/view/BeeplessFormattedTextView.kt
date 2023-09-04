package com.github.gitofleonardo.simplesqlitebrowser.ui.view

import javax.swing.JFormattedTextField

class BeeplessFormattedTextView(formatter: AbstractFormatter?) : JFormattedTextField(formatter) {

    override fun invalidEdit() {
        // No beep feedback
    }
}