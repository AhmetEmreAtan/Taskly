package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.notesapp.fragments.AboutUsFragment
import com.example.notesapp.fragments.AddNewNoteFragment
import com.example.notesapp.fragments.AddNewTeamFragment
import com.example.notesapp.fragments.AddNewToDoFragment
import com.example.notesapp.fragments.HomeFragment
import com.example.notesapp.fragments.NotesFragment
import com.example.notesapp.fragments.ProfileFragment
import com.example.notesapp.fragments.SettingsFragment
import com.example.notesapp.fragments.TeamFragment
import com.example.notesapp.fragments.ToDoFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var  actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var navigationView: NavigationView
    private var isFabMenuOpen = false
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        drawerLayout = findViewById(R.id.myDrawer_layout)
        actionBarDrawerToggle = ActionBarDrawerToggle(this,drawerLayout,R.string.nav_open,R.string.nav_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        auth = FirebaseAuth.getInstance()


        //Firebase Auth
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, HelloActivity::class.java)
            startActivity(intent)
            finish()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //FloatinActionButton
        val mainFab = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val fabNewTodo = findViewById<FloatingActionButton>(R.id.fab_new_todo)
        val fabNewNote = findViewById<FloatingActionButton>(R.id.fab_new_note)
        val fabNewTeam = findViewById<FloatingActionButton>(R.id.fab_new_team)

        mainFab.setOnClickListener {
            if (isFabMenuOpen) {
                closeFabMenu(fabNewTodo, fabNewNote, fabNewTeam, mainFab)
            } else {
                openFabMenu(fabNewTodo, fabNewNote, fabNewTeam, mainFab)
            }
        }

        fabNewTodo.setOnClickListener {
            loadFragment(AddNewToDoFragment())
            closeFabMenu(fabNewTodo, fabNewNote, fabNewTeam, mainFab)
        }

        fabNewNote.setOnClickListener {
            loadFragment(AddNewNoteFragment())
            closeFabMenu(fabNewTodo, fabNewNote, fabNewTeam, mainFab)
        }

        fabNewTeam.setOnClickListener {
            loadFragment(AddNewTeamFragment())
            closeFabMenu(fabNewTodo, fabNewNote, fabNewTeam, mainFab)
        }

        //Navigation View
        navigationView = findViewById(R.id.nav_view)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_frameLayout, HomeFragment())
                .commit()
        }
        navigationView.setNavigationItemSelectedListener { menuItem ->
            val fragment = when (menuItem.itemId) {
                R.id.home_nav_menu -> HomeFragment()
                R.id.todo_nav_menu -> ToDoFragment()
                R.id.notes_nav_menu -> NotesFragment()
                R.id.team_nav_menu -> TeamFragment()
                R.id.profile_nav_menu -> ProfileFragment()
                R.id.settings_nav_menu -> SettingsFragment()
                R.id.aboutUs_nav_menu -> AboutUsFragment()
                else -> null
            }

            fragment?.let {
                loadFragment(it)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            fragment != null
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            true
        }else{
            super.onOptionsItemSelected(item)
        }

    }

    private fun openFabMenu(
        fabNewTodo: FloatingActionButton,
        fabNewNote: FloatingActionButton,
        fabNewTeam: FloatingActionButton,
        mainFab: FloatingActionButton
    ) {
        isFabMenuOpen = true
        mainFab.setImageResource(R.drawable.baseline_close)

        fabNewTodo.apply {
            visibility = View.VISIBLE
            animate().alpha(1f).translationX(resources.getDimension(R.dimen.fab_margin_1)).setDuration(200).start()
        }
        fabNewNote.apply {
            visibility = View.VISIBLE
            animate().alpha(1f).translationY(-resources.getDimension(R.dimen.fab_margin_2)).setDuration(200).start()
        }
        fabNewTeam.apply {
            visibility = View.VISIBLE
            animate().alpha(1f).translationX(resources.getDimension(R.dimen.fab_margin_3_x))
            animate().translationY(resources.getDimension(R.dimen.fab_margin_3_y)).setDuration(200).start()
        }
    }




    private fun closeFabMenu(
        fabNewTodo: FloatingActionButton,
        fabNewNote: FloatingActionButton,
        fabNewTeam: FloatingActionButton,
        mainFab: FloatingActionButton
    ) {
        isFabMenuOpen = false
        mainFab.setImageResource(R.drawable.baseline_add_24)

        fabNewTodo.animate().alpha(0f).translationY(0f).setDuration(200).withEndAction { fabNewTodo.visibility = View.GONE }.start()
        fabNewNote.animate().alpha(0f).translationY(0f).setDuration(200).withEndAction { fabNewNote.visibility = View.GONE }.start()
        fabNewTeam.animate().alpha(0f).translationY(0f).setDuration(200).withEndAction { fabNewTeam.visibility = View.GONE }.start()
    }


    private fun loadFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frameLayout, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

}