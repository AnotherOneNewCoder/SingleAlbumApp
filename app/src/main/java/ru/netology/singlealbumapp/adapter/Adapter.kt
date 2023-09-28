package ru.netology.singlealbumapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.singlealbumapp.R
import ru.netology.singlealbumapp.databinding.TrackBinding
import ru.netology.singlealbumapp.dto.Track

interface OnInteractionListener {
    fun onPress(track: Track) {}
}



class Adapter(
    private val interactionListener: OnInteractionListener,
): ListAdapter<Track, TrackViewHolder>(TrackDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = TrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding, interactionListener)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = getItem(position)
        holder.playOrStopView(track)
    }

}


class TrackDiffCallback: DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean =
        oldItem == newItem

}

class TrackViewHolder(
    private val binding: TrackBinding,
    private val interactionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun playOrStopView(track: Track) {
        with(binding) {
            trackName.text = track.file
            playItem.setImageResource(
                if (track.isPlaying) {
                    R.drawable.ic_pause
                } else {
                  R.drawable.ic_play
                }
            )
            trackTime.text = track.duration ?: ""
            addListener(track)

        }
    }

    private fun addListener(track: Track) {
        binding.apply {
            playItem.setOnClickListener {
                interactionListener.onPress(track)
            }
        }
    }
}