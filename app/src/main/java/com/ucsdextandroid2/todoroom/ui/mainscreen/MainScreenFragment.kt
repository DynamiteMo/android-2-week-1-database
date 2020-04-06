package com.ucsdextandroid2.todoroom.ui.mainscreen

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ucsdextandroid2.todoroom.R
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

    lateinit var binding: NotesListFragmentBinding

    private val viewModel: MainScreenViewModel by injectViewModel {
        MainScreenViewModel(requireContext().appDependencies.database.noteDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.notes_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.notesListMenuChangeView) {
            if(binding.recyclerView.layoutManager is LinearLayoutManager)
                binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            else
                binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {
        //this creates the layout from the xml file. The name of the class is the same as the xml file
        binding = NotesListFragmentBinding.inflate(layoutInflater, container, false)

        setUpToolbar(binding.toolbar)

        //steps for a recycler view
        // 1. create the xml for your list item view
        // 2. create an adapter
        // 3. create the layout manager
        // 4. add a method to update the list of items in your adapter
        // 5. subscribe to updates from your view model when the items list changes
        val notesAdapter = NotesAdapter()

        binding.recyclerView.adapter = notesAdapter

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

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
            notesAdapter.submitList(notes)
        })

        //here we return the root of the layout
        return binding.root
    }

    private fun setUpToolbar(toolbar: Toolbar) {
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
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