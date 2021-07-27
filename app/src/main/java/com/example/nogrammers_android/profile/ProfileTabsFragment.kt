package com.example.nogrammers_android.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentProfileTabsBinding

/**
 * Fragment for profile tabs
 */
class ProfileTabsFragment : Fragment() {

    lateinit var binding: FragmentProfileTabsBinding

    companion object {
        const val ARG_POSITION = "position"

        fun getInstance(position: Int): Fragment {
            val profileTabsFrag = ProfileTabsFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_POSITION, position)
            profileTabsFrag.arguments = bundle
            return profileTabsFrag
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /* Inflate the layout for this fragment */
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_profile_tabs, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val position = requireArguments().getInt(ARG_POSITION)

        /* Create recycler view */
        // TODO: Populate tabs here
//        val adapter = ProfileItemAdapter((1..25).map {
//            ProfileItem("let's count to $it, version $position")
//        })
        val adapter = ProfileItemAdapter(listOf(ProfileItem("Coming soon!")))
        binding.profileTabList.adapter = adapter

        // Note: this is visibility: gone right now
        binding.tabPos.text = "Position: $position"
    }

}