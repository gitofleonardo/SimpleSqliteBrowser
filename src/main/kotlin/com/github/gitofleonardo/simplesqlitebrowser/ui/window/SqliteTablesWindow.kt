package com.github.gitofleonardo.simplesqlitebrowser.ui.window

import com.github.gitofleonardo.simplesqlitebrowser.*
import com.github.gitofleonardo.simplesqlitebrowser.data.DbRow
import com.github.gitofleonardo.simplesqlitebrowser.data.DbTableInstance
import com.github.gitofleonardo.simplesqlitebrowser.tools.DatabaseTableCellRenderer
import com.github.gitofleonardo.simplesqlitebrowser.tools.DatabaseTableModel
import com.github.gitofleonardo.simplesqlitebrowser.ui.TabbedChildView
import com.github.gitofleonardo.simplesqlitebrowser.ui.view.BeeplessFormattedTextView
import com.github.gitofleonardo.simplesqlitebrowser.ui.viewmodel.TableViewModel
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.GridLayoutManager
import com.intellij.uiDesigner.core.Spacer
import net.coderazzi.filters.gui.AutoChoices
import net.coderazzi.filters.gui.IFilterEditor
import net.coderazzi.filters.gui.IFilterHeaderObserver
import net.coderazzi.filters.gui.TableFilterHeader
import org.jdesktop.swingx.combobox.ListComboBoxModel
import java.awt.*
import java.awt.datatransfer.StringSelection
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.sql.Types
import java.text.NumberFormat
import java.util.Base64
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.table.TableColumn
import javax.swing.text.NumberFormatter

private const val TITLE = "Tables"
private const val IMAGE_PREVIEW_MAX_WIDTH = 360
private const val IMAGE_PREVIEW_MAX_HEIGHT = 180
private const val BASE64_PREVIEW_MAX_LENGTH = 8192
private const val TABLE_CARD_DATA = "TABLE_DATA"
private const val TABLE_CARD_LOADING = "TABLE_LOADING"
private const val COPY_BASE64_TEXT = "Copy Base64"
private const val SAVE_IMAGE_TEXT = "Save Image"
private const val SAVE_BLOB_TEXT = "Save BLOB"

class SqliteTablesWindow(private val dbFile: VirtualFile) : TabbedChildView(), IFilterHeaderObserver {
    override val title: String = TITLE
    override val icon: Icon? = null

    // Auto generated component begin {@
    private lateinit var rootPanel: JPanel
    private lateinit var panelTableSelection: JPanel
    private lateinit var tableTitle: JLabel
    private lateinit var tableComboBox: ComboBox<Any>
    private lateinit var panelPageJump: JPanel
    private lateinit var pageTitle: JLabel
    private lateinit var pageInputField: BeeplessFormattedTextView
    private lateinit var jumpButton: JButton
    private lateinit var panelPageSwitch: JPanel
    private lateinit var firstPageBtn: JButton
    private lateinit var prevPageBtn: JButton
    private lateinit var pageIndicatorText: JLabel
    private lateinit var nextPageBtn: JButton
    private lateinit var lastPageBtn: JButton
    private lateinit var refreshButton: JButton
    private lateinit var resetFiltersButton: JButton
    private lateinit var dataTable: JBTable
    private lateinit var tableContainerPanel: JPanel
    private lateinit var tableLoadingPanel: JPanel
    private lateinit var toolbarContainer: JPanel
    private lateinit var bottomToolPanel: JPanel
    private lateinit var bottomInfoPanel: JPanel
    private lateinit var dbValueInfoLabel: JLabel
    private lateinit var dbValueField: JTextArea
    private lateinit var textScrollContainer: JScrollPane
    private lateinit var imageLabel: JLabel
    private lateinit var dataHolderPanel: JPanel
    private lateinit var imageScrollContainer: JScrollPane
    private lateinit var copyBase64Button: JButton
    private lateinit var saveBlobButton: JButton
    // @}

    private val emptyTablePage = DbTableInstance()

    private val viewModel = TableViewModel(dbFile)
    private lateinit var tableModel: DatabaseTableModel
    private val tables = mutableListOf<String>()
    private val tableComboModel = ListComboBoxModel(tables)
    private val tableFilterHeader = TableFilterHeader()
    private val filterHeaderCache = mutableMapOf<String, String>()
    private val filterEditors = mutableMapOf<String, IFilterEditor>()
    private var currentFullBase64: String? = null
    private var currentBlobBytes: ByteArray? = null
    private var currentBlobDefaultFileName: String = "blob.bin"

    init {
        setupUI()
        initListeners()
        initObservers()
        viewModel.loadTables()
    }

    private fun initListeners() {
        tableComboBox.addOnItemChangeListener {
            filterHeaderCache.clear()
            viewModel.resetTableData(it as String)
        }
        pageInputField.addOnKeyEventListener {
            if (it.keyCode == KeyEvent.VK_ENTER) {
                val page = pageInputField.value as Int
                viewModel.loadPage(page)
            }
        }
        jumpButton.addActionListener {
            val page = pageInputField.value as Int
            viewModel.loadPage(page)
        }
        prevPageBtn.addActionListener {
            viewModel.loadPreviousPage()
        }
        nextPageBtn.addActionListener {
            viewModel.loadNextPage()
        }
        firstPageBtn.addActionListener {
            viewModel.loadFirstPage()
        }
        lastPageBtn.addActionListener {
            viewModel.loadLastPage()
        }
        refreshButton.addActionListener {
            viewModel.resetTableData()
        }
        resetFiltersButton.addActionListener {
            resetAllFilters()
        }
        saveBlobButton.addActionListener {
            saveCurrentBlobToLocal()
        }
        dataTable.addOnTouchListener {
            updateTableSelection()
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
            if (::tableModel.isInitialized && tableModel.canReuseWith(it)) {
                tableModel.updateTableData(it)
            } else {
                filterEditors.clear()
                tableModel = DatabaseTableModel(it)
                dataTable.model = tableModel
            }

            pageIndicatorText.text = "${viewModel.currentPage}-${viewModel.totalPages}"
        }
        viewModel.isLoading.observe { loading ->
            val cardLayout = tableContainerPanel.layout as CardLayout
            if (loading) {
                cardLayout.show(tableContainerPanel, TABLE_CARD_LOADING)
            } else {
                cardLayout.show(tableContainerPanel, TABLE_CARD_DATA)
            }
        }
    }

    private fun updateTableSelection() {
        val row = dataTable.selectedRow
        val column = dataTable.selectedColumn
        if (!tableModel.checkIndexRange(row, column)) {
            return
        }
        val data = tableModel.getValueAt(dataTable.convertRowIndexToModel(row), dataTable.convertColumnIndexToModel(column)) as DbRow.RowData
        when (data.type) {
            Types.BLOB -> {
                val blob = data.data as ByteArray? ?: byteArrayOf()
                val image = decodeImage(blob)
                if (image != null) {
                    setCurrentImageInfo(blob, image)
                    updateDataDisplayPanel(showImage = true)
                } else {
                    val encodedText = if (blob.isEmpty()) "" else Base64.getEncoder().encodeToString(blob)
                    setCurrentBase64Info(blob, encodedText)
                    updateDataDisplayPanel(showImage = false)
                }
            }
            else -> {
                setCurrentTextInfo(data.data.toStringOr())
                updateDataDisplayPanel(showImage = false)
            }
        }
    }

    private fun setCurrentImageInfo(bytes: ByteArray, image: BufferedImage) {
        currentFullBase64 = null
        copyBase64Button.isVisible = false
        currentBlobBytes = if (bytes.isEmpty()) null else bytes
        currentBlobDefaultFileName = bytes.guessImageDefaultFileName() ?: "image.bin"
        saveBlobButton.isVisible = bytes.isNotEmpty()
        saveBlobButton.text = SAVE_IMAGE_TEXT
        if (bytes.isEmpty()) {
            imageLabel.icon = null
            dbValueInfoLabel.text = ""
            return
        }
        val scaledImage = image.getScaledPreviewImage(IMAGE_PREVIEW_MAX_WIDTH, IMAGE_PREVIEW_MAX_HEIGHT)
        imageLabel.icon = ImageIcon(scaledImage)
        dbValueInfoLabel.text = "Size: ${bytes.toSizeString()} (${image.width}x${image.height} pixels)"
    }

    private fun setCurrentTextInfo(text: String) {
        currentFullBase64 = null
        copyBase64Button.isVisible = false
        currentBlobBytes = null
        currentBlobDefaultFileName = "blob.bin"
        saveBlobButton.isVisible = false
        saveBlobButton.text = SAVE_BLOB_TEXT
        dbValueField.text = text
        dbValueInfoLabel.text = "Length: ${text.length}"
    }

    private fun setCurrentBase64Info(blobBytes: ByteArray, base64Text: String) {
        currentFullBase64 = base64Text
        currentBlobBytes = blobBytes
        currentBlobDefaultFileName = "blob.bin"
        copyBase64Button.isVisible = base64Text.isNotEmpty()
        saveBlobButton.isVisible = blobBytes.isNotEmpty()
        saveBlobButton.text = SAVE_BLOB_TEXT
        if (base64Text.length <= BASE64_PREVIEW_MAX_LENGTH) {
            dbValueField.text = base64Text
            dbValueInfoLabel.text = "Base64 length: ${base64Text.length}"
            return
        }
        dbValueField.text = base64Text.take(BASE64_PREVIEW_MAX_LENGTH)
        dbValueInfoLabel.text = "Base64 length: ${base64Text.length} (preview: $BASE64_PREVIEW_MAX_LENGTH)"
    }

    private fun updateDataDisplayPanel(showImage: Boolean) {
        imageScrollContainer.isVisible = showImage
        textScrollContainer.isVisible = !showImage
    }

    private fun decodeImage(bytes: ByteArray): BufferedImage? {
        if (bytes.isEmpty()) {
            return null
        }
        return runCatching {
            ByteArrayInputStream(bytes).use { inputStream ->
                ImageIO.read(inputStream)
            }
        }.getOrNull()
    }

    private fun BufferedImage.getScaledPreviewImage(maxWidth: Int, maxHeight: Int): Image {
        val widthRatio = maxWidth.toDouble() / width
        val heightRatio = maxHeight.toDouble() / height
        val ratio = minOf(1.0, widthRatio, heightRatio)
        val targetWidth = (width * ratio).toInt().coerceAtLeast(1)
        val targetHeight = (height * ratio).toInt().coerceAtLeast(1)
        return getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH)
    }

    override fun tableFilterEditorCreated(
        header: TableFilterHeader,
        editor: IFilterEditor,
        tableColumn: TableColumn
    ) {
        val columnName = tableColumn.headerValue.toString()
        filterEditors[columnName] = editor
        val cachedFilter = filterHeaderCache[columnName] ?: return
        editor.content = cachedFilter
    }

    override fun tableFilterEditorExcluded(
        header: TableFilterHeader?,
        editor: IFilterEditor?,
        tableColumn: TableColumn?
    ) {
        tableColumn?.headerValue?.toString()?.let { filterEditors.remove(it) }
    }

    override fun tableFilterUpdated(header: TableFilterHeader,
                                    editor: IFilterEditor,
                                    tableColumn: TableColumn) {
        val columnName = tableColumn.headerValue.toString()
        val headerContent = editor.content.toString()
        filterHeaderCache[columnName] = headerContent
    }

    private fun resetAllFilters() {
        filterHeaderCache.clear()
        filterEditors.values.forEach { it.content = "" }
    }

    private fun saveCurrentBlobToLocal() {
        val blob = currentBlobBytes ?: return
        val chooser = JFileChooser().apply {
            selectedFile = java.io.File(currentBlobDefaultFileName)
        }
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return
        }
        runCatching {
            Files.write(chooser.selectedFile.toPath(), blob)
        }.onSuccess {
            JOptionPane.showMessageDialog(
                this,
                "Saved to: ${chooser.selectedFile.absolutePath}",
                "Save Successful",
                JOptionPane.INFORMATION_MESSAGE
            )
        }.onFailure {
            JOptionPane.showMessageDialog(this, "Failed to save BLOB: ${it.message}", "Save Error", JOptionPane.ERROR_MESSAGE)
        }
    }

    private fun ByteArray.guessImageDefaultFileName(): String? {
        if (isEmpty()) {
            return null
        }
        val formatName = runCatching {
            ByteArrayInputStream(this).use { input ->
                ImageIO.createImageInputStream(input).use { imageInput ->
                    val readers = ImageIO.getImageReaders(imageInput)
                    if (readers.hasNext()) readers.next().formatName else null
                }
            }
        }.getOrNull() ?: return null
        val extension = when (formatName.lowercase()) {
            "jpeg" -> "jpg"
            else -> formatName.lowercase()
        }
        return "image.$extension"
    }

    // UI configuration begin {@
    private fun setupUI() {
        rootPanel = JPanel()
        rootPanel.layout = GridLayoutManager(3, 1, Insets(0, 0, 0, 0),
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
        pageInputField = BeeplessFormattedTextView(NumberFormatter(NumberFormat.getIntegerInstance()).apply {
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
        resetFiltersButton = JButton()
        resetFiltersButton.text = "Reset Filters"
        toolbarContainer.add(resetFiltersButton)
        dataTable = JBTable()
        tableModel = DatabaseTableModel(emptyTablePage)
        dataTable.model = tableModel
        dataTable.autoCreateRowSorter = true
        dataTable.setDefaultRenderer(Any::class.java, DatabaseTableCellRenderer())
        tableFilterHeader.apply {
            this.table = dataTable
            this.autoChoices = AutoChoices.DISABLED
            this.isFilterOnUpdates = true
            this.isAdaptiveChoices = false
            this.isInstantFiltering = true
            this.isChoicesEnable = false
            this.addHeaderObserver(this@SqliteTablesWindow)
        }
        dataTable.tableHeader.reorderingAllowed = false
        dataTable.maximumSize = Dimension(40, 40)
        dataTable.fillsViewportHeight = true
        dataTable.autoResizeMode = JTable.AUTO_RESIZE_OFF
        val tableScrollPane = JBScrollPane()
        tableScrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        tableScrollPane.setViewportView(dataTable)
        tableContainerPanel = JPanel(CardLayout())
        tableContainerPanel.add(tableScrollPane, TABLE_CARD_DATA)
        tableLoadingPanel = JPanel(GridBagLayout()).apply {
            add(JLabel("Loading database..."))
        }
        tableContainerPanel.add(tableLoadingPanel, TABLE_CARD_LOADING)
        (tableContainerPanel.layout as CardLayout).show(tableContainerPanel, TABLE_CARD_LOADING)
        rootPanel.add(tableContainerPanel, GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_WANT_GROW,
            null, null, null, 0, true))

        bottomToolPanel = JPanel()
        bottomToolPanel.layout = BorderLayout(0, 0)
        rootPanel.add(bottomToolPanel, GridConstraints(2, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null, 0, false))
        dataHolderPanel = JPanel()
        dataHolderPanel.layout = CardLayout(0, 0)
        dataHolderPanel.preferredSize = Dimension(400, 180)
        bottomToolPanel.add(dataHolderPanel, BorderLayout.WEST)
        imageLabel = JLabel()
        imageLabel.isEnabled = true
        imageLabel.isVisible = true
        imageScrollContainer = JScrollPane(imageLabel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED).apply {
            preferredSize = Dimension(IMAGE_PREVIEW_MAX_WIDTH, IMAGE_PREVIEW_MAX_HEIGHT)
            minimumSize = Dimension(IMAGE_PREVIEW_MAX_WIDTH, IMAGE_PREVIEW_MAX_HEIGHT)
        }
        dataHolderPanel.add(imageScrollContainer, "Card1")
        dbValueField = JTextArea()
        dbValueField.isVisible = true
        dbValueField.lineWrap = true
        dbValueField.wrapStyleWord = false
        textScrollContainer = JScrollPane(
            dbValueField,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        ).apply {
            preferredSize = Dimension(400, IMAGE_PREVIEW_MAX_HEIGHT)
            minimumSize = Dimension(400, IMAGE_PREVIEW_MAX_HEIGHT)
        }
        dataHolderPanel.add(textScrollContainer, "Card2")
        bottomInfoPanel = JPanel()
        bottomInfoPanel.layout = FlowLayout(FlowLayout.LEFT, 5, 5)
        bottomToolPanel.add(bottomInfoPanel, BorderLayout.CENTER)
        dbValueInfoLabel = JLabel()
        bottomInfoPanel.add(dbValueInfoLabel)
        copyBase64Button = JButton(COPY_BASE64_TEXT).apply {
            isVisible = false
            addActionListener {
                val base64 = currentFullBase64 ?: return@addActionListener
                Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(base64), null)
            }
        }
        bottomInfoPanel.add(copyBase64Button)
        saveBlobButton = JButton(SAVE_BLOB_TEXT).apply {
            isVisible = false
        }
        bottomInfoPanel.add(saveBlobButton)

        layout = BorderLayout()
        add(rootPanel)
    }
    // @}
}
