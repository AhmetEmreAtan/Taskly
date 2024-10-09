package com.example.notesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.dataClass.Note

class NoteAdapter(
    private val noteList: List<Note>,
    private val onItemClick: (Note) -> Unit,
    private val onItemLongClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.note_title)
        val detailTextView: TextView = itemView.findViewById(R.id.note_detail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = noteList[position]
        holder.titleTextView.text = currentNote.title
        holder.detailTextView.text = currentNote.detail

        holder.itemView.setOnClickListener {
            onItemClick(currentNote)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClick(currentNote)
            true
        }
    }

    override fun getItemCount(): Int {
        return noteList.size
    }
}
