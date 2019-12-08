package com.aesthomic.chargingrecord.record


import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
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
import com.aesthomic.chargingrecord.databinding.FragmentRecordBinding
import com.google.android.material.snackbar.Snackbar

class RecordFragment : Fragment() {

    private lateinit var binding: FragmentRecordBinding
    private lateinit var application: Application
    private lateinit var viewModel: RecordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_record, container, false)

        application = requireNotNull(this.activity).application
        val database = ChargingRecordDatabase.getInstance(application).recordDao

        val viewModelFactory = RecordViewModelFactory(database, application)
        viewModel = ViewModelProviders.of(
            this, viewModelFactory).get(RecordViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.eventStart.observe(this, Observer { start ->
            if (start) {
                viewModel.onStartCharging(getBatteryLevel())
            }
        })

        viewModel.eventStop.observe(this, Observer { stop ->
            if (stop) {
                viewModel.onStopCharging(getBatteryLevel())
            }
        })

        viewModel.navigateToPhoneHeat.observe(this, Observer { record ->
            record?.let {
                if (this.findNavController().currentDestination?.id ==
                        R.id.record_destination) {
                    this.findNavController().navigate(
                        RecordFragmentDirections
                            .actionRecordDestinationToPhoneHeatDestination(it.id))
                    viewModel.onNavigatingDone()
                }
            }
        })

        viewModel.eventSnackBar.observe(this, Observer {
            if (it) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.delete_snackbar),
                    Snackbar.LENGTH_SHORT).show()
                viewModel.onSnackbarDone()
            }
        })

        return binding.root
    }

    fun getBatteryLevel(): Int {
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = application.registerReceiver(null, ifilter)

        return batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    }


}
