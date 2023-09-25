package ru.netology.singlealbumapp.activity

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import ru.netology.singlealbumapp.R
import ru.netology.singlealbumapp.adapter.Adapter
import ru.netology.singlealbumapp.adapter.OnInteractionListener
import ru.netology.singlealbumapp.databinding.ActivityMainBinding
import ru.netology.singlealbumapp.dto.Album
import ru.netology.singlealbumapp.dto.Track
import ru.netology.singlealbumapp.viewmodel.AlbumViewModel
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: Adapter
    private lateinit var recyclerView: RecyclerView

    private val mediaPlayer = MediaPlayer()
    private val viewModel = AlbumViewModel(mediaPlayer)
    private val url =
        "https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/album.json"

    private val interactionListener = object : OnInteractionListener {
        override fun onPress(track: Track) {
            viewModel.onPlayTrack(track)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerView = binding.listItem
        adapter = Adapter(interactionListener)
        recyclerView.adapter = adapter
        request()
        flowData()
        listener()


    }

        private fun flowData() {
        lifecycleScope.launchWhenCreated {
            viewModel.isPlaying.collectLatest {
                if (mediaPlayer.isPlaying) {
                    binding.playAlbum.setImageResource(R.drawable.ic_pause)
                } else {
                    binding.playAlbum.setImageResource(R.drawable.ic_play)
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.dataAlbumTracks.collectLatest {
                adapter.submitList(it)
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.isPlayingCurrentPosition.collectLatest {
                if (it in 1..9999999) {
                    val minuteDur = it / 60_000
                    val secondDur = (it / 1000) - (minuteDur * 60)
                    val currentPosition =
                        "$minuteDur:${if (secondDur < 10) "0" else ""}$secondDur"
                    binding.soundCurrentPosition.text = currentPosition

                }
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.isPlayingDuration.collectLatest {
                if (it in 1..9999999) {
                    val minuteDur = it / 60_000
                    val secondDur = (it / 1_000) - (minuteDur * 60)
                    val duration = "/ $minuteDur:${if (secondDur < 10) "0" else ""}$secondDur"
                    binding.soundDuration.text = duration
                }
            }
        }
    }
//    private fun flowData() {
//        lifecycleScope.launchWhenCreated {
//            viewModel.isPlaying.collectLatest {
//                if (mediaPlayer.isPlaying) {
//                    binding.playAlbum.setImageResource(R.drawable.ic_pause)
//                } else {
//                    binding.playAlbum.setImageResource(R.drawable.ic_play)
//                }
//            }
//
//
//            viewModel.dataAlbumTracks.collectLatest {
//                adapter.submitList(it)
//            }
//
//
//            viewModel.isPlayingCurrentPosition.collectLatest {
//                if (it in 1..9999999) {
//                    val minuteDur = it / 60_000
//                    val secondDur = (it / 1000) - (minuteDur * 60)
//                    val currentPosition =
//                        "$minuteDur:${if (secondDur < 10) "0" else ""}$secondDur"
//                    binding.soundCurrentPosition.text = currentPosition
//
//                }
//            }
//
//
//            viewModel.isPlayingDuration.collectLatest {
//                if (it in 1..9999999) {
//                    val minuteDur = it / 60_000
//                    val secondDur = (it / 1_000) - (minuteDur * 60)
//                    val duration = "/ $minuteDur:${if (secondDur < 10) "0" else ""}$secondDur"
//                    binding.soundDuration.text = duration
//                }
//            }
//        }
//    }

    private fun listener() {
        binding.playAlbum.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.start()
            }

        }
    }

    private fun request() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/album.json")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        "Failed to connect",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            override fun onResponse(call: Call, response: Response) {
                try {


                    val json = response.body?.string()
                    val album2 = Gson().fromJson(json, Album::class.java)

                    val album = Gson().fromJson(json, Album::class.java)
                    val tracks = album.tracks
                    viewModel.setTracks(tracks)
                    runOnUiThread {
                        binding.apply {
                            albumName.text = album.title
                            artistName.text = album.artist
                            genreName.text = album.genre
                            yearName.text = album.published
                        }


                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }

        })
    }

}