package com.github.gitofleonardo.simplesqlitebrowser.data

data class DbColumn(
    val name: String,
    val type: Int,
    val typeName: String,
    val schema: String
)
