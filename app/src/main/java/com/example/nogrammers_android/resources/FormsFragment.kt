package com.example.nogrammers_android.resources

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentFormsBinding

/**
 * Forms tab
 */
class FormsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /* Inflate the layout for this fragment */
        val binding: FragmentFormsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_forms, container, false)

        /* Listeners for hyperlink */
        binding.formsAnonFeedCell.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSe_qwghqxEpO3VV51LQJ1zDY9ESnBvpa9gdqqA54Q7_a2hejQ/viewform")))
        }

        binding.formsGreenCheckoutCell.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/1W4nqJxQYI8Kc_ts7gOIGUX6kobGL-xavxKuxPI22RX0/viewform?ts=5b8898ae&edit_requested=true")))
        }

        binding.formsInventoryCell.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSfOH8kKa-sfNsdVTv3r2ViSQhDwi1P_vlsg6vj5Io-9bU7PoA/viewform")))
        }

        binding.formsMagisterCell.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSfPtS7lDkWtbTHsUV-aupZiiakKVlNEqIv-9efDAfWGk3Bz1Q/viewform")))
        }

        binding.formsPaaCell.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/1iK2H8cst44XZ3uIAArOJVJr1gtgYViV9Ao-5XxAc5GM/viewform?edit_requested=true")))
        }

        binding.formsPrivateCell.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSdBNG6l54etZ-wHVuhhfdDd5TYD9cBfrfpbupN5gqpDeyQ21Q/viewform")))
        }

        binding.formsFlexCell.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/1kSQfixTruahIVrXMtWeHIWsloZarZtJ-k3H3jKS3sr0/viewform?c=0&w=1&usp=mail_form_link")))
        }

        binding.formsWorkCell.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://goo.gl/1DB67J")))
        }

        binding.formsRoomCell.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://duncancollege.skedda.com/booking")))
        }

        return binding.root
    }
}