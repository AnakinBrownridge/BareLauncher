/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.android.launcher3;

public final class BuildConfig {
    public static final String APPLICATION_ID = "com.android.launcher3";

    public static final boolean IS_STUDIO_BUILD = false;
    public static final boolean QSB_ON_FIRST_SCREEN = false;
    public static final boolean WIDGET_ON_FIRST_SCREEN = false;
    public static final boolean IS_DEBUG_DEVICE = false;

    // Bare Launcher keeps the home screen and app drawer lightweight by omitting
    // optional widget and notification-dot infrastructure.
    public static final boolean WIDGETS_ENABLED = false;
    public static final boolean NOTIFICATION_DOTS_ENABLED = false;
}
