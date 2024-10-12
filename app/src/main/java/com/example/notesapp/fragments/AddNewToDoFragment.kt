package com.example.notesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.notesapp.R
import com.example.notesapp.dataClass.Todo
import com.example.notesapp.viewModel.TodoViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddNewToDoFragment : Fragment() {

    private lateinit var titleText: TextView
    private lateinit var subTitleEditText: EditText
    private lateinit var notesEditText: EditText
    private lateinit var todayButton: Button
    private lateinit var tomorrowButton: Button
    private lateinit var anotherDayButton: Button
    private lateinit var timeText: TextView
    private lateinit var alarmText: TextView
    private lateinit var addTaskButton: Button
    private lateinit var imageButton: ImageButton
    private val todoViewModel: TodoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_new_todo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            val currentDate = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(currentDate)

            val dayFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
            val dayOfWeek = dayFormat.format(currentDate)

            val dateTextView: TextView = view.findViewById(R.id.date_text_view)
            val dayTextView: TextView = view.findViewById(R.id.day_text_view)

            dateTextView.text = formattedDate
            dayTextView.text = dayOfWeek

        } catch (e: Exception) {
            e.printStackTrace()
        }

        // RadioButton yerine ImageButton kullanarak değişiklik yapıyoruz
        titleText = view.findViewById(R.id.titleText)
        subTitleEditText = view.findViewById(R.id.subTitleEditText)
        notesEditText = view.findViewById(R.id.notesEditText)
        todayButton = view.findViewById(R.id.todayButton)
        tomorrowButton = view.findViewById(R.id.tomorrowButton)
        anotherDayButton = view.findViewById(R.id.anotherDayButton)
        timeText = view.findViewById(R.id.timeText)
        alarmText = view.findViewById(R.id.alarmText)
        addTaskButton = view.findViewById(R.id.addTaskButton)
        imageButton = view.findViewById(R.id.imageButton)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        addTaskButton.setOnClickListener {
            saveTask()
        }

        todayButton.setOnClickListener {
            Toast.makeText(requireContext(), "Today button clicked", Toast.LENGTH_SHORT).show()
        }

        tomorrowButton.setOnClickListener {
            Toast.makeText(requireContext(), "Tomorrow button clicked", Toast.LENGTH_SHORT).show()
        }

        anotherDayButton.setOnClickListener {
            Toast.makeText(requireContext(), "Another day button clicked", Toast.LENGTH_SHORT).show()
        }

        imageButton.setOnClickListener {
            Toast.makeText(requireContext(), "Edit image clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveTask() {
        val title = subTitleEditText.text.toString().trim()
        val notes = notesEditText.text.toString().trim()

        if (title.isEmpty() || notes.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val todo = Todo(title = title, notes = notes)
        todoViewModel.insert(todo)

        Toast.makeText(requireContext(), "Task saved!", Toast.LENGTH_SHORT).show()

        subTitleEditText.text.clear()
        notesEditText.text.clear()
    }

}