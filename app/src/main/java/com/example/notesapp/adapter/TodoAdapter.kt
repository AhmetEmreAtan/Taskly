package com.example.notesapp.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.dataClass.Todo

class TodoAdapter(
    private var todoList: MutableList<Todo>,
    private val onDeleteClick: (Todo) -> Unit,
    private val onUpdateClick: (Todo) -> Unit,
    private val onEditClick: (Todo) -> Unit,
    private val onAlarmClick: (Todo) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statusImageButton: ImageButton = itemView.findViewById(R.id.imageButton)
        val todoAlarmButton: ImageButton = itemView.findViewById(R.id.todo_alarmButton)
        val todoEditButton: TextView = itemView.findViewById(R.id.todo_editButton)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val notesTextView: TextView = itemView.findViewById(R.id.notesTextView)
        val todoDateTextView: TextView = itemView.findViewById(R.id.todoDateTextView)
        val todoTimeTextView: TextView = itemView.findViewById(R.id.todoTimeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentTodo = todoList[position]

        holder.titleTextView.text = currentTodo.title
        holder.notesTextView.text = currentTodo.notes
        holder.todoDateTextView.text = currentTodo.deadline ?: "No Deadline"
        holder.todoTimeTextView.text = currentTodo.time ?: "No Time"

        setStrikeThrough(holder, currentTodo.isCompleted)

        updateImageButton(holder, currentTodo.isCompleted)

        holder.statusImageButton.setOnClickListener {
            currentTodo.isCompleted = !currentTodo.isCompleted
            updateImageButton(holder, currentTodo.isCompleted)
            setStrikeThrough(holder, currentTodo.isCompleted)
            onUpdateClick(currentTodo)
        }

        holder.todoEditButton.setOnClickListener {
            onEditClick(currentTodo)
        }

        holder.todoAlarmButton.setOnClickListener {
            onAlarmClick(currentTodo)
        }

        holder.itemView.setOnLongClickListener {
            onDeleteClick(currentTodo)
            true
        }
    }

    override fun getItemCount(): Int = todoList.size

    private fun updateImageButton(holder: TodoViewHolder, isCompleted: Boolean) {
        if (isCompleted) {
            holder.statusImageButton.setImageResource(R.drawable.checkicon)
        } else {
            holder.statusImageButton.setImageResource(R.drawable.unchecked)
        }
    }

    private fun setStrikeThrough(holder: TodoViewHolder, isCompleted: Boolean) {
        if (isCompleted) {
            holder.titleTextView.paintFlags = holder.titleTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.notesTextView.paintFlags = holder.notesTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.titleTextView.paintFlags = holder.titleTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.notesTextView.paintFlags = holder.notesTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    fun updateTodoList(newTodoList: List<Todo>) {
        todoList.clear()
        todoList.addAll(newTodoList)
        notifyDataSetChanged()
    }
}
