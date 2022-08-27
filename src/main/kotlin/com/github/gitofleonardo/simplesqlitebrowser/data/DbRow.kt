package com.github.gitofleonardo.simplesqlitebrowser.data

class DbRow(
    val rowData: List<RowData>
) {
    data class RowData(val type: Int, val typeName: String, val data:Any?)
}
