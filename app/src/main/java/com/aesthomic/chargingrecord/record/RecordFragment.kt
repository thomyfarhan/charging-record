package com.aesthomic.chargingrecord.record


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.aesthomic.chargingrecord.R
import com.aesthomic.chargingrecord.database.ChargingRecordDatabase
import com.aesthomic.chargingrecord.databinding.FragmentRecordBinding

class RecordFragment : Fragment() {

    private lateinit var binding: FragmentRecordBinding
    private lateinit var viewModel: RecordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_record, container, false)

        val application = requireNotNull(this.activity).application
        val database = ChargingRecordDatabase.getInstance(application).recordDao

        val viewModelFactory = RecordViewModelFactory(database, application)
        viewModel = ViewModelProviders.of(
            this, viewModelFactory).get(RecordViewModel::class.java)

        return binding.root
    }


}
