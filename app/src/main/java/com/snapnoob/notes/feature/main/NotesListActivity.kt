package com.snapnoob.notes.feature.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.snapnoob.notes.R
import com.snapnoob.notes.databinding.ActivityNotesListBinding
import com.snapnoob.notes.feature.edit.EditNotesActivity
import com.snapnoob.notes.feature.edit.EditNotesEvent
import com.snapnoob.notes.network.model.Notes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotesListBinding
    private lateinit var view: View

    private lateinit var viewModel: NotesListViewModel

    private val notesListAdapter by lazy {
        NotesListAdapter {
            openEditNoteActivity(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesListBinding.inflate(layoutInflater)
        view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this)[NotesListViewModel::class.java]
        observeViewModel()
        initView()
        viewModel.getAllNotes()
    }

    private fun initView() {
        binding.swipeRefresh.setOnRefreshListener { viewModel.getAllNotes() }
        binding.rvNotesList.apply {
            layoutManager = LinearLayoutManager(this@NotesListActivity, LinearLayoutManager.VERTICAL, false)
            adapter = notesListAdapter
        }
        binding.toolBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menuCreate -> openEditNoteActivity(null)
            }
            true
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getAllNotes()
        }

    }

    private fun observeViewModel() {
        viewModel.notesListLiveData.observe(this, { listNotes ->
            if (listNotes.isNotEmpty()) {
                binding.rvNotesList.visibility = View.VISIBLE
                notesListAdapter.setData(listNotes)
                binding.viewNoData.root.visibility = View.GONE
            } else {
                binding.rvNotesList.visibility = View.GONE
                binding.viewNoData.root.visibility = View.VISIBLE
            }
            binding.swipeRefresh.isRefreshing = false
        })
        viewModel.eventLiveData.observe(this, this::handleEvent)
    }

    private fun handleEvent(event: NotesListEvent) {
        when (event) {
            is NotesListEvent.ShowError -> {
                Snackbar.make(view, event.error, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun openEditNoteActivity(notes: Notes?) {
        val intent = Intent(this, EditNotesActivity::class.java)
        if (notes != null) {
            intent.putExtra(EditNotesActivity.NOTES_OBJECT, notes)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        binding.swipeRefresh.isRefreshing = true
        viewModel.getAllNotes()
    }
}