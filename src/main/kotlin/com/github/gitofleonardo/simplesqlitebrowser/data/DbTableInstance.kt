package com.github.gitofleonardo.simplesqlitebrowser.data

/**
 * Represents a page of table.
 */
class DbTableInstance(
    val columns: List<DbColumn> = emptyList(),
    val rows: List<DbRow> = emptyList(),
    val pageCount: Int = 0,
    val page: Int = 0,
    val totalCount: Int = 0
)
