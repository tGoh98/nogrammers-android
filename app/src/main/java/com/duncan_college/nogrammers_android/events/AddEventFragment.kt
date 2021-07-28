package com.duncan_college.nogrammers_android.events

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.duncan_college.nogrammers_android.R
import com.duncan_college.nogrammers_android.databinding.FragmentAddEventBinding
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * Fragment for creating new events
 */
class AddEventFragment(val netId: String) : Fragment() {

    lateinit var binding : FragmentAddEventBinding
    lateinit var startTime : MutableList<Int>
    lateinit var endTime : MutableList<Int>
    private val ONE_MEGABYTE: Long = 1024 * 1024

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

        /* Create launcher for choosing an image */
        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == Activity.RESULT_OK) {
                /* Compute file size */
                val uri = res.data!!.data
                binding.testView.setImageURI(uri)
                val bitmap = (binding.testView.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val size = baos.size()
                if (size > ONE_MEGABYTE) {
                    Toast.makeText(context, "Please upload an image smaller than 1mb", Toast.LENGTH_SHORT).show()
                    binding.testView.setImageURI(null)
                } else {
                    /* Update view with chosen image */
                    binding.eventImage.setImageURI(uri)
                }
            }
        }
        /* Create launcher for getting permission to photo gallery */
        val requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        /* Permission is granted, let user choose image */
                        val imageChooserIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI).setType("image/*")
                        resultLauncher.launch(imageChooserIntent)
                    } else {
                        // Explain to the user that the feature is unavailable because the
                        // features requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.
                        Toast.makeText(context, "Please enable photos permission to add an event picture :(", Toast.LENGTH_SHORT).show()
                    }
                }
        binding.addImageButton.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

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
        startTime = mutableListOf(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
        endTime = mutableListOf(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))

        binding.datePickerButton.setOnClickListener {
            if (binding.datePicker.visibility.equals(View.GONE)) {
                binding.datePicker.visibility = View.VISIBLE
                binding.datePicker.requestFocus()
            }
            else {
                binding.datePicker.visibility = View.GONE
            }
        }

        binding.datePicker.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                binding.datePicker.visibility = View.GONE
            }
        }

        binding.startTimePickerButton.setOnClickListener {
            if (binding.startTimePicker.visibility.equals(View.GONE)) {
                binding.startTimePicker.visibility = View.VISIBLE
                binding.startTimePicker.requestFocus()
            }
            else {
                binding.startTimePicker.visibility = View.GONE
            }
        }

        binding.startTimePicker.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                binding.startTimePicker.visibility = View.GONE
            }
        }

        binding.endTimePickerButton.setOnClickListener {
            if (binding.endTimePicker.visibility.equals(View.GONE)) {
                binding.endTimePicker.visibility = View.VISIBLE
                binding.endTimePicker.requestFocus()
            }
            else {
                binding.endTimePicker.visibility = View.GONE
            }
        }

        binding.endTimePicker.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
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
            event.key = ref
            database.child(ref).setValue(event)
            if (binding.eventImage.height > 0) {
                saveImage(event)
            }
        }
    }

    fun saveImage (event: Event) {
        val storageRef = Firebase.storage.reference.child("eventPics").child(event.key)
        // Get the data from an ImageView as bytes
        //        imageView.isDrawingCacheEnabled = true <-- these two lines were deprecated but everything still seems to work ok
        //        imageView.buildDrawingCache()          <-- these two lines were deprecated but everything still seems to work ok
        val bitmap = (binding.eventImage.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imgData = baos.toByteArray()

        val uploadTask = storageRef.putBytes(imgData)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
    }
}