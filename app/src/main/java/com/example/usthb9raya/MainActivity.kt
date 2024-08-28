package com.example.usthb9raya

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
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

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Home"


        val homeFragment = HomeFragment()
        val favoritesFragment = FavoritesFragment()
        val contributeFragment = ContributeFragment()
        val settingsFragment = SettingsFragment()

        binding.buttHome.setOnClickListener {
            setCurrentFragment(homeFragment)

            binding.textviewHouse.setTextColor(ContextCompat.getColor(this,R.color.light_blue))
            binding.imageviewHouse.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_house_click))

            binding.textviewFavorite.setTextColor(ContextCompat.getColor(this,R.color.black))
            binding.imageviewFavorite.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_favorite))

            binding.textviewDaowload.setTextColor(ContextCompat.getColor(this,R.color.black))
            binding.imageviewDownload.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_download))

            binding.textviewSettings.setTextColor(ContextCompat.getColor(this,R.color.black))
            binding.imageviewSettings.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_settings))

        }

        binding.buttFavorites.setOnClickListener {
            setCurrentFragment(favoritesFragment)

            binding.textviewHouse.setTextColor(ContextCompat.getColor(this,R.color.black))
            binding.imageviewHouse.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_house))

            binding.textviewFavorite.setTextColor(ContextCompat.getColor(this,R.color.light_blue))
            binding.imageviewFavorite.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_favorite_click))

            binding.textviewDaowload.setTextColor(ContextCompat.getColor(this,R.color.black))
            binding.imageviewDownload.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_download))

            binding.textviewSettings.setTextColor(ContextCompat.getColor(this,R.color.black))
            binding.imageviewSettings.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_settings))
        }

        binding.buttContribute.setOnClickListener {
            setCurrentFragment(contributeFragment)

            binding.textviewHouse.setTextColor(ContextCompat.getColor(this,R.color.black))
            binding.imageviewHouse.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_house))

            binding.textviewFavorite.setTextColor(ContextCompat.getColor(this,R.color.black))
            binding.imageviewFavorite.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_favorite))

            binding.textviewDaowload.setTextColor(ContextCompat.getColor(this,R.color.light_blue))
            binding.imageviewDownload.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_download_click))

            binding.textviewSettings.setTextColor(ContextCompat.getColor(this,R.color.black))
            binding.imageviewSettings.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_settings))
        }

        binding.buttSettings.setOnClickListener {
            setCurrentFragment(settingsFragment)

            binding.textviewHouse.setTextColor(ContextCompat.getColor(this,R.color.black))
            binding.imageviewHouse.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_house))

            binding.textviewFavorite.setTextColor(ContextCompat.getColor(this,R.color.black))
            binding.imageviewFavorite.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_favorite))

            binding.textviewDaowload.setTextColor(ContextCompat.getColor(this,R.color.black))
            binding.imageviewDownload.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_download))

            binding.textviewSettings.setTextColor(ContextCompat.getColor(this,R.color.light_blue))
            binding.imageviewSettings.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_settings_click))
        }


    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragmentMain, fragment)
            commit()
        }
    }
}
