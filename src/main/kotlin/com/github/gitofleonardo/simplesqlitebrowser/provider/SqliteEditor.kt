package com.github.gitofleonardo.simplesqlitebrowser.provider

import com.github.gitofleonardo.simplesqlitebrowser.ui.window.SqliteBrowserMainWindow
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import java.beans.PropertyChangeListener
import javax.swing.JComponent

private const val NAME = "SqliteEditor"

class SqliteEditor(private val proj: Project, private val dbFile: VirtualFile) : UserDataHolderBase(), FileEditor {

    override fun dispose() {
    }

    override fun getComponent(): JComponent {
        return SqliteBrowserMainWindow(dbFile)
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return null
    }

    override fun getName(): String = NAME

    override fun setState(state: FileEditorState) {
    }

    override fun isModified(): Boolean {
        return false
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {
    }

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {
    }

    override fun getCurrentLocation(): FileEditorLocation? {
        return null
    }

    override fun getFile(): VirtualFile {
        return dbFile
    }
}
