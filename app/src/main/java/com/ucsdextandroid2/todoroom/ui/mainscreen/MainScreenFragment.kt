package com.ucsdextandroid2.todoroom.ui.mainscreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.ucsdextandroid2.todoroom.database.NotesDao
import com.ucsdextandroid2.todoroom.databinding.NotesListFragmentBinding
import com.ucsdextandroid2.todoroom.di.appDependencies
import com.ucsdextandroid2.todoroom.model.Note
import com.ucsdextandroid2.todoroom.ui.notes.NoteFragment
import com.ucsdextandroid2.todoroom.util.injectViewModel
import kotlinx.coroutines.launch

class MainScreenFragment: Fragment() {

    companion object {
        fun newInstance() = MainScreenFragment()
    }

    private val viewModel: MainScreenViewModel by injectViewModel {
        MainScreenViewModel(requireContext().appDependencies.database.noteDao())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {

        //this creates the layout from the xml file. The name of the class is the same as the xml file
        val binding = NotesListFragmentBinding.inflate(layoutInflater, container, false)

        //steps for a recycler view
        // 1. create the xml for your list item view
        // 2. create an adapter
        // 3. create the layout manager
        // 4. add a method to update the list of items in your adapter
        // 5. subscribe to updates from your view model when the items list changes

        binding.bottomTextView.setOnClickListener {
            findNavController().navigate(NoteFragment.createDirections(null))
        }

        //here we watch to database for changes in the notes and reload the list for every change
        viewModel.notes.observe(viewLifecycleOwner, Observer { notes ->
            //log them just so we can see them in the Logcat view in android studio
            notes.forEach {
                Log.d("MainActivity", "${it.title} ${it.text}")
            }

            //show a message on screen with the total number of notes
            Toast.makeText(requireContext(), "Total Number of Notes: ${notes.size}", Toast.LENGTH_SHORT).show()

            //add all the notes to the recycler view
            //TODO add the items to the list
        })

        //here we return the root of the layout
        return binding.root
    }

}

class MainScreenViewModel(private val notesDao: NotesDao) : ViewModel() {

    val notes: LiveData<List<Note>> = notesDao.getAllNotesFlow()
            .asLiveData(viewModelScope.coroutineContext)

    fun removeNote(note: Note) {
        viewModelScope.launch {
            notesDao.deleteNoteAsync(note)
        }
    }

}