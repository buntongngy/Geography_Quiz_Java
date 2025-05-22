package com.example.geography_quiz_java.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.geography_quiz_java.ExplorerAdapter;
import com.example.geography_quiz_java.ExplorerItem;
import com.example.geography_quiz_java.R;
import com.example.geography_quiz_java.quiz.CityQuiz;
import com.example.geography_quiz_java.quiz.FlagQuiz;
import com.example.geography_quiz_java.quiz.LanguageQuiz;

import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION_CODES.R;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.example.geography_quiz_java.R.layout.fragment_home, container, false);

        RecyclerView recyclerView = view.findViewById(com.example.geography_quiz_java.R.id.explorerRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        ExplorerAdapter adapter = new ExplorerAdapter(getExplorerItems(), item -> {
            switch (item.getTitle()) {
                case "Capital City":
                    Intent cityIntent = new Intent(requireContext(), CityQuiz.class);
                    startActivity(cityIntent);
                    break;
                case "Country Language":
                    Intent languageIntent = new Intent(requireContext(), LanguageQuiz.class);
                    startActivity(languageIntent);
                    break;
                case "Flag Quiz":
                    Intent flagIntent = new Intent(requireContext(), FlagQuiz.class);
                    startActivity(flagIntent);
                    break;
                case "Landmark":
                    Intent landmarkIntent = new Intent(requireContext(), com.example.geography_quiz_java.quiz.LandmarkQuiz.class);
                    startActivity(landmarkIntent);
                    break;
            }
        });

        recyclerView.setAdapter(adapter);
        return view;
    }

    private List<ExplorerItem> getExplorerItems() {
        List<ExplorerItem> items = new ArrayList<>();
        items.add(new ExplorerItem("Flag Quiz", com.example.geography_quiz_java.R.drawable.ic_flag));
        items.add(new ExplorerItem("Capital City", com.example.geography_quiz_java.R.drawable.ic_city));
        items.add(new ExplorerItem("Country Currency", com.example.geography_quiz_java.R.drawable.ic_exchange));
        items.add(new ExplorerItem("Country Language", com.example.geography_quiz_java.R.drawable.ic_language));
        items.add(new ExplorerItem("Country Shape", com.example.geography_quiz_java.R.drawable.ic_shape));
        items.add(new ExplorerItem("Landmark", com.example.geography_quiz_java.R.drawable.ic_landmark));
        return items;
    }
}