package com.example.usthb9raya

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.usthb9raya.Utils.Utils.openDriveLink
import com.example.usthb9raya.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setCurrentFragment(HomeFragment())

        val homeFragment = HomeFragment()
        val favoritesFragment = FavoritesFragment()
        val contributeFragment = ContributeFragment()
        val settingsFragment = SettingsFragment()

        binding.buttHome.setOnClickListener {
            setCurrentFragment(homeFragment)
        }

        binding.buttFavorites.setOnClickListener {
            setCurrentFragment(favoritesFragment)
        }

        binding.buttContribute.setOnClickListener {
            setCurrentFragment(contributeFragment)
        }

        binding.buttSettings.setOnClickListener {
            setCurrentFragment(settingsFragment)
        }

    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragmentMain, fragment)
            commit()
        }
    }
}
