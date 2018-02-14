package com.tooploox.aimtask

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.bumptech.glide.Glide
import com.tooploox.aimtask.base.OneOffFlag
import com.tooploox.aimtask.base.Status
import com.tooploox.aimtask.domain.entity.StationInfo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Normally view model would be provided via Dagger and hidden behind an interface.
        // Custom factory is used to have view model accept dependencies in a constructor anyway, instead of using Injection directly.
        viewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass == MainActivityViewModel::class.java) {
                    val injection = Injection

                    @Suppress("UNCHECKED_CAST")
                    return MainActivityViewModel(injection.useCaseFactory, injection.schedulers) as T
                } else {
                    throw IllegalArgumentException(String.format("Can't create view model for class %s", modelClass.toString()))
                }
            }
        }).get(MainActivityViewModel::class.java)

        val tracksListAdapter = TracksListAdapter(this, viewModel)
        rvTracksList.setHasFixedSize(true)
        rvTracksList.adapter = tracksListAdapter

        srlMain.setOnRefreshListener(viewModel::onRefreshRequested)

        render { getScreenRefreshingStatus() } with ::renderScreenRefreshingStatus
        render { tracksListUpdated } with { renderTracksListUpdated(tracksListAdapter, it) }
        render { stationInfo } with ::renderStationInfo
    }

    private fun renderScreenRefreshingStatus(status: Status<Unit>) {
        srlMain.isRefreshing = status is Status.Loading

        if (status is Status.Error) {
            Toast.makeText(this, status.errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun renderTracksListUpdated(tracksListAdapter: TracksListAdapter, flag: OneOffFlag) {
        // Normally it would be nice to diff the elements (e.g. using DiffUtils)
        // and notify the adapter only about added/changed elements. Similarly empty screen should be handled somewhere in between.
        flag.runIfSet(tracksListAdapter::notifyDataSetChanged)
    }

    private fun renderStationInfo(stationInfo: StationInfo) {
        Glide.with(this)
                .load(stationInfo.imageUrl)
                .into(ivStationImage)

        tvStationName.text = stationInfo.name
        tvStationDescription.text = stationInfo.description
    }

    private fun <T> render(liveData: MainActivityViewModel.() -> LiveData<T>): LiveData<T> =
            liveData(viewModel)

    private inline infix fun <T> LiveData<T>.with(crossinline renderer: (T) -> Unit) =
            this.observe(this@MainActivity, Observer { if (it != null) renderer(it) })
}
