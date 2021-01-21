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
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentEventTabBinding
import java.util.*

/**
 * Fragment for event tabs
 */
class EventTabsFragment() : Fragment() {

    lateinit var binding: FragmentEventTabBinding

    companion object {
        const val ARG_POSITION = "position"

        fun getInstance(position: Int): Fragment {
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
        setHasOptionsMenu(true)

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val position = requireArguments().getInt(ARG_POSITION)

        /* Create recycler view */
        popEvents(position, false)
    }

    fun popEvents(position: Int, filter: Boolean) {
        val past = Calendar.getInstance()
        past.clear()
        past.set(2021, 0, 1)
        val present = Calendar.getInstance()
        present.clear()
        present.set(2021, 0, 19)
        val future = Calendar.getInstance()
        future.clear()
        future.set(2021, 0, 25)
        var eventsList = mutableListOf(
            Event("cys", "design club", "meeting time", past, present
                    , listOf("Food"), "Not Remote", "Duncanaroos only", "",true),
                Event("tmg", "brown's finest", "fun times at brown", past, future
                        , listOf("Alcohol"), "In-person", "Campus-wide", ""),
                Event("rjp5", "Duncan Forum", "fun times at brown", present, future
                        , listOf("Forums", "Discussion"), "In-person", "Campus-wide", ""),
                Event("rjp5", "Office Hours", "fun times at brown", present, future
                        , listOf("Discussion"), "In-person", "Campus-wide", ""),
                Event("cmz2", "Design Class", "fun times at brown", present, future
                        , listOf("Arts & Music", "Food"), "In-person", "Campus-wide", ""),
                Event("duncan", "App-a-thon", "fun times at brown", present, future
                        , listOf("Competition"), "In-person", "Campus-wide", ""),
                Event("cbk1", "healthy living", "fun times at brown", present, future
                        , listOf("Health & Wellness"), "In-person", "Campus-wide", "")
        )

        // if on my events tab, filter events marked interested or going
        if (position == 1) {
            eventsList.removeFirst()
        }

        // if filter, apply all filters
        if (filter) {
            val allEventsList = eventsList
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
                    if (startDate > event.start) { addEvent = false}
                }
                if (!binding.endDateButton.text.toString().equals("end date")) {
                    val endDate = Calendar.getInstance()
                    endDate.clear()
                    endDate.set(binding.endDatePicker.year, binding.endDatePicker.month,
                            binding.endDatePicker.dayOfMonth)
                    if (endDate < event.start) { addEvent = false}
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
                    if (event.audience.equals("Campus-wide")) { addEvent = false }
                }

                if (addEvent) { eventsList.add(event) }
            }
        }

        val adapter = EventsItemAdapter(eventsList)
        binding.eventTabList.adapter = adapter
    }

    /**
     * add the filter action to the menu
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.event_actions, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * hide/show filter layout when action bar button is pressed
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val action = item.itemId
        if (action.equals(R.id.action_filter)) {
            if (binding.filterLayout.visibility.equals(View.GONE)) {
                binding.filterLayout.visibility = View.VISIBLE
                binding.addEventButton.visibility = View.GONE
            } else {
                binding.filterLayout.visibility = View.GONE
                binding.addEventButton.visibility = View.VISIBLE
            }
        }
        return super.onOptionsItemSelected(item)
    }

}