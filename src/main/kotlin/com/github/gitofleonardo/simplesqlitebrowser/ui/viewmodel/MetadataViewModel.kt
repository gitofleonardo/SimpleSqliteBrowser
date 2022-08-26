package com.github.gitofleonardo.simplesqlitebrowser.ui.viewmodel

import com.github.gitofleonardo.simplesqlitebrowser.data.SqliteMetadata
import com.github.gitofleonardo.simplesqlitebrowser.model.SqliteModel
import com.github.gitofleonardo.simplesqlitebrowser.mvvm.LiveData
import com.github.gitofleonardo.simplesqlitebrowser.mvvm.ViewModel
import com.github.gitofleonardo.simplesqlitebrowser.viewModelScope
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.launch

class MetadataViewModel : ViewModel {
    private val model = SqliteModel

    val metadata: LiveData<SqliteMetadata> = LiveData()

    fun loadMetaData(file: VirtualFile) {
        viewModelScope.launch {
            val md = model.loadMetaData(file)
            metadata.value = md
        }
    }
}
