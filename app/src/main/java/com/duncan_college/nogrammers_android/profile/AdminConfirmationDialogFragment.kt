package com.duncan_college.nogrammers_android.profile

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.duncan_college.nogrammers_android.R


class AdminConfirmationDialogFragment : DialogFragment() {
    // Use this instance of the interface to deliver action events
    internal lateinit var listener: AdminDialogListener

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    interface AdminDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
    }

    // Override the Fragment.onAttach() method to instantiate the AdminDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the AdminDialogListener so we can send events to the host
            listener = parentFragment as EditProfileFragment
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(
                (context.toString() +
                        " must implement AdminDialogListener")
            )
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val inflater = requireActivity().layoutInflater
            val root = inflater.inflate(R.layout.admin_dialog_fragment, null)
            val input = root.findViewById<EditText>(R.id.adminDialogInput)

            val dialog = AlertDialog.Builder(it)
                .setView(root)
                .setTitle("Please enter the admin password")
                .setPositiveButton("Add tag", null) // pass null, will override below
                .setNegativeButton(R.string.cancel) { _, _ ->
                    // User cancelled the dialog
                }.create()
            dialog.setOnShowListener {
                val btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                btn.setOnClickListener {
                    if (input.text.toString() == "DuncAdmin") {
                        listener.onDialogPositiveClick(this)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(context, "Incorrect password!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
