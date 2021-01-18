package com.example.nogrammers_android.events

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentAddEventBinding
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.internal.NavigationMenuItemView
import java.util.*

class AddEventFragment : Fragment() {

    lateinit var binding : FragmentAddEventBinding
    val intToMonth = mapOf<Int, String>(Pair(1, "January"), Pair(2, "February"), Pair(3, "March"),
    Pair(4, "April"), Pair(5, "May"), Pair(6, "June"), Pair(7, "July"), Pair(8, "August"),
            Pair(9, "September"), Pair(10, "October"), Pair(11, "November"), Pair(12, "December"))

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
            binding.datePickerButton.text = intToMonth.get(month + 1) + " " + dayOfMonth.toString()
        })

        binding.startTimePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            binding.startTimePickerButton.text = hourOfDay.toString() + ":" + minute.toString()
        }

        binding.endTimePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            binding.endTimePickerButton.text = hourOfDay.toString() + ":" + minute.toString()
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
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