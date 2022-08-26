package com.github.gitofleonardo.simplesqlitebrowser.ui.window

import com.github.gitofleonardo.simplesqlitebrowser.addOnClickListener
import com.github.gitofleonardo.simplesqlitebrowser.addOnItemChangeListener
import com.github.gitofleonardo.simplesqlitebrowser.addOnKeyEventListener
import com.github.gitofleonardo.simplesqlitebrowser.data.DbTableInstance
import com.github.gitofleonardo.simplesqlitebrowser.ui.TabbedChildView
import com.github.gitofleonardo.simplesqlitebrowser.ui.viewmodel.TableViewModel
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.GridLayoutManager
import com.intellij.uiDesigner.core.Spacer
import org.jdesktop.swingx.combobox.ListComboBoxModel
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Insets
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.text.NumberFormat
import javax.swing.*
import javax.swing.event.TableModelListener
import javax.swing.table.TableModel
import javax.swing.text.NumberFormatter

private const val TITLE = "Tables"

class SqliteTablesWindow(private val dbFile: VirtualFile) : TabbedChildView() {
    override val title: String = TITLE
    override val icon: Icon? = null

    // Auto generated component begin {@
    private lateinit var rootPanel: JPanel
    private lateinit var panelTableSelection: JPanel
    private lateinit var tableTitle: JLabel
    private lateinit var tableComboBox: ComboBox<Any>
    private lateinit var panelPageJump: JPanel
    private lateinit var pageTitle: JLabel
    private lateinit var pageInputField: JFormattedTextField
    private lateinit var jumpButton: JButton
    private lateinit var panelPageSwitch: JPanel
    private lateinit var firstPageBtn: JButton
    private lateinit var prevPageBtn: JButton
    private lateinit var pageIndicatorText: JLabel
    private lateinit var nextPageBtn: JButton
    private lateinit var lastPageBtn: JButton
    private lateinit var refreshButton: JButton
    private lateinit var dataTable: JBTable
    private lateinit var toolbarContainer: JPanel
    // @}

    private val emptyTablePage = DbTableInstance()

    private val viewModel = TableViewModel(dbFile)
    private var tableModel = DatabaseTableModel(emptyTablePage)
    private val tables = mutableListOf<String>()
    private val tableComboModel = ListComboBoxModel(tables)

    init {
        setupUI()
        initListeners()
        initObservers()
        viewModel.loadTables()
    }

    private fun initListeners() {
        tableComboBox.addOnItemChangeListener {
            viewModel.resetTableData(it as String)
        }
        pageInputField.addOnKeyEventListener {
            if (it.keyCode == KeyEvent.VK_ENTER) {
                val page = pageInputField.value as Int
                viewModel.loadPage(page)
            }
        }
        jumpButton.addOnClickListener {
            val page = pageInputField.value as Int
            viewModel.loadPage(page)
        }
        prevPageBtn.addOnClickListener {
            viewModel.loadPreviousPage()
        }
        nextPageBtn.addOnClickListener {
            viewModel.loadNextPage()
        }
        firstPageBtn.addOnClickListener {
            viewModel.loadFirstPage()
        }
        lastPageBtn.addOnClickListener {
            viewModel.loadLastPage()
        }
        refreshButton.addOnClickListener {
            viewModel.resetTableData()
        }
    }

    private fun initObservers() {
        viewModel.tables.observe {
            tables.clear()
            tables.addAll(it)
            tableComboModel.actionPerformed(ActionEvent(this, 0, ListComboBoxModel.UPDATE))

            if (tables.isNotEmpty()) {
                tableComboModel.selectedItem = tables[0]
                viewModel.resetTableData(tables[0])
            }
        }

        viewModel.tableData.observe {
            tableModel = DatabaseTableModel(it)
            dataTable.model = tableModel

            pageIndicatorText.text = "${viewModel.currentPage}-${viewModel.totalPages}"
        }
    }

    // UI configuration begin {@
    private fun setupUI() {
        rootPanel = JPanel()
        rootPanel.layout = GridLayoutManager(2, 1, Insets(0, 0, 0, 0),
            -1, -1)
        toolbarContainer = JPanel()
        toolbarContainer.layout = FlowLayout(FlowLayout.LEFT, 5, 5)
        rootPanel.add(toolbarContainer, GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW, null,
                null, null, 0, false))
        panelTableSelection = JPanel()
        panelTableSelection.layout = FlowLayout(FlowLayout.CENTER, 5, 5)
        toolbarContainer.add(panelTableSelection)
        tableTitle = JLabel()
        tableTitle.text = "Table:"
        panelTableSelection.add(tableTitle)
        tableComboBox = ComboBox(tableComboModel)
        panelTableSelection.add(tableComboBox)
        val spacer1 = Spacer()
        toolbarContainer.add(spacer1)
        panelPageJump = JPanel()
        panelPageJump.layout = FlowLayout(FlowLayout.CENTER, 5, 5)
        toolbarContainer.add(panelPageJump)
        pageTitle = JLabel()
        pageTitle.text = "Page:"
        panelPageJump.add(pageTitle)
        pageInputField = JFormattedTextField(NumberFormatter(NumberFormat.getIntegerInstance()).apply {
            allowsInvalid = false
            minimum = 0
        })
        pageInputField.preferredSize = Dimension(100, 30)
        pageInputField.text = "0"
        panelPageJump.add(pageInputField)
        jumpButton = JButton()
        jumpButton.text = "Jump"
        panelPageJump.add(jumpButton)
        val spacer2 = Spacer()
        toolbarContainer.add(spacer2)
        panelPageSwitch = JPanel()
        panelPageSwitch.layout = FlowLayout(FlowLayout.CENTER, 5, 5)
        toolbarContainer.add(panelPageSwitch)
        firstPageBtn = JButton()
        firstPageBtn.maximumSize = Dimension(78, 30)
        firstPageBtn.preferredSize = Dimension(30, 30)
        firstPageBtn.text = "<<"
        firstPageBtn.isVisible = true
        panelPageSwitch.add(firstPageBtn)
        prevPageBtn = JButton()
        prevPageBtn.preferredSize = Dimension(30, 30)
        prevPageBtn.text = "<"
        panelPageSwitch.add(prevPageBtn)
        pageIndicatorText = JLabel()
        pageIndicatorText.text = "0-0"
        panelPageSwitch.add(pageIndicatorText)
        nextPageBtn = JButton()
        nextPageBtn.preferredSize = Dimension(30, 30)
        nextPageBtn.text = ">"
        panelPageSwitch.add(nextPageBtn)
        lastPageBtn = JButton()
        lastPageBtn.preferredSize = Dimension(30, 30)
        lastPageBtn.text = ">>"
        panelPageSwitch.add(lastPageBtn)
        refreshButton = JButton()
        refreshButton.text = "Refresh"
        toolbarContainer.add(refreshButton)
        dataTable = JBTable(tableModel)
        dataTable.maximumSize = Dimension(40, 40)
        dataTable.fillsViewportHeight = true
        dataTable.autoResizeMode = JTable.AUTO_RESIZE_OFF
        val tableScrollPane = JBScrollPane()
        tableScrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        tableScrollPane.setViewportView(dataTable)
        rootPanel.add(tableScrollPane, GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_WANT_GROW,
            null, null, null, 0, true))
        layout = BorderLayout()
        add(rootPanel)
    }
    // @}
}

private class DatabaseTableModel(
    private val dbTableData: DbTableInstance
) : TableModel {
    private val modelListeners: ArrayList<TableModelListener?> = ArrayList()

    override fun getRowCount(): Int = dbTableData.rows.size

    override fun getColumnCount(): Int = dbTableData.columns.size

    override fun getColumnName(columnIndex: Int): String {
        return dbTableData.columns[columnIndex].name
    }

    override fun getColumnClass(columnIndex: Int): Class<*> = String::class.java

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = true

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        return dbTableData.rows[rowIndex].rowData[columnIndex]
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        // TODO Modify database
    }

    override fun addTableModelListener(l: TableModelListener?) {
        modelListeners.add(l)
    }

    override fun removeTableModelListener(l: TableModelListener?) {
        modelListeners.remove(l)
    }
}
