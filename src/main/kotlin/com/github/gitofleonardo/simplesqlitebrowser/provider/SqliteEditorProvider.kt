package com.github.gitofleonardo.simplesqlitebrowser.provider

import com.github.gitofleonardo.simplesqlitebrowser.EXTENSION
import com.github.gitofleonardo.simplesqlitebrowser.SQLITE_LANGUAGE
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile

class SqliteEditorProvider : FileEditorProvider, DumbAware {
    override fun accept(project: Project, file: VirtualFile): Boolean {
        return file.extension == EXTENSION
    }

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        return SqliteEditor(project, file)
    }

    override fun getEditorTypeId(): String = SQLITE_LANGUAGE

    override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.HIDE_DEFAULT_EDITOR

    override fun disposeEditor(editor: FileEditor) {
        super.disposeEditor(editor)
        Disposer.dispose(editor)
    }
}
