package com.example.notesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.adapter.NoteAdapter
import com.example.notesapp.viewModel.NoteViewModel

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val noteViewModel: NoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.home_note_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteViewModel.allNotes.observe(viewLifecycleOwner) { notes ->
            val adapter = NoteAdapter(notes, { selectedNote ->
                // NoteDetailFragment'a geçiş
                val noteDetailFragment = NoteDetailFragment.newInstance(selectedNote)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frameLayout, noteDetailFragment)
                    .addToBackStack(null)
                    .commit()
            }, { longClickedNote ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Note")
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton("Yes") { _, _ ->
                        noteViewModel.delete(longClickedNote)
                        Toast.makeText(requireContext(), "Note deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        // "No" seçilirse sadece dialog kapanacak
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            })
            recyclerView.adapter = adapter
        }


    }
}
