package com.example.usthb9raya

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.usthb9raya.fragments.HomeFragment
import com.example.usthb9raya.databinding.ActivityMainBinding
import com.example.usthb9raya.fragments.ContributeFragment
import com.example.usthb9raya.fragments.FavoritesFragment
import com.example.usthb9raya.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var selectedFragment = "home"
    private var newSelectedFragment = "home"
    private lateinit var buttHome: LinearLayout
    private lateinit var buttFavorites: LinearLayout
    private lateinit var buttContribute: LinearLayout
    private lateinit var buttSettings: LinearLayout

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

        buttHome = binding.buttHome
        buttHome.setOnClickListener {
            setCurrentFragment(homeFragment)
            changeColor()
        }
        buttFavorites = binding.buttFavorites
        buttFavorites.setOnClickListener {
            setCurrentFragment(favoritesFragment)
            changeColor()
        }

        buttContribute = binding.buttContribute
        buttContribute.setOnClickListener {
            setCurrentFragment(contributeFragment)
            changeColor()
        }

        buttSettings = binding.buttSettings
        buttSettings.setOnClickListener {
            setCurrentFragment(settingsFragment)
            changeColor()
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragmentMain, fragment)
            commit()
        }

        selectedFragment = newSelectedFragment

        newSelectedFragment = when (fragment) {
            is HomeFragment -> "home"
            is FavoritesFragment -> "favorites"
            is ContributeFragment -> "contribute"
            is SettingsFragment -> "settings"
            else -> "Unknown"
        }
    }

    private fun changeColor() {
        when(newSelectedFragment) {
            "home" -> {
                binding.textviewHouse.setTextColor(ContextCompat.getColor(this,R.color.blue))
                binding.imageviewHouse.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_house_click))
                buttHome.isEnabled = false
            }

            "favorites" -> {
                binding.textviewFavorite.setTextColor(ContextCompat.getColor(this,R.color.blue))
                binding.imageviewFavorite.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_favorite_click))
                buttFavorites.isEnabled = false
            }

            "contribute" -> {
                binding.textviewDaowload.setTextColor(ContextCompat.getColor(this,R.color.blue))
                binding.imageviewDownload.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_download_click))
                buttContribute.isEnabled = false
            }

            "settings" -> {
                binding.textviewSettings.setTextColor(ContextCompat.getColor(this,R.color.blue))
                binding.imageviewSettings.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_settings_click))
                buttSettings.isEnabled = false
            }
        }

        when(selectedFragment) {
            "home" -> {
                binding.textviewHouse.setTextColor(ContextCompat.getColor(this,R.color.black))
                binding.imageviewHouse.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_house))
                buttHome.isEnabled = true
            }

            "favorites" -> {
                binding.textviewFavorite.setTextColor(ContextCompat.getColor(this,R.color.black))
                binding.imageviewFavorite.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_favorite))
                buttFavorites.isEnabled = true
            }

            "contribute" -> {
                binding.textviewDaowload.setTextColor(ContextCompat.getColor(this,R.color.black))
                binding.imageviewDownload.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_download))
                buttContribute.isEnabled = true
            }

            "settings" -> {
                binding.textviewSettings.setTextColor(ContextCompat.getColor(this,R.color.black))
                binding.imageviewSettings.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_settings))
                buttSettings.isEnabled = true
            }
        }
    }
}

