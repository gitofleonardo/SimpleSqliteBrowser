package com.github.gitofleonardo.simplesqlitebrowser.ui.viewmodel

import com.github.gitofleonardo.simplesqlitebrowser.data.SqliteMetadata
import com.github.gitofleonardo.simplesqlitebrowser.model.SqliteModel
import com.github.gitofleonardo.simplesqlitebrowser.mvvm.LiveData
import com.github.gitofleonardo.simplesqlitebrowser.mvvm.ViewModel
import com.intellij.openapi.vfs.VirtualFile
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.swing.SwingUtilities

class MetadataViewModel : ViewModel {
    private val model = SqliteModel

    val metadata: LiveData<SqliteMetadata> = LiveData()

    fun loadMetaData(file: VirtualFile) {
        Observable
                .create { emitter ->
                    emitter.onNext(model.loadMetaData(file))
                }
                .subscribeOn(Schedulers.io())
                .subscribe {
                    SwingUtilities.invokeLater {
                        metadata.value = it
                    }
                }
    }
}
