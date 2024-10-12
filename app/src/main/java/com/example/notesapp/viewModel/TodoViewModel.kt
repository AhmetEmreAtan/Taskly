package com.example.notesapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.notesapp.dataClass.Todo
import com.example.notesapp.database.TodoDatabase
import com.example.notesapp.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {


    private lateinit var repository: TodoRepository
    val allTodos by lazy {
        repository.getAllTodos().asLiveData()
    }

    init {
        val todoDao = TodoDatabase.getDatabase(application).todoDao()
        repository = TodoRepository(todoDao)
    }

    fun insert(todo: Todo) = viewModelScope.launch {
        repository.insert(todo)
    }

    fun getTodoById(todoId: Int): LiveData<Todo> {
        return repository.getTodoById(todoId)
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(todo)
        }
    }


    fun delete(todo: Todo) = viewModelScope.launch {
        repository.delete(todo)
    }
}