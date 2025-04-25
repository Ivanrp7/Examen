package com.example.examen.Activities


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.examen.R
import com.example.examen.data.film
import com.example.examen.data.filmService
import com.example.examen.databinding.ActivityDetailBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DetailActivity {

    lateinit var binding: ActivityDetailBinding

    lateinit var film: film

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val id = intent.getStringExtra("film_ID")!!
        getSuperheroById(id)

        binding.navigationBar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_biography -> {
                    binding.appearanceContent.root.visibility = View.GONE
                    binding.statsContent.root.visibility = View.GONE
                    binding.biographyContent.root.visibility = View.VISIBLE
                }
                R.id.action_appearance -> {
                    binding.statsContent.root.visibility = View.GONE
                    binding.biographyContent.root.visibility = View.GONE
                    binding.appearanceContent.root.visibility = View.VISIBLE
                }
                R.id.action_stats -> {
                    binding.biographyContent.root.visibility = View.GONE
                    binding.appearanceContent.root.visibility = View.GONE
                    binding.statsContent.root.visibility = View.VISIBLE
                }
            }
            true
        }

        binding.navigationBar.selectedItemId = R.id.action_biography
    }

    fun loadData() {
        Picasso.get().load(superhero.image.url).into(binding.pictureImageView)

        supportActionBar?.title = superhero.name
        supportActionBar?.subtitle = superhero.biography.realName

        // Biography
        binding.biographyContent.publisherTextView.text = superhero.biography.publisher
        binding.biographyContent.placeOfBirthTextView.text = superhero.biography.placeOfBirth
        binding.biographyContent.alignmentTextView.text = superhero.biography.alignment
        binding.biographyContent.alignmentTextView.setTextColor(getColor(superhero.getAlignmentColor()))

        binding.biographyContent.occupationTextView.text = superhero.work.occupation
        binding.biographyContent.baseTextView.text = superhero.work.base

        // Appearance
        with(superhero.appearance) {
            binding.appearanceContent.raceTextView.text = race
            binding.appearanceContent.genderTextView.text = gender
            binding.appearanceContent.eyeColorTextView.text = eyeColor
            binding.appearanceContent.hairColorTextView.text = hairColor
            binding.appearanceContent.weightTextView.text = getWeightKg()
            binding.appearanceContent.heightTextView.text = getHeightCm()
        }

        // Stats
        with(superhero.powerstats) {
            binding.statsContent.intelligenceTextView.text = "${intelligence.toIntOrNull() ?: 0}"
            binding.statsContent.strengthTextView.text = "${strength.toIntOrNull() ?: 0}"
            binding.statsContent.speedTextView.text = "${speed.toIntOrNull() ?: 0}"
            binding.statsContent.durabilityTextView.text = "${durability.toIntOrNull() ?: 0}"
            binding.statsContent.powerTextView.text = "${power.toIntOrNull() ?: 0}"
            binding.statsContent.combatTextView.text = "${combat.toIntOrNull() ?: 0}"

            binding.statsContent.intelligenceProgressBar.progress = intelligence.toIntOrNull() ?: 0
            binding.statsContent.strengthProgressBar.progress = strength.toIntOrNull() ?: 0
            binding.statsContent.speedProgressBar.progress = speed.toIntOrNull() ?: 0
            binding.statsContent.durabilityProgressBar.progress = durability.toIntOrNull() ?: 0
            binding.statsContent.powerProgressBar.progress = power.toIntOrNull() ?: 0
            binding.statsContent.combatProgressBar.progress = combat.toIntOrNull() ?: 0
        }
    }

    fun getRetrofit(): SuperheroService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.superheroapi.com/api.php/7252591128153666/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(SuperheroService::class.java)
    }

    fun getSuperheroById(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = getRetrofit()
                superhero = service.findSuperheroById(id)

                CoroutineScope(Dispatchers.Main).launch {
                    loadData()
                }
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
    }
}