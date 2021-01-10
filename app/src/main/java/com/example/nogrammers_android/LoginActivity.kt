package com.example.nogrammers_android

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

const val NETID_MESSAGE = "com.example.nogrammers_android.NETID_MESSAGE"

/**
 * Login screen
 */
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /* Set button listener */
        findViewById<Button>(R.id.loginBtn).setOnClickListener {
            /* Pass netID information to main activity */
            // TODO: send to CAS and verify login credentials + rice affiliation
            val netID = findViewById<EditText>(R.id.editTextNetID).text.toString()
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra(NETID_MESSAGE, netID)
            }
            startActivity(intent)
            finish()
        }
    }
}