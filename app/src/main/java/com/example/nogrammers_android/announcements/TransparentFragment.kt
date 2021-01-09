package com.example.nogrammers_android.announcements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentTransparentBinding

/**
 * To use for transitions for closing the create announcements fragment.
 * Kinda sorta jank but it works
 */
class TransparentFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        /* Inflate the layout for this fragment with data binding */
        val binding = DataBindingUtil.inflate<FragmentTransparentBinding>(
                inflater,
                R.layout.fragment_transparent,
                container,
                false
        )
        return binding.root
    }
}