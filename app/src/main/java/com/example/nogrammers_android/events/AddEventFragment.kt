package com.example.nogrammers_android.events

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.nogrammers_android.MainActivity
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentAddEventBinding
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.internal.NavigationMenuItemView
import java.util.*

/**
 * Fragment for creating new events
 */
class AddEventFragment : Fragment() {

    lateinit var binding : FragmentAddEventBinding

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
        var hideKeyboardListener = View.OnFocusChangeListener { v, hasFocus ->  if (!hasFocus) {
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
            this.activity?.findViewById<BottomNavigationItemView>(R.id.events_icon)?.performClick()
        }

        /**
         * Set up date and time pickers
         */

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
        }

        binding.endTimePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            binding.endTimePickerButton.text = DateTimeUtil.getStringFromTime(hourOfDay, minute)
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

}