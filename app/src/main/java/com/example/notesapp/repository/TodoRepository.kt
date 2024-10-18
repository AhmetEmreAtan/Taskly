package com.example.notesapp.repository

import com.example.notesapp.dao.TodoDao
import com.example.notesapp.dataClass.Todo
import kotlinx.coroutines.flow.Flow
import androidx.lifecycle.LiveData

class TodoRepository(private val todoDao: TodoDao) {

    fun getAllTodos(): Flow<List<Todo>> {
        return todoDao.getAllTodos()
    }

    suspend fun insert(todo: Todo) {
        todoDao.insert(todo)
    }

    fun getTodoById(todoId: Int): LiveData<Todo> {
        return todoDao.getTodoById(todoId)
    }

    suspend fun update(todo: Todo) {
        todoDao.update(todo)
    }

    suspend fun delete(todo: Todo) {
        todoDao.delete(todo)
    }
}
