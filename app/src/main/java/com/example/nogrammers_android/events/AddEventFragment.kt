package com.example.nogrammers_android.events

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentAddEventBinding
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.options
import java.util.*
import java.util.function.IntToLongFunction

/**
 * Fragment for creating new events
 */
class AddEventFragment(val netId: String) : Fragment() {

    lateinit var binding : FragmentAddEventBinding
    lateinit var startTime : MutableList<Int>
    lateinit var endTime : MutableList<Int>

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentAddEventBinding>(
                inflater,
                R.layout.fragment_add_event, container, false
        )

        setHasOptionsMenu(true)

        // hide soft keyboard when clicking outside of edittext
        val hideKeyboardListener = View.OnFocusChangeListener { v, hasFocus ->  if (!hasFocus) {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
        }}
        binding.eventDescription.setOnFocusChangeListener(hideKeyboardListener)

        binding.eventTitle.setOnFocusChangeListener(hideKeyboardListener)

        binding.eventLocation.setOnFocusChangeListener(hideKeyboardListener)

        binding.addImageButton.setOnClickListener { showImageSelector() }

        binding.postButton.setOnClickListener {
            // populate image if listserv checkbox is selected
            if (binding.listservBox.isChecked) {
                composeEmail()
            }
            // create new event object and post to Firebase
            postEventToFirebase(createEvent())
            // close this fragment
            this.activity?.findViewById<BottomNavigationItemView>(R.id.events_icon)?.performClick()
        }

        /**
         * Set up date and time pickers
         */
        val cal = Calendar.getInstance()
        startTime = mutableListOf(cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE))
        endTime = mutableListOf(cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE))

        binding.datePickerButton.setOnClickListener {
            if (binding.datePicker.visibility.equals(View.GONE)) {
                binding.datePicker.visibility = View.VISIBLE
            }
            else {
                binding.datePicker.visibility = View.GONE
            }
        }

        binding.startTimePickerButton.setOnClickListener {
            if (binding.startTimePicker.visibility.equals(View.GONE)) {
                binding.startTimePicker.visibility = View.VISIBLE
            }
            else {
                binding.startTimePicker.visibility = View.GONE
            }
        }

        binding.endTimePickerButton.setOnClickListener {
            if (binding.endTimePicker.visibility.equals(View.GONE)) {
                binding.endTimePicker.visibility = View.VISIBLE
            }
            else {
                binding.endTimePicker.visibility = View.GONE
            }
        }

        val calendar = Calendar.getInstance()
        binding.datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE), { view, year, month, dayOfMonth ->
            binding.datePickerButton.text = DateTimeUtil.getStringFromDate(month, dayOfMonth)
        })

        binding.startTimePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            binding.startTimePickerButton.text = DateTimeUtil.getStringFromTime(hourOfDay, minute)
            startTime[0] = hourOfDay
            startTime[1] = minute
        }

        binding.endTimePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            binding.endTimePickerButton.text = DateTimeUtil.getStringFromTime(hourOfDay, minute)
            endTime[0] = hourOfDay
            endTime[1] = minute
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.add_event_actions, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Close fragment when action bar button is pressed
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        this.activity?.findViewById<BottomNavigationItemView>(R.id.events_icon)?.performClick()
        return super.onOptionsItemSelected(item)
    }

    fun showImageSelector(){
        Log.d("Tag","upload image")
    }

    fun composeEmail() {
        val text = binding.eventTitle.text.toString() + "\nDate: " + binding.datePickerButton.text.toString() +
                " Time:" + binding.startTimePickerButton.text.toString() + " - " +
                binding.endTimePickerButton.text.toString() + "\nLocation: " +
                binding.eventLocation.text.toString() + "\n" + binding.eventDescription.text.toString()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        if (this.activity?.packageManager?.let { intent.resolveActivity(it) } != null) {
            startActivity(intent)
        }
    }

    fun createEvent() : Event {
        // get current user for author
        val author = netId

        val title = binding.eventTitle.text.toString()

        // get start and end date
        val start = DateTimeUtil.getCalendarFromDate(binding.datePicker.year,
                binding.datePicker.month, binding.datePicker.dayOfMonth)
        start.set(Calendar.HOUR, startTime[0])
        start.set(Calendar.MINUTE, startTime[1])
        val end = DateTimeUtil.getCalendarFromDate(binding.datePicker.year,
                binding.datePicker.month, binding.datePicker.dayOfMonth)
        end.set(Calendar.HOUR, endTime[0])
        end.set(Calendar.MINUTE, endTime[1])

        val remote = binding.remoteBox.isChecked
        val location = binding.eventLocation.text.toString()
        val description = binding.eventDescription.text.toString()
        val audience = if (binding.campusWideBox.isChecked) { "Campus-wide" }
                        else if (binding.duncaroosBox.isChecked) {"Duncaroos-only"}
                        else {""}
        val tags = mutableListOf<String>()
        for(child in binding.tagGroup.children) {
            if (child is Button && child.currentTextColor == Color.parseColor("#FFFFFFFF")) {
                tags.add(child.text.toString())
            }
        }

        val pic = ""
        return Event(author, title, description, start.timeInMillis, end.timeInMillis, tags, location, audience, pic, remote)
    }

    fun postEventToFirebase (event : Event) {
        val database = Firebase.database.reference.child("events")
        val ref = database.push().key
        if (ref != null) {
            database.child(ref).setValue(event)
        }
    }
}