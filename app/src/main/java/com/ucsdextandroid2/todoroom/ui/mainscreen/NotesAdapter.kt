package com.ucsdextandroid2.todoroom.ui.mainscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ucsdextandroid2.todoroom.databinding.NoteViewBinding
import com.ucsdextandroid2.todoroom.model.Note

class NotesAdapter : RecyclerView.Adapter<NoteViewHolder>() {

    private var noteList: List<Note> = listOf()

    fun submitList(listNotes: List<Note>) {
        noteList = listNotes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(NoteViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = noteList[position]
        holder.binding.titleTextView.text = note.title
        holder.binding.noteTextView.text = note.text
    }
}