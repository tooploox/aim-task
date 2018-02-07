package com.tooploox.aimtask;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Normally view model would be provided via Dagger and hidden behind an interface.
        // Custom factory is used to have view model accept dependencies in a constructor anyway, instead of using Injection directly.
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass == MainActivityViewModel.class) {
                    Injection injection = Injection.INSTANCE;
                    //noinspection unchecked
                    return (T) new MainActivityViewModel(injection.getUseCaseFactory(), injection.getSchedulers());
                } else {
                    throw new IllegalArgumentException(String.format("Can't create view model for class %s", modelClass.toString()));
                }
            }
        }).get(MainActivityViewModel.class);

        TracksListAdapter tracksListAdapter = new TracksListAdapter(this, viewModel);
        RecyclerView rvTracksList = findViewById(R.id.rvTracksList);
        rvTracksList.setHasFixedSize(true);
        rvTracksList.setAdapter(tracksListAdapter);

        SwipeRefreshLayout swipeToRefreshLayout = findViewById(R.id.srlMain);
        swipeToRefreshLayout.setOnRefreshListener(viewModel::onRefreshRequested);

        // In real life I'd use either Butterknife of Kotlin Android Extensions to find views.
        ImageView ivStationImage = findViewById(R.id.ivStationImage);
        TextView tvStationName = findViewById(R.id.tvStationName);
        TextView tvStationDescription = findViewById(R.id.tvStationDescription);

        bindScreenRefreshingStatus(swipeToRefreshLayout);
        bindTracksListUpdated(tracksListAdapter);
        bindStationInfo(ivStationImage, tvStationName, tvStationDescription);
    }

    private void bindScreenRefreshingStatus(SwipeRefreshLayout swipeToRefreshLayout) {
        viewModel.getScreenRefreshingStatus().observe(this, status -> {

            if (status == null) {
                return;
            }

            swipeToRefreshLayout.setRefreshing(status.isLoading());

            if (status.isError()) {
                Toast.makeText(this, status.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindTracksListUpdated(TracksListAdapter tracksListAdapter) {
        viewModel.getTracksListUpdated().observe(this, flag -> {
            if (flag == null) {
                return;
            }

            // Normally it would be nice to diff the elements (e.g. using DiffUtils)
            // and notify the adapter only about added/changed elements. Similarly empty screen should be handled somewhere in between.
            flag.runIfSet(tracksListAdapter::notifyDataSetChanged);
        });
    }

    private void bindStationInfo(ImageView ivStationImage, TextView tvStationName, TextView tvStationDescription) {
        viewModel.getStationInfo().observe(this, stationInfo -> {
            if (stationInfo == null) {
                return;
            }

            Glide.with(this)
                    .load(stationInfo.getImageUrl())
                    .into(ivStationImage);

            tvStationName.setText(stationInfo.getName());
            tvStationDescription.setText(stationInfo.getDescription());
        });
    }
}
