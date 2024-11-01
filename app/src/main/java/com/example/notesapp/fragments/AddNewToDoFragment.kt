package com.example.notesapp.fragments

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.example.notesapp.AlarmReceiver
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
    private var selectedTime: String? = null
    private lateinit var selectedTimeTextView: TextView
    private lateinit var todayButton: Button
    private lateinit var tomorrowButton: Button
    private lateinit var anotherDayButton: Button
    private lateinit var alarmTimeTextView: TextView
    private var selectedAlarmTime: Calendar? = null
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

        checkAndRequestNotificationPermission()

        subTitleEditText = view.findViewById(R.id.subTitleEditText)
        notesEditText = view.findViewById(R.id.notesEditText)
        addTaskButton = view.findViewById(R.id.addTaskButton)
        calendarView = view.findViewById(R.id.calendar_view)
        selectedDateTextView = view.findViewById(R.id.selectedDateTextView)
        selectedTimeTextView = view.findViewById(R.id.selectedTimeTextView)
        todayButton = view.findViewById(R.id.todayButton)
        tomorrowButton = view.findViewById(R.id.tomorrowButton)
        anotherDayButton = view.findViewById(R.id.anotherDayButton)


        alarmTimeTextView = view.findViewById(R.id.alarmTime)
        alarmTimeTextView.setOnClickListener {
            showDatePickerDialog()
        }

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
        val title = subTitleEditText.text.toString().trim()
        val notes = notesEditText.text.toString().trim()
        val deadline = selectedDate
        val alarmTime = selectedAlarmTime?.timeInMillis

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
            alarmTime = alarmTime,
            time = selectedTimeTextView.text.toString(),
            taskDateType = selectedDateType
        )

        todoViewModel.insert(todo)

        Toast.makeText(requireContext(), "Task saved!", Toast.LENGTH_SHORT).show()

        if (alarmTime != null) {
            setAlarm(selectedAlarmTime!!, title)
        }

        subTitleEditText.text?.clear()
        notesEditText.text?.clear()
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

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                showTimePickerDialog(calendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun showTimePickerDialog(calendar: Calendar) {
        val timePicker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                selectedAlarmTime = calendar


                val formattedTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(calendar.time)
                alarmTimeTextView.text = formattedTime
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePicker.show()
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val sharedPreferences = requireContext().getSharedPreferences("AlarmPreferences", Context.MODE_PRIVATE)
            val isPermissionGranted = sharedPreferences.getBoolean("isNotificationPermissionGranted", false)

            Log.d("PermissionCheck", "Notification permission granted status: $isPermissionGranted")

            if (isPermissionGranted) {
                Log.d("PermissionCheck", "Notification permission already granted, no need to request.")
                return
            }

            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (!notificationManager.areNotificationsEnabled()) {
                Log.d("PermissionCheck", "Notification permission not granted, requesting permission...")
                requestNotificationPermission()
            } else {
                Log.d("PermissionCheck", "Notification permission granted, saving to SharedPreferences.")
                sharedPreferences.edit().putBoolean("isNotificationPermissionGranted", true).apply()
            }
        } else {
            Log.d("PermissionCheck", "Notification permission check not needed for SDK version below TIRAMISU.")
        }
    }

    private fun requestNotificationPermission() {
        Log.d("PermissionRequest", "Requesting notification permission...")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.d("PermissionResult", "Permission result received, requestCode: $requestCode")

        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PermissionResult", "Notification permission granted, saving to SharedPreferences.")
                val sharedPreferences = requireContext().getSharedPreferences("AlarmPreferences", Context.MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("isNotificationPermissionGranted", true).apply()
                Toast.makeText(requireContext(), "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("PermissionResult", "Notification permission denied.")
                Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun setAlarm(calendar: Calendar, todoTitle: String) {
        checkAndRequestNotificationPermission()

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra("todoTitle", todoTitle)  // Todo başlığını intent ile gönderiyoruz

        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

}