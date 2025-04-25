package com.example.examen.Activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.examen.R
import com.example.examen.adapters.filmAdapter
import com.example.examen.data.film
import com.example.examen.data.filmService
import com.example.examen.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    lateinit var adapter: filmAdapter
    lateinit var binding: ActivityMainBinding

    var filmList: List<film> = listOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = filmAdapter(filmList) { position ->
            val superhero = filmList[position]

            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("film_ID", film.id)
            startActivity(intent)
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)

        searchfilmByName("a")
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)

        val menuItem = menu?.findItem(R.id.action_search)
        val searchView = menuItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchSuperheroesByName(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                return false
            }
        })

        return true
    }

    fun getRetrofit(): filmService {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.omdbapi.com/?i=tt3896198&apikey=54603b0a")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(filmService::class.java)
    }

    fun searchfilmByName(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = getRetrofit()
                val result = service.findfilmByName(query)

                filmList = result.results

                CoroutineScope(Dispatchers.Main).launch {
                    adapter.items = filmList
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}