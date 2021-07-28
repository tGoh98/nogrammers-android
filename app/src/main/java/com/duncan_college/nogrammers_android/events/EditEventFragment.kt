package com.duncan_college.nogrammers_android.events

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.duncan_college.nogrammers_android.R
import com.duncan_college.nogrammers_android.databinding.FragmentEditEventBinding
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * Fragment for editing events
 */
class EditEventFragment(val event: Event) : Fragment() {

    lateinit var binding : FragmentEditEventBinding
    lateinit var startTime : MutableList<Int>
    lateinit var endTime : MutableList<Int>
    var imageChanged = false
    private val ONE_MEGABYTE: Long = 1024 * 1024

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentEditEventBinding>(
                inflater,
                R.layout.fragment_edit_event, container, false
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

        binding.saveButton.setOnClickListener {
            // populate image if listserv checkbox is selected
            if (binding.listservBox.isChecked) {
                composeEmail()
            }
            // save to Firebase
            saveEventToFirebase()
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
        setValues()

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

    fun saveEventChanges() {

        event.title = binding.eventTitle.text.toString()

        // get start and end date
        val start = DateTimeUtil.getCalendarFromDate(binding.datePicker.year,
                binding.datePicker.month, binding.datePicker.dayOfMonth)
        start.set(Calendar.HOUR, startTime[0])
        start.set(Calendar.MINUTE, startTime[1])
        event.start = start.timeInMillis

        val end = DateTimeUtil.getCalendarFromDate(binding.datePicker.year,
                binding.datePicker.month, binding.datePicker.dayOfMonth)
        end.set(Calendar.HOUR, endTime[0])
        end.set(Calendar.MINUTE, endTime[1])
        event.end = end.timeInMillis

        event.remote = binding.remoteBox.isChecked
        event.location = binding.eventLocation.text.toString()
        event.desc = binding.eventDescription.text.toString()
        event.audience = if (binding.campusWideBox.isChecked) { "Campus-wide" }
        else if (binding.duncaroosBox.isChecked) {"Duncaroos-only"}
        else {""}
        val tags = mutableListOf<String>()
        for(child in binding.tagGroup.children) {
            event.clearTags()
            if (child is Button && child.currentTextColor == Color.parseColor("#FFFFFFFF")) {
                tags.add(child.text.toString())
            }
        }
        event.tags = tags
    }

    fun saveEventToFirebase () {
        saveEventChanges()
        val database = Firebase.database.reference.child("events")
        database.child(event.key).setValue(event)
        if (imageChanged) {
            saveImage()
        }
    }

    fun saveImage () {
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

    fun setValues() {
        binding.eventTitle.setText(event.title)
        var startCal = DateTimeUtil.getCalendarFromTimeInMillis(event.start)
        var endCal = DateTimeUtil.getCalendarFromTimeInMillis(event.end)
        binding.datePicker.updateDate(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH),
        startCal.get(Calendar.DAY_OF_MONTH))
        binding.startTimePicker.currentHour = startCal.get(Calendar.HOUR_OF_DAY)
        binding.startTimePicker.currentMinute = startCal.get(Calendar.MINUTE)
        binding.endTimePicker.currentHour = endCal.get(Calendar.HOUR_OF_DAY)
        binding.endTimePicker.currentMinute = endCal.get(Calendar.MINUTE)
        binding.eventLocation.setText(event.location)
        binding.remoteBox.isChecked = event.remote
        binding.eventDescription.setText(event.desc)
        binding.campusWideBox.isChecked = (event.audience.equals(context?.resources?.getString(
                R.string.Campus_wide)))
        binding.duncaroosBox.isChecked = (event.audience.equals(context?.resources?.getString(
                R.string.Duncaroos_only)))
        for(child in binding.tagGroup.children) {
            if (child is Button && event.tags.contains(child.text.toString())) {
                this.context?.let { ContextCompat.getColor(it, R.color.white) }?.let { child.setTextColor(it) }
                this.context?.let { ContextCompat.getColor(it, R.color.olive) }?.let { child.setBackgroundColor(it) }
            }
        }
        /* Set custom image */
        val storageRef = Firebase.storage.reference.child("eventPics").child(event.key)
        val ONE_MEGABYTE: Long = 1024 * 1024 * 5
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            /* Found pic, set it */
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            val eventImgView = binding.eventImage
            eventImgView.setImageBitmap(Bitmap.createScaledBitmap(bmp, eventImgView.width, 200, false))
        }.addOnFailureListener {
            /* Not found/error, use default */
        }
    }
}