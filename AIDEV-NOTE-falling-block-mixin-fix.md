# FallingBlockEntity Mixin Fix for Minecraft 1.21.6

## Issue
The mixin `FallingBlockEntityMixin` was trying to inject into methods `onStartedTrackingBy` and `onStoppedTrackingBy` in the `FallingBlockEntity` class, but these methods are not overridden in `FallingBlockEntity` in Minecraft 1.21.6.

## Root Cause
- In Minecraft 1.21.6, `FallingBlockEntity` does not override `onStartedTrackingBy` and `onStoppedTrackingBy` methods
- These methods are defined in the parent `Entity` class as public methods
- The `@Inject` annotations were targeting non-existent methods in `FallingBlockEntity`

## Solution
Instead of using `@Inject` annotations to inject into these methods, we:
1. Removed the `@Inject` annotations and their associated methods (`onStartTracking` and `onStopTracking`)
2. Moved the logic directly into the overridden methods that were already present in the mixin

This way, the mixin properly overrides the methods from the `Entity` class and adds the custom logic without relying on injection into non-existent methods.

## Changes Made
- Removed lines 94-102 (the @Inject methods)
- Added `this.polymc$addPlayer(player);` to the overridden `onStartedTrackingBy` method
- Added `this.polymc$removePlayer(player);` to the overridden `onStoppedTrackingBy` method