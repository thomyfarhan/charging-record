package com.aesthomic.chargingrecord

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.aesthomic.chargingrecord.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navController = this.findNavController(R.id.nav_host_fragment_main)

        NavigationUI.setupActionBarWithNavController(this, navController)

        NavigationUI.setupWithNavController(binding.navBottomMain, navController)
    }
}
