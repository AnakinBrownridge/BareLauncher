package com.android.launcher3.settings;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.android.launcher3.R;

public class SystemInfoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setPadding(32, 32, 32, 32);
        textView.setTextSize(16f);
        textView.setMovementMethod(new ScrollingMovementMethod());
        setContentView(textView);
        setTitle(R.string.system_info_title);

        try {
            textView.setText(readSystemInfo());
        } catch (IOException e) {
            textView.setText(R.string.system_info_title);
        }
    }

    private String readSystemInfo() throws IOException {
        File scriptDir = new File(getCacheDir(), "barelauncher");
        if (!scriptDir.exists() && !scriptDir.mkdirs()) {
            throw new IOException("Unable to create temp script dir");
        }

        File scriptFile = new File(scriptDir, "SysVer.sh");
        try (InputStream in = getResources().openRawResource(R.raw.sysver);
             FileOutputStream out = new FileOutputStream(scriptFile)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }

        if (!scriptFile.setExecutable(true, false)) {
            throw new IOException("Unable to chmod script");
        }

        Process process = new ProcessBuilder("/system/bin/sh", scriptFile.getAbsolutePath())
                .redirectErrorStream(true)
                .start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append('\n');
            }
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return "System information script exited with code " + exitCode + "\n" + output;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return output.toString();
        }

        return output.toString();
    }
}
