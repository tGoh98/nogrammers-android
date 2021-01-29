package com.example.nogrammers_android.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentTagSearchBinding

/**
 * Displays tag search results. Also used to show matching users after a tag is selected.
 * Fragment is given the corresponding clickListener from MainActivity
 */
class TagSearchFragment(var clickListener: CellClickListener) : Fragment() {

    lateinit var binding: FragmentTagSearchBinding
    lateinit var adapter: TagItemAdapter
    lateinit var tagResults: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tag_search, container, false)

        /* Create and set adapter with suggested tags */
        adapter = TagItemAdapter(clickListener)
        tagResults = listOf("STRIVE Liaison", "PAA", "RHA", "Dunc Squad", "Dunc EC")
        adapter.submitList(tagResults)
        binding.tagSearchListView.adapter = adapter

        /* Add recycler view divider lines */
        val dividerLine = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        binding.tagSearchListView.addItemDecoration(dividerLine)

        /* Inflate the layout for this fragment */
        return binding.root
    }

    /**
     * So that main activity can remove the label when typing begins
     */
    fun removeSuggestedLabel() {
        binding.tagSearchSuggestedTags.visibility = View.GONE
    }

    /**
     * So that main activity can update tag results data in the recycler view
     */
    fun updateTagResults(newData: List<String>) {
        tagResults = newData
        adapter.submitList(tagResults)
    }

    /**
     * So that main activity can update the click listener for displaying users
     */
    fun updateClickListener(newCL: CellClickListener) {
        clickListener = newCL
        adapter = TagItemAdapter(clickListener)
        binding.tagSearchListView.adapter = adapter
    }
}