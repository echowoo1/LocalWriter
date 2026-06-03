package com.example.localwriter

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.Date

class DocumentAdapter(
    private val context: Context,
    private val documents: List<Document>
) : BaseAdapter() {
    
    override fun getCount(): Int = documents.size
    
    override fun getItem(position: Int): Any = documents[position]
    
    override fun getItemId(position: Int): Long = documents[position].id
    
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
           .inflate(R.layout.item_document, parent, false)
        
        val document = documents[position]
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvPreview = view.findViewById<TextView>(R.id.tvPreview)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val tvStats = view.findViewById<TextView>(R.id.tvStats)
        
        tvTitle.text = document.title
        
        val preview = document.content.take(100).ifEmpty { "空文档" }
        tvPreview.text = preview
        
        val date = Date(document.updatedAt)
        val dateStr = DateFormat.format("yyyy/MM/dd HH:mm", date).toString()
        tvDate.text = dateStr
        
        val charCount = document.content.length
        val wordCount = if (document.content.isEmpty()) 0 
            else document.content.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
        tvStats.text = "字符: $charCount | 字数: $wordCount"
        
        return view
    }
}
