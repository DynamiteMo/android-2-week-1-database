package com.ucsdextandroid2.todoroom.ui.mainscreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ucsdextandroid2.todoroom.database.NotesDao
import com.ucsdextandroid2.todoroom.databinding.NotesListFragmentBinding
import com.ucsdextandroid2.todoroom.databinding.ViewHolderNoteCardBinding
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

        val adapter = NotesAdapter()
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        binding.recyclerView.adapter = adapter

        adapter.onNoteClickListener = { note ->
            findNavController().navigate(NoteFragment.createDirections(note))
        }

        binding.bottomTextView.setOnClickListener {
            findNavController().navigate(NoteFragment.createDirections(null))
        }

        enableSwipeToDelete(binding.recyclerView, adapter)
        enableScrollToTopWhenItemAddedToTopOfList(binding.recyclerView, adapter)

        //here we watch to database for changes in the notes and reload the list for every change
        viewModel.notes.observe(viewLifecycleOwner, Observer { notes ->
            //log them just so we can see them in the Logcat view in android studio
            notes.forEach {
                Log.d("MainActivity", "${it.title} ${it.text}")
            }

            //show a message on screen with the total number of notes
            Toast.makeText(requireContext(), "Total Number of Notes: ${notes.size}", Toast.LENGTH_SHORT).show()

            //add all the notes to the recycler view
            adapter.submitList(notes)
        })

        //here we return the root of the layout
        return binding.root
    }

    private fun enableScrollToTopWhenItemAddedToTopOfList(recyclerView: RecyclerView, adapter: NotesAdapter) {
        adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)

                if(positionStart == 0)
                    recyclerView.layoutManager?.scrollToPosition(0)
            }

        })
    }

    private fun enableSwipeToDelete(recyclerView: RecyclerView, adapter: NotesAdapter) {
        val itemTouchHelper = ItemTouchHelper(
                object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START or ItemTouchHelper.END) {

                    override fun onMove(recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val note = adapter.removeItem(viewHolder.adapterPosition)
                        if(note != null)
                            viewModel.removeNote(note)
                    }

                }
        )

        itemTouchHelper.attachToRecyclerView(recyclerView)
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

private class NotesAdapter : ListAdapter<Note, NoteCardViewHolder>(NOTE_DIFF) {

    var onNoteClickListener: ((Note) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteCardViewHolder {
        return NoteCardViewHolder.inflate(parent).apply {
            itemView.setOnClickListener {
                val note = getItem(adapterPosition)
                if(note != null) {
                    onNoteClickListener?.invoke(note)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: NoteCardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun removeItem(position: Int): Note? = getItem(position)

    companion object {

        /**
         * This is just used to compare two different objects to determine if the recycler view
         * view holder needs to be updated or not. The first function
         **/
        private val NOTE_DIFF: DiffUtil.ItemCallback<Note> = object : DiffUtil.ItemCallback<Note>() {

            /**
             * This function is used to tell if a new item added to the list is a new item or just
             * an update to an existing item. If the item is new [RecyclerView.Adapter.notifyItemInserted]
             * or [RecyclerView.Adapter.notifyItemMoved] will be called and the recycler view will
             * show the insert or move animations.
             *
             * @return true if items have the same unique identifier
             **/
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.createdAt == newItem.createdAt
            }

            /**
             * This function is called if [DiffUtil.ItemCallback.areItemsTheSame] returns true. Once
             * we know that a new item added to the list is an updated version of an old item this
             * function is used to determine if that item has visually changed. If the object is
             * equal to the existing object then nothing has changed and the view wont update. If
             * something has changed [RecyclerView.Adapter.notifyItemChanged] will tell the
             * RecyclerView to call [RecyclerView.Adapter.onBindViewHolder] again to rebind the view
             * with the new data.
             *
             * @return true if items have the same unique identifier
             **/
            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem == newItem
            }

        }
    }

}

private class NoteCardViewHolder private constructor(val binding: ViewHolderNoteCardBinding) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun inflate(parent: ViewGroup) = NoteCardViewHolder(
                ViewHolderNoteCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    fun bind(note: Note?) {
        if(note != null) {
            binding.noteCardTitleView.text = note.title
            binding.noteCardTextView.text = note.text

            if(note.imageUri != null) {
                binding.noteCardImage.isVisible = true
                binding.noteCardImage.setImageURI(note.imageUri)
            } else {
                binding.noteCardImage.isVisible = false
            }
        } else {
            binding.noteCardTextView.text = ""
            binding.noteCardTitleView.text = ""
            binding.noteCardImage.isVisible = false
        }
    }

}