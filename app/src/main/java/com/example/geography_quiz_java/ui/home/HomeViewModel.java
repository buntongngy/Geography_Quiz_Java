package com.example.geography_quiz_java.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> _text = new MutableLiveData<>();
    private final LiveData<String> text = _text;

    public HomeViewModel() {
        _text.setValue("This is home Fragment");
    }

    public LiveData<String> getText() {
        return text;
    }
}