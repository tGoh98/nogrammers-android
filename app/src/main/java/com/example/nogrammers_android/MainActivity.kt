package com.example.nogrammers_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference


class MainActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val shoutoutsFrag=ShoutoutsFragment()
        val eventsFrag=EventsFragment()

        setCurrentFragment(shoutoutsFrag)

        findViewById<BottomNavigationView>(R.id.bottomNavView).setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.shoutouts_icon->setCurrentFragment(shoutoutsFrag)
                R.id.events_icon->setCurrentFragment(eventsFrag)
            }
            true
        }
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        var authors = listOf("adrienne", "tim", "julie", "colin", "cindy", "tim", "julie", "colin", "cindy", "tim", "julie", "colin", "cindy", "tim", "julie", "colin", "cindy", "tim", "julie", "colin", "cindy", "tim", "julie", "colin", "cindy", "tim", "julie", "colin", "cindy", "tim").map { Shoutouts(it, "Lorem ipsum dolor sit amet, consectetur adipiscing elit.") }
//        authors = authors.toMutableList()
//        val adapter = ShoutoutsAdapter(authors)
//
//        database = Firebase.database.reference.child("shoutouts")
//        val updateListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val so = dataSnapshot.getValue<ArrayList<HashMap<String, String>>>()
//                if (so != null) {
//                    authors.clear()
//                    for (map in so) {
//                        map["author"]?.let { map["msg"]?.let { it1 -> map["date"]?.let { it2 -> Shoutouts(it, it1, it2) } } }?.let { authors.add(it) }
//                    }
//                    authors.sortByDescending{it.date}
//                    adapter.notifyDataSetChanged()
//                }
//                Log.d("TAG", so.toString())
//                Log.d("TAG2", authors.toString())
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Getting Post failed, log a message
//                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
//            }
//        }
//        database.addValueEventListener(updateListener)
//
//        binding.shoutoutsView.adapter = adapter
//
//        // Button listeners
//        // Cancel, clear text fields and hide keyboard
//        binding.cancelButton.setOnClickListener() {
//            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(it.windowToken, 0)
//            binding.editTextName.text.clear()
//            binding.editTextMsg.text.clear()
//        }
//        // Post, add shoutout to firebase, then cancel
//        binding.postButton.setOnClickListener() {
//            var num = 0
//            val postListener = object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    val so = dataSnapshot.getValue<ArrayList<HashMap<String, String>>>()
//                    if (so != null) {
//                        num = so.size
//                    }
//                    database.child(num.toString()).setValue(Shoutouts(binding.editTextName.text.toString(),
//                            binding.editTextMsg.text.toString(), java.util.Calendar.getInstance().timeInMillis.toString()))
//                    binding.cancelButton.performClick()
//                }
//                override fun onCancelled(databaseError: DatabaseError) {
//                    // Getting Post failed, log a message
//                    Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
//                }
//            }
//            database.addListenerForSingleValueEvent(postListener)
//        }

    }

    private fun setCurrentFragment(fragment: Fragment)=
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fl_fragment,fragment)
                commit()
            }
}
