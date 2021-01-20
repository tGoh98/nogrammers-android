package com.example.nogrammers_android.events

import android.graphics.Color
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.alpha
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.MainActivity
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentEventsBinding
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

/**
 * Events tab
 */
class EventsFragment : Fragment() {
    lateinit var binding: FragmentEventsBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentEventsBinding>(
                inflater,
                R.layout.fragment_events, container, false
        )

        /* Create and set tabs adapter */
        binding.eventTabsPager.adapter = EventsTabAdapter(activity as AppCompatActivity, 2)

        /* Tab layout mediator to connect tab labels to ViewPager */
        TabLayoutMediator(binding.eventTabLayout, binding.eventTabsPager) { tab, pos ->
            when (pos) {
                0 -> tab.text = "Feed"
                else -> tab.text = "My Events"
            }
        }.attach()

        setHasOptionsMenu(true)

        val order_options = listOf("Most recent first",
                "Saved first", "Most popular first")
        binding.ordering.adapter = this.context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, order_options) }

        binding.applyButton.setOnClickListener {
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

    /**
     * add the filter action to the menu
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
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