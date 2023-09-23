package com.github.gitofleonardo.simplesqlitebrowser.ui.viewmodel

import com.github.gitofleonardo.simplesqlitebrowser.data.DbTableInstance
import com.github.gitofleonardo.simplesqlitebrowser.model.SqliteModel
import com.github.gitofleonardo.simplesqlitebrowser.mvvm.LiveData
import com.github.gitofleonardo.simplesqlitebrowser.mvvm.ViewModel
import com.intellij.openapi.vfs.VirtualFile
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.swing.SwingUtilities
import kotlin.math.ceil

private const val DEFAULT_PGE_COUNT = 50

class TableViewModel(private val dbFile: VirtualFile) : ViewModel {
    private val model = SqliteModel
    var currentPage: Int = 1
    var pageCount: Int = DEFAULT_PGE_COUNT
    var currentTableName: String? = null
    var totalPages: Int = 1
    var totalCount: Int = 0

    val tables = LiveData<List<String>>()
    val tableData = LiveData<DbTableInstance>()

    fun resetTableData() {
        currentTableName?.let { resetTableData(it) }
    }

    fun resetTableData(tableName: String) {
        currentPage = 1
        currentTableName = tableName
        loadTableData(dbFile, tableName, pageCount, currentPage)
    }

    fun loadNextPage() {
        currentTableName?.let {
            if (currentPage < totalPages) {
                ++currentPage
                loadTableData(dbFile, it, pageCount, currentPage)
            }
        }
    }

    fun loadPreviousPage() {
        currentTableName?.let {
            if (currentPage > 1) {
                --currentPage
                loadTableData(dbFile, it, pageCount, currentPage)
            }
        }
    }

    fun loadPage(page: Int) {
        if (page < 1 || page > totalPages) {
            return
        }
        currentTableName?.let {
            currentPage = page
            loadTableData(dbFile, it, pageCount, currentPage)
        }
    }

    fun loadFirstPage() {
        loadPage(1)
    }

    fun loadLastPage() {
        loadPage(totalPages)
    }

    private fun loadTableData(file: VirtualFile, tableName: String, pageCount: Int, page: Int) {
        Observable
                .create { emitter ->
                    emitter.onNext(model.loadTableData(file, tableName, pageCount, page))
                }
                .subscribeOn(Schedulers.io())
                .subscribe { result ->
                    SwingUtilities.invokeLater {
                        totalCount = result.totalCount
                        totalPages = ceil(totalCount.toFloat() / pageCount).toInt()
                        tableData.value = result
                    }
                }

    }

    fun loadTables() {
        Observable
                .create { emitter->
                    emitter.onNext(model.loadTables(dbFile))
                }
                .subscribeOn(Schedulers.io())
                .subscribe {  tbls ->
                    SwingUtilities.invokeLater {
                        tables.value = tbls
                    }
                }
    }
}
