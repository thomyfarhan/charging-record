package com.aesthomic.chargingrecord.status


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.aesthomic.chargingrecord.R
import com.aesthomic.chargingrecord.databinding.FragmentStatusBinding

class StatusFragment : Fragment() {

    private lateinit var binding: FragmentStatusBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_status, container, false)

        val application = requireNotNull(this.activity).application

        val viewModelFactory = StatusViewModelFactory(application)
        val viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(StatusViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.isCharging.observe(this, Observer {
            if (it) {
                binding.tvStatusIscharging.setTextColor(
                    ContextCompat.getColor(application, R.color.colorPrimary)
                )
            } else {
                binding.tvStatusIscharging.setTextColor(
                    ContextCompat.getColor(application, R.color.delete_primary)
                )
            }
        })

        return binding.root
    }


}
