package com.example.earthquakemonitor

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.earthquakemonitor.databinding.EarthQuakeListItemBinding

private val TAG = EarthquakeAdapter::class.java.simpleName

class EarthquakeAdapter(private val context: Context) :
    ListAdapter<Earthquake, EarthquakeAdapter.EarthquakeHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Earthquake>() {
        override fun areItemsTheSame(oldItem: Earthquake, newItem: Earthquake): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Earthquake, newItem: Earthquake): Boolean {
            return oldItem == newItem
        }
    }

    lateinit var onItemClickListener: (Earthquake) -> Unit


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EarthquakeAdapter.EarthquakeHolder {
        val binding = EarthQuakeListItemBinding.inflate(LayoutInflater.from(parent.context))
        return EarthquakeHolder(binding)
    }

    override fun onBindViewHolder(holder: EarthquakeAdapter.EarthquakeHolder, position: Int) {
        val earthquake = getItem(position)
        holder.bind(earthquake)
    }

    inner class EarthquakeHolder(private val binding: EarthQuakeListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(earthquake: Earthquake) {
            binding.earthQuakeMagnitudeText.text = context.getString(R.string.magnitude_format, earthquake.magnitude)
            binding.earthQuakePlaceText.text = earthquake.place
            binding.root.setOnClickListener {
                if (::onItemClickListener.isInitialized) {
                    onItemClickListener(earthquake)
                } else {
                    Log.e(TAG, "onItemClickListener no esta inicializado")
                }
            }
        }
    }
}