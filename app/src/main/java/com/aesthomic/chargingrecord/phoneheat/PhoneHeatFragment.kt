package com.aesthomic.chargingrecord.phoneheat


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.aesthomic.chargingrecord.R
import com.aesthomic.chargingrecord.databinding.FragmentPhoneHeatBinding

class PhoneHeatFragment : Fragment() {

    private lateinit var binding: FragmentPhoneHeatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_phone_heat, container, false)
        return binding.root
    }


}