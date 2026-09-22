# Bare Launcher

Bare Launcher is a deliberately minimal Android home-screen launcher based on LineageOS Trebuchet.

It provides the essentials for a fast, clean home screen while avoiding the extra services and visual features that increase background memory use. It is intended for low-memory devices and everyday use, including school and work.

## Goals

- Stay small, responsive, and quiet in the background.
- Keep the home screen and app drawer simple.
- Avoid claiming a fixed memory percentage: Android memory use varies by device, installed apps, screen size, and whether the launcher is serving widgets or recents.

## Memory notes

The build disables optional launcher features that are not part of the Bare experience and avoids requesting a large heap. A target such as 2.5% of total device memory must be validated on real hardware; it cannot be guaranteed by an Android application because the operating system reports and accounts for memory differently across devices.

For meaningful measurements, compare the same device and build after a cold start and after normal launcher use. Record both the proportional value and the process PSS from `dumpsys meminfo`.

## Source

Bare Launcher is a continuation of the private Bare Launcher app and is built from the LineageOS Trebuchet codebase.
