package com.example.nogrammers_android.events

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.graphics.alpha
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentEventTabBinding

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val position = requireArguments().getInt(ARG_POSITION)

        /* Create recycler view */
        popEvents(position)
    }

    fun popEvents(position: Int) {
        var eventsList = mutableListOf(
            Event("cys", "design club", "meeting time", "7:00", "8:00"
                    , listOf("food", "art & music", "competition"), "remote", "duncan", ""),
                Event("tmg", "brown's finest", "fun times at brown", "7:00", "8:00"
                        , listOf("food", "art & music", "alcohol"), "in-person", "campus", "")
        )

        // if on my events tab, filter events
        if (position == 1) {
            eventsList.removeFirst()
        }

        val adapter = EventsItemAdapter(eventsList)
        binding.eventTabList.adapter = adapter
    }

}