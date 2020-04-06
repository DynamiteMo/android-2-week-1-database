package com.ucsdextandroid2.todoroom.ui.notes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.RecyclerView
import com.ucsdextandroid2.todoroom.R
import com.ucsdextandroid2.todoroom.database.NotesDao
import com.ucsdextandroid2.todoroom.databinding.NoteFragmentBinding
import com.ucsdextandroid2.todoroom.di.appDependencies
import com.ucsdextandroid2.todoroom.model.Note
import com.ucsdextandroid2.todoroom.util.FragmentNavDirections
import com.ucsdextandroid2.todoroom.util.injectViewModel
import kotlinx.coroutines.launch

/**
 * Created by rjaylward on 4/5/20
 */

class NoteFragment: Fragment() {

    companion object {
        private const val NOTE_EXTRA = "note_extra"

        fun createDirections(note: Note?): NavDirections {
            val bundle = Bundle()
            bundle.putParcelable(NOTE_EXTRA, note)

            return FragmentNavDirections(R.id.notesFragment, bundle)
        }
    }

    private lateinit var binding: NoteFragmentBinding

    private val originalNote: Note? by lazy {
        arguments?.getParcelable<Note?>(NOTE_EXTRA)
    }

    private val viewModel: NoteViewModel by injectViewModel {
        NoteViewModel(requireContext().appDependencies.database.noteDao())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, saveOnBackPressed)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = NoteFragmentBinding.inflate(layoutInflater, container, false)

        setUpToolbar(binding.toolbar)

        setUpColorPicker(binding.colorsRecyclerView)

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        if(originalNote != null) {
            binding.noteTitleView.setText(originalNote?.title)
            binding.noteTextView.setText(originalNote?.text)
        }

        return binding.root
    }

    private fun setUpColorPicker(recyclerView: RecyclerView) {
        //TODO add a color adapter and bind the color recycler view
        // create adapter
        // add layout manger
        // set items to the adapter
    }

    private fun setUpToolbar(toolbar: Toolbar) {
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
    }

    private val saveOnBackPressed: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            //save the current note
            saveNote()

            //now that we did our work we have to disable the callback and and call back pressed again
            //so it doesn't just keep trying to save the note and never goes back
            isEnabled = false
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun saveNote() {
        if(binding.noteTitleView.text.isNotEmpty() || binding.noteTextView.text.isNotEmpty()) {
            val note = Note(
                    binding.noteTitleView.text.toString(),
                    binding.noteTextView.text.toString(),
                    originalNote?.createdAt ?: System.currentTimeMillis(),
                    originalNote?.imageUri
            )

            viewModel.insertNote(note)
        }
    }

}

class NoteViewModel(private val notesDao: NotesDao) : ViewModel() {

    fun insertNote(note: Note) {
        viewModelScope.launch {
            notesDao.insertNote(note)
        }
    }

}