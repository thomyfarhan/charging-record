package com.aesthomic.chargingrecord.phoneheat


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders

import com.aesthomic.chargingrecord.R
import com.aesthomic.chargingrecord.database.ChargingRecordDatabase
import com.aesthomic.chargingrecord.databinding.FragmentPhoneHeatBinding

class PhoneHeatFragment : Fragment() {

    private lateinit var binding: FragmentPhoneHeatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_phone_heat, container, false)

        val application = requireNotNull(this.activity).application
        val arguments = PhoneHeatFragmentArgs.fromBundle(arguments!!)
        val database = ChargingRecordDatabase.getInstance(application).recordDao

        val viewModelFactory = PhoneHeatViewModelFactory(arguments.recordId, database)
        val viewModel = ViewModelProviders.of(
            this, viewModelFactory).get(PhoneHeatViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }


}
