package com.aesthomic.chargingrecord

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.aesthomic.chargingrecord.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        navController = this.findNavController(R.id.nav_host_fragment_main)
        val appBarConfiguration = AppBarConfiguration
            .Builder(
                R.id.record_destination,
                R.id.status_destination)
            .build()

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        NavigationUI.setupWithNavController(binding.navBottomMain, navController)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            if (destination.id in
                arrayOf(controller.graph.startDestination, R.id.status_destination)) {

                binding.navBottomMain.visibility = View.VISIBLE

            } else {
                binding.navBottomMain.visibility = View.GONE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}
