package com.example.nogrammers_android.events

import android.graphics.Color
import android.icu.text.DateTimePatternGenerator
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.core.graphics.alpha
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.nogrammers_android.MainActivity
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentEventTabBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*

/**
 * Fragment for event tabs
 */
class EventTabsFragment() : Fragment() {

    lateinit var binding: FragmentEventTabBinding
    var allEventsList = mutableListOf<Event>()

    companion object {
        const val ARG_POSITION = "position"

        fun getInstance(position: Int): EventTabsFragment {
            val eventTabsFrag = EventTabsFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_POSITION, position)
            eventTabsFrag.arguments = bundle
            return eventTabsFrag
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        /* Inflate the layout for this fragment */
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_event_tab, container, false
        )

        binding.applyButton.setOnClickListener {
            popEvents(requireArguments().getInt(ARG_POSITION), true)
            binding.filterLayout.visibility = View.GONE
            binding.addEventButton.visibility = View.VISIBLE
        }

        /**
         * Set up datepickers
         */
        val calendar = Calendar.getInstance()
        binding.startDateButton.setOnClickListener {
            if (binding.startDatePicker.visibility.equals(View.GONE)) {
                binding.startDatePicker.visibility = View.VISIBLE
            }
            else {
                binding.startDatePicker.visibility = View.GONE
            }
        }
        binding.endDateButton.setOnClickListener {
            if (binding.endDatePicker.visibility.equals(View.GONE)) {
                binding.endDatePicker.visibility = View.VISIBLE
            }
            else {
                binding.endDatePicker.visibility = View.GONE
            }
        }
        binding.startDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE)) { view, year, month, dayOfMonth ->
            binding.startDateButton.text = DateTimeUtil.getStringFromDate(month, dayOfMonth)
        }
        binding.endDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE)) { view, year, month, dayOfMonth ->
            binding.endDateButton.text = DateTimeUtil.getStringFromDate(month, dayOfMonth)
        }

        // connect to Firebase
        val database = Firebase.database.reference.child("events")
        val updateListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                allEventsList.clear()
                for (child in dataSnapshot.children) {
                    val eventObj = child.getValue<Map<String, Any>>()
                    if (eventObj != null) {
                        allEventsList.add(
                            Event(
                                eventObj.get("author") as String,
                                eventObj.get("title") as String,
                                eventObj.get("desc") as String,
                                eventObj.get("start") as Long,
                                eventObj.get("end") as Long,
                                if (eventObj.get("tags") == null) {
                                    mutableListOf<String>()} else {eventObj.get("tags") as List<String>},
                                eventObj.get("location") as String,
                                eventObj.get("audience") as String,
                                eventObj.get("pic") as String,
                                eventObj.get("remote") as Boolean
                            )
                        )
                    }
                }
               popEvents(getPosition(), false)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(updateListener)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val position = requireArguments().getInt(ARG_POSITION)

        /* Create recycler view */
        popEvents(position, false)
    }

    fun getPosition(): Int {
        return requireArguments().getInt(ARG_POSITION)
    }

    fun popEvents(position: Int, filter: Boolean) {
        var eventsList = allEventsList
        // if on my events tab, filter events marked interested or going
//        if (position == 1)

        // if filter, apply all filters
        if (filter) {
            eventsList = mutableListOf()

            // get all tags that are selected
            val tags = mutableListOf<String>()
            for(child in binding.filterButtonsLayout.children) {
                if (child is Button && child.currentTextColor == Color.parseColor("#FFFFFFFF")) {
                    tags.add(child.text.toString())
                }
            }

            for (event in allEventsList) {
                var addEvent = true
               // tags filter (if event has any of the applied tags, or if the tags list is empty
                if (tags.size > 0) {
                    var hasTag = false
                    for (tag in tags) { if (event.tags.contains(tag)) { hasTag = true } }
                    addEvent = hasTag
                }

                // date filter (if date has been changed)
                if (!binding.startDateButton.text.toString().equals("start date")) {
                    val startDate = Calendar.getInstance()
                    startDate.clear()
                    startDate.set(binding.startDatePicker.year, binding.startDatePicker.month,
                        binding.startDatePicker.dayOfMonth)
                    if (startDate.timeInMillis > event.start) { addEvent = false}
                }
                if (!binding.endDateButton.text.toString().equals("end date")) {
                    val endDate = Calendar.getInstance()
                    endDate.clear()
                    endDate.set(binding.endDatePicker.year, binding.endDatePicker.month,
                            binding.endDatePicker.dayOfMonth)
                    if (endDate.timeInMillis < event.start) { addEvent = false}
                }

                // format filter (if only one box is checked)
                if (binding.remoteBox.isChecked and !binding.inPersonBox.isChecked) {
                    if (!event.remote) { addEvent = false }
                }
                else if (!binding.remoteBox.isChecked and binding.inPersonBox.isChecked) {
                    if (event.remote) { addEvent = false }
                }

                // audience filter (if only one box is checked)
                if (binding.campusWideBox.isChecked and !binding.duncaroosBox.isChecked) {
                    if (!event.audience.equals("Campus-wide")) { addEvent = false }
                }
                else if (!binding.campusWideBox.isChecked and binding.duncaroosBox.isChecked) {
                    if (!event.audience.equals("Duncaroos-only")) { addEvent = false }
                }

                if (addEvent) { eventsList.add(event) }
            }
        }

        /* Update recycler view contents */
        val adapter = EventsItemAdapter(eventsList.sortedBy { it.start },
                EventsItemListener { event ->
                    parentFragmentManager.beginTransaction().apply{
                        val fragment = EventDetailFragment(event)
                        replace(R.id.fl_fragment, fragment)
                        setReorderingAllowed(true)
                        addToBackStack("event detail") // name can be null
                        commit()
                    }})
        binding.eventTabList.adapter = adapter
    }

    /**
     * hide/show filter layout when action bar button is pressed
     */
    fun filterPressed() {
        if (binding.filterLayout.visibility.equals(View.GONE)) {
            binding.filterLayout.visibility = View.VISIBLE
            binding.addEventButton.visibility = View.GONE
        } else {
            binding.filterLayout.visibility = View.GONE
            binding.addEventButton.visibility = View.VISIBLE
        }
    }

}