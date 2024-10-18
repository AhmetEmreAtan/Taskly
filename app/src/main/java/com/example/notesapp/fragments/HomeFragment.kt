package com.example.notesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.adapter.NoteAdapter
import com.example.notesapp.adapter.TodoAdapter
import com.example.notesapp.dataClass.Todo
import com.example.notesapp.viewModel.NoteViewModel
import com.example.notesapp.viewModel.TodoViewModel

class HomeFragment : Fragment() {

    private lateinit var noteRecyclerView: RecyclerView
    private lateinit var todoRecyclerView: RecyclerView
    private val noteViewModel: NoteViewModel by viewModels()
    private val todoViewModel: TodoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val todoListAllTextView = view.findViewById<TextView>(R.id.todoListAllTextView)

        todoListAllTextView.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.main_frameLayout, ToDoFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }


        noteRecyclerView = view.findViewById(R.id.home_note_list)
        noteRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        todoRecyclerView = view.findViewById(R.id.home_todo_list)
        todoRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteViewModel.allNotes.observe(viewLifecycleOwner) { notes ->
            val adapter = NoteAdapter(notes, { selectedNote ->
                val noteDetailFragment = NoteDetailFragment.newInstance(selectedNote)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frameLayout, noteDetailFragment)
                    .addToBackStack(null)
                    .commit()
            }, { longClickedNote ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Note")
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton("Yes") { _, _ ->
                        noteViewModel.delete(longClickedNote)
                        Toast.makeText(requireContext(), "Note deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            })
            noteRecyclerView.adapter = adapter
        }


        todoViewModel.allTodos.observe(viewLifecycleOwner) { todoList ->
            if (todoList.isEmpty()) {
                todoRecyclerView.visibility = View.GONE
                view.findViewById<TextView>(R.id.empty_view).visibility = View.VISIBLE
            } else {
                todoRecyclerView.visibility = View.VISIBLE
                view.findViewById<TextView>(R.id.empty_view).visibility = View.GONE

                todoRecyclerView.adapter = TodoAdapter(
                    todoList.toMutableList(),
                    { longClickedTodo ->
                        AlertDialog.Builder(requireContext())
                            .setTitle("Delete Todo")
                            .setMessage("Are you sure you want to delete this task?")
                            .setPositiveButton("Yes") { _, _ ->
                                todoViewModel.delete(longClickedTodo)
                                Toast.makeText(requireContext(), "Todo deleted", Toast.LENGTH_SHORT).show()
                            }
                            .setNegativeButton("No") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                    },
                    { updatedTodo ->
                        todoViewModel.updateTodo(updatedTodo)
                    },
                    { todoToEdit ->
                        val bundle = Bundle().apply {
                            putInt("todoId", todoToEdit.id)
                        }
                        val todoDetailFragment = TodoDetailFragment().apply {
                            arguments = bundle
                        }
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.main_frameLayout, todoDetailFragment)
                            .addToBackStack(null)
                            .commit()
                    },
                    { todoWithAlarm ->
                        Toast.makeText(requireContext(), "Alarm button clicked for ${todoWithAlarm.title}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}
