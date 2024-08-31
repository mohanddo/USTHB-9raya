package com.example.usthb9raya


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.usthb9raya.databinding.ActivityMainBinding
import com.example.usthb9raya.fragments.FavoritesFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var selectedFragment = "home"
    private var newSelectedFragment = "home"
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
            changeColor()
        }

        binding.buttFavorites.setOnClickListener {
            setCurrentFragment(favoritesFragment)
            changeColor()
        }

        binding.buttContribute.setOnClickListener {
            setCurrentFragment(contributeFragment)
            changeColor()
        }

        binding.buttSettings.setOnClickListener {
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
                binding.textviewHouse.setTextColor(ContextCompat.getColor(this,R.color.light_blue))
                binding.imageviewHouse.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_house_click))
            }

            "favorites" -> {
                binding.textviewFavorite.setTextColor(ContextCompat.getColor(this,R.color.light_blue))
                binding.imageviewFavorite.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_favorite_click))
            }

            "contribute" -> {
                binding.textviewDaowload.setTextColor(ContextCompat.getColor(this,R.color.light_blue))
                binding.imageviewDownload.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_download_click))
            }

            "settings" -> {
                binding.textviewSettings.setTextColor(ContextCompat.getColor(this,R.color.light_blue))
                binding.imageviewSettings.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_settings_click))
            }
        }

        when(selectedFragment) {
            "home" -> {
                binding.textviewHouse.setTextColor(ContextCompat.getColor(this,R.color.black))
                binding.imageviewHouse.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_house))
            }

            "favorites" -> {
                binding.textviewFavorite.setTextColor(ContextCompat.getColor(this,R.color.black))
                binding.imageviewFavorite.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_favorite))
            }

            "contribute" -> {
                binding.textviewDaowload.setTextColor(ContextCompat.getColor(this,R.color.black))
                binding.imageviewDownload.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_download))
            }

            "settings" -> {
                binding.textviewSettings.setTextColor(ContextCompat.getColor(this,R.color.black))
                binding.imageviewSettings.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_settings))
            }
        }
    }
}
