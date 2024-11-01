package com.example.notesapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val userNameEditText = findViewById<EditText>(R.id.userName)
        val signUpButton = findViewById<Button>(R.id.loginButton)

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val username = userNameEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            val userMap = HashMap<String, Any>()
                            userMap["username"] = username

                            if (userId != null) {
                                database.child(userId).setValue(userMap)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this, "Kayıt Başarılı", Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this, MainActivity::class.java))
                                            finish()
                                        } else {
                                            Toast.makeText(this, "Kayıt başarısız", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        } else {
                            // Kayıt işlemi başarısız oldu
                            Toast.makeText(this, "Kayıt Başarısız: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

