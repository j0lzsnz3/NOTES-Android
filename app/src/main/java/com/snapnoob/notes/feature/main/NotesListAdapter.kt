package com.snapnoob.notes.feature.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.snapnoob.notes.databinding.ViewNotesBinding
import com.snapnoob.notes.network.model.Notes

class NotesListAdapter(
    private val setOnClickListener: (Notes) -> Unit
) : RecyclerView.Adapter<NotesListAdapter.ViewHolder>() {

    private var notesList: List<Notes> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Notes>) {
        this.notesList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewNotesBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding, setOnClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notesList[position])
    }

    override fun getItemCount(): Int = notesList.size

    inner class ViewHolder(
        private val binding: ViewNotesBinding,
        private val setOnClickListener: (Notes) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notes: Notes) {
            val view = binding.root
            view.setOnClickListener { setOnClickListener.invoke(notes) }
            binding.tvTitle.text = notes.title
            binding.tvNotes.text = notes.notes
        }
    }

}