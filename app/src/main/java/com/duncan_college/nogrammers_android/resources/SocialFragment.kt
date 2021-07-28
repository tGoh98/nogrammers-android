package com.duncan_college.nogrammers_android.resources

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.duncan_college.nogrammers_android.R
import com.duncan_college.nogrammers_android.databinding.FragmentSocialBinding

/**
 * Social media tab
 */
class SocialFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /* Inflate the layout for this fragment */
        val binding: FragmentSocialBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_social, container, false)

        /* Hyperlink listeners */
        binding.resourceFacebookCell.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/groups/201995963184811/")))
        }

        binding.resourceInstaCell.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/duncancollege/")))
        }

        binding.resourceHistorianCell.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/duncanhistory/")))
//            startActivity(Intent.createChooser(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:duncancollegesecretaries@gmail.com")), "Email Duncan"))
        }

        return binding.root
    }
}