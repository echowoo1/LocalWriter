package com.example.localwriter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    
    private lateinit var editText: EditText
    private lateinit var tvCharCount: TextView
    private lateinit var tvWordCount: TextView
    private lateinit var tvLineCount: TextView
    private lateinit var sliderFontSize: Slider
    private lateinit var sliderLineSpacing: Slider
    private lateinit var tvFontSizeValue: TextView
    private lateinit var tvLineSpacingValue: TextView
    private lateinit var toolbar: MaterialToolbar
    
    private lateinit var databaseHelper: DatabaseHelper
    private var currentDocumentId: Long = -1
    private var isDarkMode = false
    
    private val PREFS_NAME = "LocalWriterPrefs"
    private val KEY_DARK_MODE = "dark_mode"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // 应用深色模式设置
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupToolbar()
        
        databaseHelper = DatabaseHelper(this)
        
        // 获取文档ID
        currentDocumentId = intent.getLongExtra("document_id", -1)
        if (currentDocumentId == -1L) {
            // 如果没有指定文档ID，打开第一个文档
            val documents = databaseHelper.getAllDocuments()
            if (documents.isNotEmpty()) {
                currentDocumentId = documents[0].id
            } else {
                currentDocumentId = databaseHelper.createDocument("未命名文档")
            }
        }
        
        loadDocument()
        setupTextWatcher()
        setupSliders()
        updateDarkModeMenu()
    }
    
    private fun initViews() {
        editText = findViewById(R.id.editText)
        tvCharCount = findViewById(R.id.tvCharCount)
        tvWordCount = findViewById(R.id.tvWordCount)
        tvLineCount = findViewById(R.id.tvLineCount)
        sliderFontSize = findViewById(R.id.sliderFontSize)
        sliderLineSpacing = findViewById(R.id.sliderLineSpacing)
        tvFontSizeValue = findViewById(R.id.tvFontSizeValue)
        tvLineSpacingValue = findViewById(R.id.tvLineSpacingValue)
        toolbar = findViewById(R.id.toolbar)
    }
    
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        updateToolbarTitle()
    }
    
    private fun updateToolbarTitle() {
        val document = databaseHelper.getDocument(currentDocumentId)
        supportActionBar?.title = document?.title ?: "本地写作"
    }
    
    private fun loadDocument() {
        val document = databaseHelper.getDocument(currentDocumentId)
        document?.let {
            editText.setText(it.content)
            sliderFontSize.value = it.fontSize
            sliderLineSpacing.value = it.lineSpacing
            
            updateFontSize(it.fontSize)
            updateLineSpacing(it.lineSpacing)
            updateSettingsDisplay(it.fontSize, it.lineSpacing)
            updateWordCount(it.content)
        }
    }
    
    private fun setupSliders() {
        sliderFontSize.addOnChangeListener { _, value, _ ->
            updateFontSize(value)
            updateSettingsDisplay(value, sliderLineSpacing.value)
            saveDocument()
        }
        
        sliderLineSpacing.addOnChangeListener { _, value, _ ->
            updateLineSpacing(value)
            updateSettingsDisplay(sliderFontSize.value, value)
            saveDocument()
        }
    }
    
    private fun updateFontSize(size: Float) {
        editText.textSize = size
    }
    
    private fun updateLineSpacing(spacing: Float) {
        editText.setLineSpacing(spacing * editText.textSize, spacing)
    }
    
    private fun updateSettingsDisplay(fontSize: Float, lineSpacing: Float) {
        tvFontSizeValue.text = "字体大小: ${fontSize.toInt()}sp"
        tvLineSpacingValue.text = "行间距: ${String.format("%.1f", lineSpacing)}x"
    }
    
    private fun setupTextWatcher() {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                updateWordCount(s?.toString() ?: "")
                saveDocument()
            }
        })
    }
    
    private fun updateWordCount(text: String) {
        val charCount = text.length
        val wordCount = if (text.isEmpty()) 0 else text.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
        val lineCount = editText.lineCount
        
        tvCharCount.text = "字符: $charCount"
        tvWordCount.text = "字数: $wordCount"
        tvLineCount.text = "行数: $lineCount"
    }
    
    private fun saveDocument() {
        val document = databaseHelper.getDocument(currentDocumentId)
        document?.let {
            val updatedDocument = it.copy(
                content = editText.text.toString(),
                fontSize = sliderFontSize.value,
                lineSpacing = sliderLineSpacing.value,
                updatedAt = System.currentTimeMillis()
           )
            databaseHelper.updateDocument(updatedDocument)
        }
    }
    
    private fun updateDocumentTitle(title: String) {
        databaseHelper.updateDocumentTitle(currentDocumentId, title)
        updateToolbarTitle()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        
        // 更新深色模式菜单项的选中状态
        val darkModeItem = menu.findItem(R.id.action_dark_mode)
        darkModeItem?.isChecked = isDarkMode
        
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                showSearchDialog()
                true
            }
            R.id.action_export_txt -> {
                exportAsTxt()
                true
            }
            R.id.action_export_pdf -> {
                exportAsPdf()
                true
            }
            R.id.action_dark_mode -> {
                toggleDarkMode()
                true
            }
            R.id.action_document_list -> {
                val intent = Intent(this, DocumentListActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun showSearchDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_search, null)
        val etSearch = dialogView.findViewById<EditText>(R.id.etSearch)
        
        MaterialAlertDialogBuilder(this)
            .setTitle("查找")
            .setView(dialogView)
            .setPositiveButton("查找") { _, _ ->
                val searchText = etSearch.text.toString()
                if (searchText.isNotEmpty()) {
                    searchInText(searchText)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun searchInText(searchText: String) {
        val content = editText.text.toString()
        val index = content.indexOf(searchText)
        
        if (index >= 0) {
            editText.requestFocus()
            editText.setSelection(index, index + searchText.length)
        } else {
            MaterialAlertDialogBuilder(this)
                .setTitle("查找结果")
                .setMessage("未找到「$searchText」")
                .setPositiveButton("确定", null)
                .show()
        }
    }
    
    private fun exportAsTxt() {
        val document = databaseHelper.getDocument(currentDocumentId)
        document?.let {
            try {
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "${it.title}_$timestamp.txt"
                
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)
                
                FileOutputStream(file).use { fos ->
                    fos.write(it.content.toByteArray(Charsets.UTF_8))
                }
                
                MaterialAlertDialogBuilder(this)
                    .setTitle("导出成功")
                    .setMessage("已导出到下载目录：\n$fileName")
                    .setPositiveButton("确定", null)
                    .show()
            } catch (e: Exception) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("导出失败")
                    .setMessage(e.message)
                    .setPositiveButton("确定", null)
                    .show()
            }
        }
    }
    
    private fun exportAsPdf() {
        val document = databaseHelper.getDocument(currentDocumentId)
        document?.let {
            try {
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "${it.title}_$timestamp.pdf"
                
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)
                
                // 简单的PDF生成（实际使用建议使用iText等库）
                MaterialAlertDialogBuilder(this)
                    .setTitle("导出PDF")
                    .setMessage("PDF导出功能需要添加iText等PDF库。\n\n当前已导出为TXT格式。")
                    .setPositiveButton("确定", null)
                    .show()
                
                // 临时方案：导出为TXT
                exportAsTxt()
            } catch (e: Exception) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("导出失败")
                    .setMessage(e.message)
                    .setPositiveButton("确定", null)
                    .show()
            }
        }
    }
    
    private fun toggleDarkMode() {
        isDarkMode = !isDarkMode
        
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DARK_MODE, isDarkMode).apply()
        
        // 重建Activity以应用主题更改
        recreate()
    }
    
    private fun updateDarkModeMenu() {
        // 菜单项的状态会在onCreateOptionsMenu中更新
    }
    
    override fun onPause() {
        super.onPause()
        saveDocument()
    }
    
    override fun onResume() {
        super.onResume()
        loadDocument()
    }
}
