package com.getyoteam.budamind.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.getyoteam.budamind.R
import kotlinx.android.synthetic.main.fragment_image.*


class ImageFragment(val productImage: Int?) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(requireContext())
            .load(productImage)
            .placeholder(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.colorPrimary)))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(ivImage)
    }




}