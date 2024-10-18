package com.example.notesapp.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.example.notesapp.R
import com.example.notesapp.dataClass.Todo
import com.example.notesapp.viewModel.TodoViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddNewToDoFragment : Fragment() {

    private lateinit var subTitleEditText: EditText
    private lateinit var notesEditText: EditText
    private lateinit var addTaskButton: Button
    private lateinit var selectedDateTextView: TextView
    private lateinit var calendarView: CalendarView
    private var selectedDate: String? = null
    private lateinit var selectedTimeTextView: TextView
    private lateinit var todayButton: Button
    private lateinit var tomorrowButton: Button
    private lateinit var anotherDayButton: Button
    private var selectedDateType: String? = null
    private val todoViewModel: TodoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_new_todo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        subTitleEditText = view.findViewById(R.id.subTitleEditText)
        notesEditText = view.findViewById(R.id.notesEditText)
        addTaskButton = view.findViewById(R.id.addTaskButton)
        calendarView = view.findViewById(R.id.calendar_view)
        selectedDateTextView = view.findViewById(R.id.selectedDateTextView)
        selectedTimeTextView = view.findViewById(R.id.selectedTimeTextView)
        todayButton = view.findViewById(R.id.todayButton)
        tomorrowButton = view.findViewById(R.id.tomorrowButton)
        anotherDayButton = view.findViewById(R.id.anotherDayButton)


        todayButton.setOnClickListener {
            setupClickListeners()
        }

        tomorrowButton.setOnClickListener {
            setupClickListeners()
        }

        anotherDayButton.setOnClickListener {
            setupClickListeners()
        }


        calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar
                selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(clickedDayCalendar.time)


                selectedDateTextView.text = selectedDate


                val eventDays = mutableListOf<EventDay>()
                eventDays.add(EventDay(clickedDayCalendar, R.drawable.checkicon))


                calendarView.setEvents(eventDays)
            }
        })

        subTitleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (it.isNotEmpty()) {
                        val firstChar = it[0].toUpperCase()
                        if (it[0] != firstChar) {
                            val newText = firstChar + it.substring(1)
                            subTitleEditText.setText(newText)
                            subTitleEditText.setSelection(subTitleEditText.text.length)
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        selectedTimeTextView.setOnClickListener {
            showTimePickerDialog()
        }

        addTaskButton.setOnClickListener {
            saveTask()
        }
    }

    private fun saveTask() {
        val title = view?.findViewById<EditText>(R.id.subTitleEditText)?.text.toString().trim()
        val notes = view?.findViewById<EditText>(R.id.notesEditText)?.text.toString().trim()
        val deadline = selectedDate
        val time = selectedTimeTextView.text.toString()

        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a title", Toast.LENGTH_SHORT).show()
            return
        }

        if (notes.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter notes", Toast.LENGTH_SHORT).show()
            return
        }

        val todo = Todo(
            title = title,
            notes = notes,
            deadline = deadline,
            time = time,
            taskDateType = selectedDateType
        )

        todoViewModel.insert(todo)

        Toast.makeText(requireContext(), "Task saved!", Toast.LENGTH_SHORT).show()

        view?.findViewById<EditText>(R.id.subTitleEditText)?.text?.clear()
        view?.findViewById<EditText>(R.id.notesEditText)?.text?.clear()
        selectedTimeTextView.text = ""

        val homeFragment = HomeFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_frameLayout, homeFragment)
            .addToBackStack(null)
            .commit()
    }



    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, selectedHour: Int, selectedMinute: Int ->
            val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            selectedTimeTextView.text = selectedTime

        }, hour, minute, true).show()
    }


    private fun setupClickListeners() {
        todayButton.setOnClickListener {
            resetButtonColors()
            todayButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.selectedButton))
            selectedDateType = "today"
        }

        tomorrowButton.setOnClickListener {
            resetButtonColors()
            tomorrowButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.selectedButton))
            selectedDateType = "tomorrow"
        }

        anotherDayButton.setOnClickListener {
            resetButtonColors()
            anotherDayButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.selectedButton))
            selectedDateType = "another_day"
        }
    }


    private fun resetButtonColors() {
        todayButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.pastelBlue))
        tomorrowButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.lightBlue))
        anotherDayButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.yellowBlue))
    }

}
