package com.example.earthquakemonitor.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.earthquakemonitor.DetailActivity
import com.example.earthquakemonitor.DetailActivity.Companion.EARTHQUAKE_LATITUDE_KEY
import com.example.earthquakemonitor.DetailActivity.Companion.EARTHQUAKE_LONGITUDE_KEY
import com.example.earthquakemonitor.DetailActivity.Companion.EARTHQUAKE_MAGNITUDE_KEY
import com.example.earthquakemonitor.DetailActivity.Companion.EARTHQUAKE_PLACE_KEY
import com.example.earthquakemonitor.DetailActivity.Companion.EARTHQUAKE_TIME_KEY
import com.example.earthquakemonitor.Earthquake
import com.example.earthquakemonitor.R
import com.example.earthquakemonitor.api.ApiStatusResponse
import com.example.earthquakemonitor.databinding.ActivityMainBinding

const val SORT_TYPE_KEY = "sortType"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.main_menu_sort_magnitude) {
            viewModel.reloadEartquakes(sortByMagnitude = true)
            Log.d("MAGNITUDE", "Se apreto el boton de warning")
            saveSortType(true)
        } else if (itemId == R.id.main_menu_sort_time) {
            viewModel.reloadEartquakes(sortByMagnitude = false)
            Log.d("TIME", "Se apreto el boton del reloj")
            saveSortType(false)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun saveSortType(sortByMagnitude: Boolean) {
        val prefs = getSharedPreferences("eq_preferences", MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(SORT_TYPE_KEY, sortByMagnitude)
        editor.apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val sortType = getSortType()

        binding.earthQuakeRecycler.layoutManager = LinearLayoutManager(this)
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(application, sortType)
        ).get(MainViewModel::class.java)

        val adapter = EarthquakeAdapter(this)
        binding.earthQuakeRecycler.adapter = adapter

        viewModel.eqList.observe(this, Observer { eqList ->
            adapter.submitList(eqList)

            handleEmptyView(eqList)
        })

        // Rueda de progreso
        viewModel.status.observe(
            this,
            Observer { apiStatusResponse ->
                when (apiStatusResponse) {
                    ApiStatusResponse.LOADING -> binding.loadingWheel.visibility = View.VISIBLE
                    else -> binding.loadingWheel.visibility = View.GONE
                }
            })

        adapter.onItemClickListener = {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(EARTHQUAKE_PLACE_KEY, it.place)
            intent.putExtra(EARTHQUAKE_MAGNITUDE_KEY, it.magnitude)
            intent.putExtra(EARTHQUAKE_LONGITUDE_KEY, it.longitude)
            intent.putExtra(EARTHQUAKE_LATITUDE_KEY, it.latitude)
            intent.putExtra(EARTHQUAKE_TIME_KEY, it.time)
            startActivity(intent)
        }
    }

    private fun getSortType(): Boolean {
        val prefs = getSharedPreferences("eq_preferences", MODE_PRIVATE)
        return prefs.getBoolean(SORT_TYPE_KEY, false)
    }

    private fun handleEmptyView(eqList: MutableList<Earthquake>) {
        if (eqList.isEmpty())
            binding.earthQuakeEmptyView.visibility = View.VISIBLE
        else
            binding.earthQuakeEmptyView.visibility = View.GONE
    }
}