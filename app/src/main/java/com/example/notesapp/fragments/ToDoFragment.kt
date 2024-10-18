package com.example.notesapp.fragments

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.MainActivity
import com.example.notesapp.R
import com.example.notesapp.adapter.CalendarAdapter
import com.example.notesapp.adapter.TodoAdapter
import com.example.notesapp.dataClass.Todo
import com.example.notesapp.viewModel.TodoViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class ToDoFragment : Fragment() {

    private lateinit var allTextView: TextView
    private lateinit var activeTextView: TextView
    private lateinit var completeTextView: TextView
    private lateinit var todayDayTextView: TextView
    private lateinit var todayDateTextView: TextView
    private lateinit var todoRecyclerView: RecyclerView
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var addNewTodoFab: FloatingActionButton
    private var filteredTodoList: MutableList<Todo> = mutableListOf()
    private var todoList: MutableList<Todo> = mutableListOf()
    private val todoViewModel: TodoViewModel by viewModels()

    private var selectedDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_todo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todayDayTextView = view.findViewById(R.id.todayDayTextView)
        todayDateTextView = view.findViewById(R.id.todayDateTextView)
        setCurrentDayAndDate()

        (activity as? MainActivity)?.findViewById<FloatingActionButton>(R.id.floatingActionButton)?.hide()


        val calendarRecyclerView: RecyclerView = view.findViewById(R.id.calendarRecyclerView)
        calendarRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val calendar = Calendar.getInstance()
        val dates = mutableListOf<Date>()
        for (i in 0..6) {
            dates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }


        val calendarAdapter = CalendarAdapter(dates) { selectedDate ->
            this.selectedDate = selectedDate
            applyFilter(getCurrentFilter())
        }
        calendarRecyclerView.adapter = calendarAdapter


        allTextView = view.findViewById(R.id.allTextView)
        activeTextView = view.findViewById(R.id.activeTextView)
        completeTextView = view.findViewById(R.id.completeTextView)
        todoRecyclerView = view.findViewById(R.id.todolistRecylerView)
        todoRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        todoAdapter = TodoAdapter(
            filteredTodoList,
            onDeleteClick = { todo ->
                todoViewModel.delete(todo)
            },
            onUpdateClick = { _ -> },
            onEditClick = { todo -> },
            onAlarmClick = { _ -> }
        )
        todoRecyclerView.adapter = todoAdapter


        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val todoToDelete = filteredTodoList[position]
                todoAdapter.notifyItemRemoved(position)
                todoViewModel.delete(todoToDelete)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val background = ColorDrawable()
                val icon = ContextCompat.getDrawable(requireContext(), R.drawable.delete_forever)


                if (dX > 0) {
                    background.color = Color.parseColor("#FF0000")
                    background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                    background.draw(c)

                    icon?.let {
                        val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                        val iconTop = itemView.top + iconMargin
                        val iconLeft = itemView.left + iconMargin
                        val iconRight = iconLeft + it.intrinsicWidth
                        val iconBottom = iconTop + it.intrinsicHeight

                        it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        it.draw(c)
                    }

                } else if (dX < 0) {
                    background.color = Color.parseColor("#FF0000")
                    background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    background.draw(c)

                    icon?.let {
                        val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                        val iconTop = itemView.top + iconMargin
                        val iconRight = itemView.right - iconMargin
                        val iconLeft = iconRight - it.intrinsicWidth
                        val iconBottom = iconTop + it.intrinsicHeight

                        it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        it.draw(c)
                    }
                } else {
                    background.setBounds(0, 0, 0, 0)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(todoRecyclerView)



        selectFilter(allTextView)

        todoViewModel.allTodos.observe(viewLifecycleOwner) { todos ->
            todoList.clear()
            todoList.addAll(todos)
            applyFilter("all")
        }


        allTextView.setOnClickListener {
            selectFilter(allTextView)
            applyFilter("all")
        }

        activeTextView.setOnClickListener {
            selectFilter(activeTextView)
            applyFilter("active")
        }

        completeTextView.setOnClickListener {
            selectFilter(completeTextView)
            applyFilter("complete")
        }

        addNewTodoFab = view.findViewById(R.id.addNewTodoFloatingActionButton)


        addNewTodoFab.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.main_frameLayout, AddNewToDoFragment())
                addToBackStack(null)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.findViewById<FloatingActionButton>(R.id.floatingActionButton)?.show()
    }

    private fun getCurrentFilter(): String {
        return when {
            allTextView.currentTextColor == ContextCompat.getColor(requireContext(), R.color.selectedText) -> "all"
            activeTextView.currentTextColor == ContextCompat.getColor(requireContext(), R.color.selectedText) -> "active"
            completeTextView.currentTextColor == ContextCompat.getColor(requireContext(), R.color.selectedText) -> "complete"
            else -> "all"
        }
    }

    private fun selectFilter(selectedTextView: TextView) {
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.selectedText)
        val defaultColor = ContextCompat.getColor(requireContext(), android.R.color.darker_gray)

        allTextView.setTextColor(defaultColor)
        allTextView.paintFlags = allTextView.paintFlags and android.graphics.Paint.UNDERLINE_TEXT_FLAG.inv()
        activeTextView.setTextColor(defaultColor)
        activeTextView.paintFlags = activeTextView.paintFlags and android.graphics.Paint.UNDERLINE_TEXT_FLAG.inv()
        completeTextView.setTextColor(defaultColor)
        completeTextView.paintFlags = completeTextView.paintFlags and android.graphics.Paint.UNDERLINE_TEXT_FLAG.inv()

        selectedTextView.setTextColor(selectedColor)
        selectedTextView.paintFlags = selectedTextView.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
    }

    private fun applyFilter(filter: String) {
        filteredTodoList.clear()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        when (filter) {
            "all" -> {
                if (selectedDate == null) {
                    filteredTodoList.addAll(todoList)
                } else {
                    filteredTodoList.addAll(todoList.filter { todo ->
                        todo.deadline?.let { deadline ->
                            val todoDate = dateFormat.parse(deadline)
                            todoDate?.let {
                                dateFormat.format(selectedDate) == dateFormat.format(it)
                            } ?: false
                        } ?: true
                    })
                }
            }
            "active" -> {
                if (selectedDate == null) {
                    filteredTodoList.addAll(todoList.filter { !it.isCompleted })
                } else {
                    filteredTodoList.addAll(todoList.filter { todo ->
                        !todo.isCompleted && todo.deadline?.let { deadline ->
                            val todoDate = dateFormat.parse(deadline)
                            todoDate?.let {
                                dateFormat.format(selectedDate) == dateFormat.format(it)
                            } ?: false
                        } ?: true
                    })
                }
            }
            "complete" -> {
                if (selectedDate == null) {
                    filteredTodoList.addAll(todoList.filter { it.isCompleted })
                } else {
                    filteredTodoList.addAll(todoList.filter { todo ->
                        todo.isCompleted && todo.deadline?.let { deadline ->
                            val todoDate = dateFormat.parse(deadline)
                            todoDate?.let {
                                dateFormat.format(selectedDate) == dateFormat.format(it)
                            } ?: false
                        } ?: true
                    })
                }
            }
        }
        todoAdapter.notifyDataSetChanged()
    }

    private fun setCurrentDayAndDate() {
        val currentDate = Date()

        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val dateFormat = SimpleDateFormat("d MMMM", Locale.getDefault())

        todayDayTextView.text = dayFormat.format(currentDate)
        todayDateTextView.text = dateFormat.format(currentDate)
    }
}