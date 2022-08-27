package com.github.gitofleonardo.simplesqlitebrowser.data

class DbRow(
    val rowData: List<RowData>
) {
    data class RowData(val type: Int, val typeName: String, val data:Any?): Comparable<RowData> {
        override fun compareTo(other: RowData): Int {
            if (this == other) {
                return 0
            }
            if (data == null && other.data == null) {
                return 0
            }
            if (data == null) {
                return 1
            }
            if (other.data == null) {
                return -1
            }
            if (data is Number && other.data is Number) {
                return data.toFloat().compareTo(other.data.toFloat())
            }
            return data.toString().compareTo(other.data.toString())
        }
    }
}
