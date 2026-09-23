package com.android.launcher3.settings;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.launcher3.R;

public class BareLauncherSettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);

        TextView title = new TextView(this);
        title.setText(R.string.bare_settings_title);
        title.setTextSize(28f);
        title.setTextColor(Color.WHITE);
        root.addView(title);

        Switch oneUiGrid = new Switch(this);
        oneUiGrid.setText(R.string.bare_feature_oneui_grid);
        oneUiGrid.setChecked(true);
        root.addView(oneUiGrid);

        Switch wallpaperContainer = new Switch(this);
        wallpaperContainer.setText(R.string.bare_feature_wallpapers);
        wallpaperContainer.setChecked(true);
        root.addView(wallpaperContainer);

        Switch smartWidgets = new Switch(this);
        smartWidgets.setText(R.string.bare_feature_smart_widgets);
        smartWidgets.setChecked(true);
        root.addView(smartWidgets);

        Switch appShortcuts = new Switch(this);
        appShortcuts.setText(R.string.bare_feature_app_shortcuts);
        appShortcuts.setChecked(true);
        root.addView(appShortcuts);

        Button systemInfoButton = new Button(this);
        systemInfoButton.setText(R.string.system_info_title);
        systemInfoButton.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, SystemInfoActivity.class));
        });
        root.addView(systemInfoButton);

        Button aboutButton = new Button(this);
        aboutButton.setText(R.string.about_bare_launcher);
        aboutButton.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, AboutActivity.class));
        });
        root.addView(aboutButton);

        setContentView(root);
        setTitle(R.string.bare_settings_title);
    }
}
