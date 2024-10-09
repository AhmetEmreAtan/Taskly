package com.example.notesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.notesapp.dataClass.Note
import com.example.notesapp.databinding.FragmentAddNewNoteBinding
import com.example.notesapp.viewModel.NoteViewModel


class AddNewNoteFragment : Fragment() {

    private lateinit var binding: FragmentAddNewNoteBinding
    private val noteViewModel: NoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNewNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveNoteButton.setOnClickListener {
            val noteTitle = binding.newNoteTitle.text.toString().trim()
            val noteDetail = binding.newNoteDetail.text.toString().trim()

            if (noteTitle.isEmpty() || noteDetail.isEmpty()) {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            } else {
                val note = Note(title = noteTitle, detail = noteDetail)
                noteViewModel.insert(note)
                Toast.makeText(requireContext(), "Not başarıyla kaydedildi", Toast.LENGTH_SHORT).show()
                binding.newNoteTitle.text.clear()
                binding.newNoteDetail.text.clear()
            }
        }

    }
}
