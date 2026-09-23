package com.android.launcher3.settings;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.android.launcher3.BareLauncherBuild;
import com.android.launcher3.R;

public class AboutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setPadding(32, 32, 32, 32);
        textView.setTextSize(18f);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText(getString(R.string.bare_about_summary, BareLauncherBuild.BUILD_NUMBER));
        setContentView(textView);
        setTitle(R.string.about_bare_launcher);
    }
}
