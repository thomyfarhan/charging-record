package com.aesthomic.chargingrecord.phoneheat


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import com.aesthomic.chargingrecord.R
import com.aesthomic.chargingrecord.database.ChargingRecordDatabase
import com.aesthomic.chargingrecord.databinding.FragmentPhoneHeatBinding

class PhoneHeatFragment : Fragment() {

    private lateinit var binding: FragmentPhoneHeatBinding
    private lateinit var viewModel: PhoneHeatViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_phone_heat, container, false)

        val application = requireNotNull(this.activity).application
        val arguments = PhoneHeatFragmentArgs.fromBundle(arguments!!)
        val database = ChargingRecordDatabase.getInstance(application).recordDao

        val viewModelFactory = PhoneHeatViewModelFactory(
            arguments.recordId, database, application)

        viewModel = ViewModelProviders.of(
            this, viewModelFactory).get(PhoneHeatViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initSeekBar()

        viewModel.navigateToRecord.observe(this, Observer { navigate ->
            if (navigate) {
                if (this.findNavController().currentDestination?.id ==
                    R.id.phone_heat_destination) {
                    this.findNavController().navigate(
                        PhoneHeatFragmentDirections
                            .actionPhoneHeatDestinationToRecordDestination())
                }
                viewModel.onNavigatingDone()
            }
        })

        binding.verticalsbHeat.setOnProgressChangeListener {
            viewModel.progressBar.value = it
        }

        return binding.root
    }

    private fun initSeekBar() {
        binding.verticalsbHeat.maxValue = 90
        binding.verticalsbHeat.progress = viewModel.progressBar.value ?: 0
    }


}
