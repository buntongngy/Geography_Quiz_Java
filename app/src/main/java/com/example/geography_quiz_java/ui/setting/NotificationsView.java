package com.example.geography_quiz_java.ui.setting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsView extends ViewModel {

    private final MutableLiveData<String> _text = new MutableLiveData<>();
    private final LiveData<String> text = _text;

    public NotificationsView()
    {
        _text.setValue("This is notification model");
    }

    public LiveData<String> getText()
    {
        return text;
    }

}
