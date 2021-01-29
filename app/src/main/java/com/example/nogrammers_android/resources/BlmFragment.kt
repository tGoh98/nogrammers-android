package com.example.nogrammers_android.resources

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.R

/**
 * BLM statement fragment
 */
class BlmFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /* Inflate the layout for this fragment */
        return inflater.inflate(R.layout.fragment_blm, container, false)
    }
}