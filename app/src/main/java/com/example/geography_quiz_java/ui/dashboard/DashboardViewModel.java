package com.example.geography_quiz_java.ui.dashboard;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> _text = new MutableLiveData<>();
    private final LiveData<String> text = _text;

    public DashboardViewModel() {
        _text.setValue("This is dashboard Fragment");
    }

    public LiveData<String> getText() {
        return text;
    }
}