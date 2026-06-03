package com.example.localwriter

import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class DocumentListActivity : AppCompatActivity() {
    
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var adapter: DocumentAdapter
    private lateinit var toolbar: MaterialToolbar
    private lateinit var fabNewDocument: FloatingActionButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_list)
        
        initViews()
        setupToolbar()
        loadDocuments()
        setupListView()
        setupFab()
    }
    
    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        listView = findViewById(R.id.listView)
        fabNewDocument = findViewById(R.id.fabNewDocument)
        databaseHelper = DatabaseHelper(this)
    }
    
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "本地写作 - 文档列表"
    }
    
    private fun loadDocuments() {
        val documents = databaseHelper.getAllDocuments()
        adapter = DocumentAdapter(this, documents)
        listView.adapter = adapter
    }
    
    private fun setupListView() {
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val document = adapter.getItem(position) as Document
            openDocument(document.id)
        }
        
        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, view, position, _ ->
            showDocumentMenu(view, position)
            true
        }
    }
    
    private fun setupFab() {
        fabNewDocument.setOnClickListener {
            createNewDocument()
        }
    }
    
    private fun openDocument(documentId: Long) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("document_id", documentId)
        startActivity(intent)
    }
    
    private fun createNewDocument() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_document, null)
        val etTitle = dialogView.findViewById<TextInputEditText>(R.id.etDocumentTitle)
        
        AlertDialog.Builder(this)
            .setTitle("新建文档")
            .setView(dialogView)
            .setPositiveButton("创建") { _, _ ->
                val title = etTitle.text.toString().ifEmpty { "未命名文档" }
                val id = databaseHelper.createDocument(title)
                if (id > 0) {
                    loadDocuments()
                    openDocument(id)
                } else {
                    Toast.makeText(this, "创建失败", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showDocumentMenu(view: View, position: Int) {
        val document = adapter.getItem(position) as Document
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.document_menu, popup.menu)
        
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_rename -> {
                    renameDocument(document)
                    true
                }
                R.id.action_delete -> {
                    deleteDocument(document)
                    true
                }
                else -> false
            }
        }
        
        popup.show()
    }
    
    private fun renameDocument(document: Document) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_document, null)
        val etTitle = dialogView.findViewById<TextInputEditText>(R.id.etDocumentTitle)
        etTitle.setText(document.title)
        
        AlertDialog.Builder(this)
            .setTitle("重命名文档")
            .setView(dialogView)
            .setPositiveButton("确定") { _, _ ->
                val newTitle = etTitle.text.toString().ifEmpty { "未命名文档" }
                databaseHelper.updateDocumentTitle(document.id, newTitle)
                loadDocuments()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun deleteDocument(document: Document) {
        AlertDialog.Builder(this)
            .setTitle("删除文档")
            .setMessage("确定要删除「${document.title}」吗？")
            .setPositiveButton("删除") { _, _ ->
                databaseHelper.deleteDocument(document.id)
                loadDocuments()
                Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    override fun onResume() {
        super.onResume()
        loadDocuments()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // TODO: 打开设置
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
