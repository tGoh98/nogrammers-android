package com.example.nogrammers_android.events

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import com.example.nogrammers_android.databinding.FragmentEventDetailBinding
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.R
import com.google.android.material.bottomnavigation.BottomNavigationItemView

/**
 * Fragment for seeing an event's details
 */
class EventDetailFragment(val event: Event) : Fragment() {

    lateinit var binding : FragmentEventDetailBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentEventDetailBinding>(
                inflater,
                R.layout.fragment_event_detail, container, false
        )

        binding.eventImage.setImageResource(R.drawable.testimg)
        binding.eventTitle.text = event.title
        binding.eventDate.text = DateTimeUtil.getStringFromDateinMillis(event.start) + "\n" +
                DateTimeUtil.getStringFromTimeinMillis(event.start) +
                " to " + DateTimeUtil.getStringFromTimeinMillis(event.end)
        binding.eventLocation.text = event.location
        binding.remoteBox.isChecked = event.remote
        binding.eventDescription.text = event.desc
        binding.campusWideBox.isChecked = (event.audience.equals(context?.resources?.getString(R.string.Campus_wide)))
        binding.duncaroosBox.isChecked = (event.audience.equals(context?.resources?.getString(R.string.Duncaroos_only)))
        for(child in binding.tagGroup.children) {
            if (child is Button && event.tags.contains(child.text.toString())) {
                child.isClickable = false
                this.context?.let { getColor(it, R.color.white) }?.let { child.setTextColor(it) }
                this.context?.let { getColor(it, R.color.olive) }?.let { child.setBackgroundColor(it) }
            }
            else if (child is Button) {
                child.visibility = View.GONE
            }
        }

        setHasOptionsMenu(true)

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
}