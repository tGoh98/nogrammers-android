package com.example.nogrammers_android

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        /* Disable dark mode */
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        /* Sign in as anonymous user for firebase access */
        Firebase.auth.signInAnonymously().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) Log.d("TAG", "Successful anon login!")
            else Log.d("TAG", "Anon login failed")
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // This is used to hide the status bar and make
        // the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        Handler().postDelayed({
//            val intent = Intent(this, MainActivity::class.java).apply {
//                putExtra(NETID_MESSAGE, "tmg5")
//            }
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 1500) // the delayed time in milliseconds.
    }

}