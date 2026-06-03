package com.example.localwriter

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.Date

data class Document(
    val id: Long = -1,
    val title: String = "",
    val content: String = "",
    val fontSize: Float = 16f,
    val lineSpacing: Float = 1.2f,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    
    companion object {
        private const val DATABASE_NAME = "localwriter.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_DOCUMENTS = "documents"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_FONT_SIZE = "font_size"
        const val COLUMN_LINE_SPACING = "line_spacing"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_UPDATED_AT = "updated_at"
    }
    
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_DOCUMENTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_CONTENT TEXT,
                $COLUMN_FONT_SIZE REAL DEFAULT 16,
                $COLUMN_LINE_SPACING REAL DEFAULT 1.2,
                $COLUMN_CREATED_AT INTEGER NOT NULL,
                $COLUMN_UPDATED_AT INTEGER NOT NULL
            )
        """
        db.execSQL(createTable)
        
        // 创建默认文档
        val values = ContentValues().apply {
            put(COLUMN_TITLE, "未命名文档")
            put(COLUMN_CONTENT, "")
            put(COLUMN_FONT_SIZE, 16f)
            put(COLUMN_LINE_SPACING, 1.2f)
            put(COLUMN_CREATED_AT, System.currentTimeMillis())
            put(COLUMN_UPDATED_AT, System.currentTimeMillis())
        }
        db.insert(TABLE_DOCUMENTS, null, values)
    }
    
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DOCUMENTS")
        onCreate(db)
    }
    
    fun createDocument(title: String = "未命名文档"): Long {
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_CONTENT, "")
            put(COLUMN_FONT_SIZE, 16f)
            put(COLUMN_LINE_SPACING, 1.2f)
            put(COLUMN_CREATED_AT, System.currentTimeMillis())
            put(COLUMN_UPDATED_AT, System.currentTimeMillis())
        }
        return writableDatabase.insert(TABLE_DOCUMENTS, null, values)
    }
    
    fun getAllDocuments(): List<Document> {
        val documents = mutableListOf<Document>()
        val cursor = readableDatabase.query(
            TABLE_DOCUMENTS,
            null, null, null, null, null,
            "$COLUMN_UPDATED_AT DESC"
       )
        
        cursor.use {
            while (it.moveToNext()) {
                documents.add(cursorToDocument(it))
            }
        }
        return documents
    }
    
    fun getDocument(id: Long): Document? {
        val cursor = readableDatabase.query(
            TABLE_DOCUMENTS,
            null, "$COLUMN_ID = ?", arrayOf(id.toString()),
            null, null, null
       )
        
        cursor.use {
            if (it.moveToFirst()) {
                return cursorToDocument(it)
            }
        }
        return null
    }
    
    fun updateDocument(document: Document) {
        val values = ContentValues().apply {
            put(COLUMN_TITLE, document.title)
            put(COLUMN_CONTENT, document.content)
            put(COLUMN_FONT_SIZE, document.fontSize)
            put(COLUMN_LINE_SPACING, document.lineSpacing)
            put(COLUMN_UPDATED_AT, System.currentTimeMillis())
        }
        writableDatabase.update(
            TABLE_DOCUMENTS, values,
            "$COLUMN_ID = ?", arrayOf(document.id.toString())
       )
    }
    
    fun deleteDocument(id: Long) {
        writableDatabase.delete(
            TABLE_DOCUMENTS,
            "$COLUMN_ID = ?", arrayOf(id.toString())
       )
    }
    
    fun updateDocumentTitle(id: Long, title: String) {
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_UPDATED_AT, System.currentTimeMillis())
        }
        writableDatabase.update(
            TABLE_DOCUMENTS, values,
            "$COLUMN_ID = ?", arrayOf(id.toString())
       )
    }
    
    private fun cursorToDocument(cursor: Cursor): Document {
        return Document(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
            content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
            fontSize = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_FONT_SIZE)),
            lineSpacing = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_LINE_SPACING)),
            createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)),
            updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT))
       )
    }
}
