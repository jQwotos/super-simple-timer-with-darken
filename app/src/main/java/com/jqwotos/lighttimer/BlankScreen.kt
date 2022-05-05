package com.jqwotos.lighttimer

import android.graphics.Color
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.jqwotos.lighttimer.databinding.ActivityBlankScreenBinding

class BlankScreen : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityBlankScreenBinding
    private lateinit var layout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layout = findViewById(R.id.blank_screen_constaint_layout)
        layout.setBackgroundColor(Color.parseColor("#000000"))

        binding = ActivityBlankScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_blank_screen)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}