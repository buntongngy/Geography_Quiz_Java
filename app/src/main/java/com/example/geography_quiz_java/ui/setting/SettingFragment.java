package com.example.geography_quiz_java.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.geography_quiz_java.LocaleHelper;
import com.example.geography_quiz_java.MainActivity;
import com.example.geography_quiz_java.databinding.FragmentSettingBinding;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;
    private SharedPreferences sharedPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Load current language from preferences
        sharedPref = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String currentLanguage = sharedPref.getString("app_language", "en");
        if (currentLanguage == null) {
            currentLanguage = "en";
        }
        binding.currentLanguage.setText(getLanguageDisplayName(currentLanguage));

        // Set up language selection
        binding.languageOption.setOnClickListener(v -> showLanguageDialog());
    }

    private void showLanguageDialog() {
        Map<String, String> languages = new HashMap<>();
        languages.put("English", "en");
        languages.put("Français", "fr");
        languages.put("Español", "es");
        languages.put("Deutsch", "de");
        languages.put("日本語", "ja");
        languages.put("ខ្មែរ", "kh");

        String currentLanguageCode = sharedPref.getString("app_language", "en");
        if (currentLanguageCode == null) {
            currentLanguageCode = "en";
        }

        String currentLanguageName = "English";
        for (Map.Entry<String, String> entry : languages.entrySet()) {
            if (entry.getValue().equals(currentLanguageCode)) {
                currentLanguageName = entry.getKey();
                break;
            }
        }

        String[] languageNames = languages.keySet().toArray(new String[0]);
        int checkedItem = -1;
        for (int i = 0; i < languageNames.length; i++) {
            if (languageNames[i].equals(currentLanguageName)) {
                checkedItem = i;
                break;
            }
        }
        checkedItem = Math.max(checkedItem, 0);

        new AlertDialog.Builder(requireContext())
                .setTitle("Select Language")
                .setSingleChoiceItems(
                        languageNames,
                        checkedItem,
                        (dialog, which) -> {
                            String selectedLanguageCode = languages.get(languageNames[which]);
                            setAppLanguage(selectedLanguageCode);
                            dialog.dismiss();
                        })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setAppLanguage(String languageCode) {
        // Save preference
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("app_language", languageCode);
        editor.apply();

        // Update UI immediately
        binding.currentLanguage.setText(getLanguageDisplayName(languageCode));

        // Change app locale
        updateLocale(languageCode);

        // Restart app to apply changes
        restartApp();
    }

    private void updateLocale(String languageCode) {
        // Update application context
        Context appContext = requireContext().getApplicationContext();
        LocaleHelper.setLocale(appContext, languageCode);

        // Update activity context
        if (getActivity() != null) {
            Context updatedContext = LocaleHelper.setLocale(getActivity(), languageCode);
            getActivity().getResources().updateConfiguration(
                    updatedContext.getResources().getConfiguration(),
                    updatedContext.getResources().getDisplayMetrics()
            );
        }
    }

    private void restartApp() {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private String getLanguageDisplayName(String languageCode) {
        switch (languageCode) {
            case "en":
                return "English";
            case "fr":
                return "Français";
            case "es":
                return "Español";
            case "de":
                return "Deutsch";
            case "ja":
                return "日本語";
            case "kh":
                return "ខ្មែរ";
            default:
                return "English";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}