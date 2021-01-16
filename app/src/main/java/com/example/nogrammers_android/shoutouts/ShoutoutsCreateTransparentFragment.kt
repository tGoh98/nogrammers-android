package com.example.nogrammers_android.shoutouts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentCreateShoutoutTransparentBinding

class ShoutoutsCreateTransparentFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        /* Inflate the layout for this fragment with data binding */
        val binding = DataBindingUtil.inflate<FragmentCreateShoutoutTransparentBinding>(
                inflater,
                R.layout.fragment_create_shoutout_transparent,
                container,
                false
        )
        return binding.root
    }
}