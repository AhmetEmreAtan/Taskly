package com.example.notesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.notesapp.R
import com.example.notesapp.dataClass.Todo
import com.example.notesapp.viewModel.TodoViewModel

class TodoDetailFragment : Fragment() {

    private lateinit var todoViewModel: TodoViewModel
    private lateinit var subTitleEditText: EditText
    private lateinit var notesEditText: EditText
    private var todoId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_todo_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subTitleEditText = view.findViewById(R.id.subTitleEditText)
        notesEditText = view.findViewById(R.id.notesEditText)

        // ViewModel'i al
        todoViewModel = ViewModelProvider(this).get(TodoViewModel::class.java)

        // HomeFragment'ten gelen todoId'yi al
        todoId = arguments?.getInt("todoId")


        todoId?.let { id ->
            todoViewModel.getTodoById(id).observe(viewLifecycleOwner) { todo ->
                todo?.let {
                    subTitleEditText.setText(it.title)
                    notesEditText.setText(it.notes)
                }
            }
        }

        // Save button click handling
        view.findViewById<Button>(R.id.saveChangesButton).setOnClickListener {
            saveTodoChanges()
        }
    }

    private fun saveTodoChanges() {
        val updatedTitle = subTitleEditText.text.toString()
        val updatedNotes = notesEditText.text.toString()

        if (todoId != null) {
            todoViewModel.updateTodo(
                Todo(
                    id = todoId!!,
                    title = updatedTitle,
                    notes = updatedNotes
                )
            )
            Toast.makeText(requireContext(), "Todo updated", Toast.LENGTH_SHORT).show()

            // Geri dön
            parentFragmentManager.popBackStack()
        } else {
            Toast.makeText(requireContext(), "Error: Todo not found", Toast.LENGTH_SHORT).show()
        }
    }
}