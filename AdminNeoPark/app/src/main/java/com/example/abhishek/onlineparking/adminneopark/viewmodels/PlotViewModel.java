package com.example.abhishek.onlineparking.adminneopark.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.abhishek.onlineparking.adminneopark.models.PlotModel;
import com.example.abhishek.onlineparking.adminneopark.repositories.PlotRepository;

import java.util.List;

public class PlotViewModel extends ViewModel {
    private final PlotRepository repository = new PlotRepository();
    private final LiveData<List<PlotModel>> plotsLiveData = repository.getPlotsLiveData();

    public LiveData<List<PlotModel>> getPlots() {
        return plotsLiveData;
    }

    public void addPlot(PlotModel plot) {
        repository.addPlot(plot);
    }

    public void deletePlot(PlotModel plotModel) {
        repository.deletePlot(plotModel);
    }

    public void updatePlot(PlotModel plot) {
        repository.updatePlot(plot);
    }

}

