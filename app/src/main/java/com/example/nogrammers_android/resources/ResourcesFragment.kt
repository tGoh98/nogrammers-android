package com.example.nogrammers_android.resources

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.MainActivity
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentResourcesBinding

/**
 * Resources tab
 */
class ResourcesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /* Inflate the layout for this fragment */
        val binding = DataBindingUtil.inflate<FragmentResourcesBinding>(
            inflater,
            R.layout.fragment_resources,
            container,
            false
        )

        /* Banner listener */
        binding.blmBannerLink.setOnClickListener {
            (activity as MainActivity).setBlmFragAdapter()
        }

        /* Listeners */
        binding.resourceWebsiteCell.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://riceduncan.github.io/")
                )
            )
        }
        binding.resourceFormsCell.setOnClickListener {
            (activity as MainActivity).setFormsFragAdapter()
        }
        binding.resourceSocialCell.setOnClickListener {
            (activity as MainActivity).setSocialFragAdapter()
        }

        binding.joeyImg.setOnClickListener {
            Toast.makeText(context, "Joey clicked!", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }
}