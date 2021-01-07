package com.example.nogrammers_android.profile

import android.widget.TextView
import androidx.databinding.BindingAdapter

/**
 * Binding adapters for ProfileItemViewHolder
 */
@BindingAdapter("content")
fun TextView.setContent(item: ProfileItem) {
    text = item.content
}