package com.example.notesapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.notesapp.R
import com.example.notesapp.dataClass.Todo
import com.example.notesapp.viewModel.TodoViewModel

class TodoDetailFragment : Fragment() {

    private lateinit var todoViewModel: TodoViewModel
    private lateinit var subTitleEditText: EditText
    private lateinit var notesEditText: EditText
    private lateinit var selectedDateText: TextView
    private lateinit var selectedTimeText: TextView
    private lateinit var todayButton: Button
    private lateinit var tomorrowButton: Button
    private lateinit var anotherDayButton: Button
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
        selectedDateText = view.findViewById(R.id.selectedDateText)
        selectedTimeText = view.findViewById(R.id.selectedTimeText)
        todayButton = view.findViewById(R.id.todayButton)
        tomorrowButton = view.findViewById(R.id.tomorrowButton)
        anotherDayButton = view.findViewById(R.id.anotherDayButton)


        todoViewModel = ViewModelProvider(this).get(TodoViewModel::class.java)


        todoId = arguments?.getInt("todoId")


        todoId?.let { id ->
            todoViewModel.getTodoById(id).observe(viewLifecycleOwner) { todo ->
                todo?.let {
                    subTitleEditText.setText(it.title)
                    notesEditText.setText(it.notes)


                    it.deadline?.let { deadline ->
                        selectedDateText.text = deadline
                    }
                    it.time?.let { time ->
                        selectedTimeText.text = time
                    }

                    when (it.taskDateType) {
                        "today" -> {
                            todayButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.selectedButton))
                            todayButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkicon, 0,0,0)
                        }
                        "tomorrow" -> {
                            tomorrowButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.selectedButton))
                            tomorrowButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkicon, 0,0,0)
                        }
                        "another_day" -> {
                            anotherDayButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.selectedButton))
                            anotherDayButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkicon, 0,0,0)
                            }
                    }
                }
            }
        }






        view.findViewById<Button>(R.id.saveChangesButton).setOnClickListener {
            saveTodoChanges()
        }
    }

    private fun saveTodoChanges() {
        val updatedTitle = subTitleEditText.text.toString().trim()
        val updatedNotes = notesEditText.text.toString().trim()
        val updatedDate = selectedDateText.text.toString().trim()
        val updatedTime = selectedTimeText.text.toString().trim()

        if (updatedTitle.isEmpty() || updatedNotes.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (todoId != null) {
            todoViewModel.updateTodo(
                Todo(
                    id = todoId!!,
                    title = updatedTitle,
                    notes = updatedNotes,
                    deadline = updatedDate,
                    time = updatedTime
                )
            )
            Toast.makeText(requireContext(), "Todo updated", Toast.LENGTH_SHORT).show()
            
            parentFragmentManager.popBackStack()
        } else {
            Toast.makeText(requireContext(), "Error: Todo not found", Toast.LENGTH_SHORT).show()
        }
    }

}