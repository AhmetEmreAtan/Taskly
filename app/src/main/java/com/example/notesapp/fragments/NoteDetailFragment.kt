package com.example.notesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.notesapp.R
import com.example.notesapp.dataClass.Note
import com.example.notesapp.viewModel.NoteViewModel

class NoteDetailFragment : Fragment() {

    private val noteViewModel: NoteViewModel by viewModels()
    private var currentNote: Note? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_note_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleEditText: EditText = view.findViewById(R.id.edit_note_title)
        val detailEditText: EditText = view.findViewById(R.id.edit_note_detail)
        val saveButton: Button = view.findViewById(R.id.save_note_button)

        // Note bilgilerini al ve EditText'lere yerleştir
        currentNote = arguments?.getParcelable("selected_note")
        titleEditText.setText(currentNote?.title)
        detailEditText.setText(currentNote?.detail)

        // Kaydet butonuna tıklandığında notu güncelle
        saveButton.setOnClickListener {
            val updatedTitle = titleEditText.text.toString()
            val updatedDetail = detailEditText.text.toString()

            if (updatedTitle.isEmpty() || updatedDetail.isEmpty()) {
                Toast.makeText(requireContext(), "Title and detail cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                val updatedNote = currentNote?.copy(title = updatedTitle, detail = updatedDetail)
                if (updatedNote != null) {
                    noteViewModel.update(updatedNote)
                    Toast.makeText(requireContext(), "Note updated successfully", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack() // Geriye dön
                }
            }
        }
    }

    companion object {
        fun newInstance(note: Note): NoteDetailFragment {
            val fragment = NoteDetailFragment()
            val args = Bundle()
            args.putParcelable("selected_note", note)
            fragment.arguments = args
            return fragment
        }
    }
}

