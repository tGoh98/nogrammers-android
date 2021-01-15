package com.example.nogrammers_android.events

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.alpha
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentEventsBinding
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Events tab
 */
class EventsFragment : Fragment() {
    lateinit var binding:FragmentEventsBinding;

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
                else -> tab.text = "Saved Events"
            }
        }.attach()

        setHasOptionsMenu(true);

        val order_options = listOf("Most recent first",
         "Saved first", "Most popular first")
        binding.ordering.adapter = this.context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, order_options) }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.event_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val action = item.itemId;
        if (action.equals(R.id.action_filter)) {
            if (binding.filterLayout.visibility.equals(View.GONE)) {
                binding.filterLayout.visibility = View.VISIBLE
            }
            else {
                binding.filterLayout.visibility = View.GONE
            }
        }
        else {
            Log.d("TAG", "some button was pressed")
        }
        return super.onOptionsItemSelected(item)
    }
}