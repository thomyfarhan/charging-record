package com.aesthomic.chargingrecord.record


import android.app.Application
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

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

        /**
         * Added dialog to make sure the user choose the right action
         */
        viewModel.eventClear.observe(this, Observer {
            if (it) {
                val dialog = MaterialAlertDialogBuilder(activity)
                dialog.apply {
                    setTitle("Delete All")
                    setMessage("Are you sure?")
                    setPositiveButton("Yes"){ _, _ ->
                        viewModel.onClear()
                    }
                    setNegativeButton("No", null)
                }
                dialog.show()
                viewModel.onClearDone()
            }
        })

        /**
         * Some condition added on this state to prevent some
         * navigation error by such actions like
         * two button pressed at the same time
         */
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

        /**
         * setAnchorView function on Snackbar is to determine the exact
         * position of Snackbar
         * in this state the snackbar will be placed above the NavBottom
         */
        viewModel.eventSnackBar.observe(this, Observer {
            if (it) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.delete_snackbar),
                    Snackbar.LENGTH_SHORT)
                    .setAnchorView(requireActivity().nav_bottom_main)
                    .show()
                viewModel.onSnackbarDone()
            }
        })

        return binding.root
    }


}
