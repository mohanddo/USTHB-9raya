package com.example.usthb9raya


    import android.content.Intent
    import android.os.Bundle
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import com.example.usthb9raya.databinding.ActivitySplashScreenBinding


class SplashScreen : AppCompatActivity() {

        private lateinit var binding: ActivitySplashScreenBinding

        override fun onCreate(savedInstanceState: Bundle?) {

            super.onCreate(savedInstanceState)

            binding = ActivitySplashScreenBinding.inflate(layoutInflater)
            val view = binding.root
            setContentView(view)

            val thread = object : Thread() {
                override fun run() {
                    try {
                        sleep(3000)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        val toMain = Intent(this@SplashScreen, MainActivity::class.java)
                        startActivity(toMain)
                    }
                }
            }
            thread.start()

            enableEdgeToEdge()

        }
    }
