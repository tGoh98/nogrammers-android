package com.example.nogrammers_android

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.nogrammers_android.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        var authors = listOf("adrienne", "tim", "julie", "colin", "cindy", "tim", "julie", "colin", "cindy", "tim", "julie", "colin", "cindy", "tim", "julie", "colin", "cindy", "tim", "julie", "colin", "cindy", "tim", "julie", "colin", "cindy", "tim", "julie", "colin", "cindy", "tim").map { Shoutouts(it, "Lorem ipsum dolor sit amet, consectetur adipiscing elit.") }
        authors = authors.toMutableList()
        val adapter = ShoutoutsAdapter(authors)

        database = Firebase.database.reference.child("shoutouts")
//        database.child("0").setValue(Shoutouts("ALtesttest0", "hi"))
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val so = dataSnapshot.getValue<ArrayList<HashMap<String, String>>>()
                if (so != null) {
                    authors.clear()
                    for (map in so) {
                        if (map.containsKey("author")) {
                            map.get("author")?.let { Shoutouts(it, it) }?.let { authors.add(it) }
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
                Log.d("TAG", so.toString())
                Log.d("TAG", authors.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(postListener)

        binding.shoutoutsView.adapter = adapter
    }
}
