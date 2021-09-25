package com.snapnoob.notes.feature.edit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.snapnoob.notes.databinding.ActivityEditBinding
import com.snapnoob.notes.network.model.Notes
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.ignoreIoExceptions

@AndroidEntryPoint
class EditNotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBinding
    private lateinit var view: View

    private lateinit var viewModel: EditNotesViewModel

    private var notesObject: Notes? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this)[EditNotesViewModel::class.java]
        observeViewModel()

        if (intent.hasExtra(NOTES_OBJECT)) {
            notesObject = intent.getParcelableExtra(NOTES_OBJECT)
            setNotesToView()
            binding.toolBar.title = "Edit Notes"
            binding.btnDelete.visibility = View.VISIBLE
            binding.btnCreate.text = "Edit"
        } else {
            binding.toolBar.title = "Create Notes"
            binding.btnDelete.visibility = View.GONE
            binding.btnCreate.text = "Create"
        }
        initView()
    }

    private fun initView() {
        binding.toolBar.setNavigationOnClickListener { finish() }
        binding.btnCreate.setOnClickListener {
            if (notesObject != null) {
                val notes = createNotesFromInput(notesObject!!.id)
                if (notes != null) {
                    viewModel.updateNotes(notes)
                }
            } else {
                val notes = createNotesFromInput()
                if (notes != null) {
                    viewModel.createNotes(notes)
                }
            }
        }
        binding.btnDelete.setOnClickListener {
            notesObject?.let {
                if (it.id != null) {
                    viewModel.deleteNotes(it.id)
                }
            }
        }
    }

    private fun createNotesFromInput(id: Long? = 0): Notes? {
        val title = binding.tvTitle.text.toString()
        val notes = binding.tvNotes.text.toString()

        return if (title.isEmpty() || notes.isEmpty()) {
            Snackbar.make(view, "Title and Notes are required", Snackbar.LENGTH_LONG).show()
            null
        } else {
            Notes(
                id = id,
                title = binding.tvTitle.text.toString(),
                notes = binding.tvNotes.text.toString()
            )
        }
    }

    private fun observeViewModel() {
        viewModel.eventLiveData.observe(this, this::handleEvent)
    }

    private fun handleEvent(event: EditNotesEvent) {
        when (event) {
            is EditNotesEvent.ProcessSuccess -> {
                Toast.makeText(this, "Process success", Toast.LENGTH_LONG).show()
                finish()
            }
            is EditNotesEvent.ShowError -> Snackbar.make(view, event.error, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun setNotesToView() {
        binding.tvTitle.setText(notesObject!!.title)
        binding.tvNotes.setText(notesObject!!.notes)
    }

    companion object {
        const val NOTES_OBJECT = "notes_object"
    }
}