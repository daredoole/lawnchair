/*
 * Copyright (C) 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.wm.shell.shared.pip

import android.app.AppGlobals
import android.content.pm.PackageManager
import android.window.DesktopExperienceFlags
import com.android.wm.shell.Flags

class PipFlags {
    companion object {
        /**
         * Returns true if PiP2 implementation should be used. Special note: if PiP on Desktop
         * Windowing is enabled, override the PiP2 gantry flag to be ON.
         */
        @JvmStatic
        val isPip2ExperimentEnabled: Boolean by lazy {
            val isTv = AppGlobals.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_LEANBACK, 0)
            (Flags.enablePip2() || isDesktopWindowingPipEnabled) && !isTv
        }

        @JvmStatic
        val isPipUmoExperienceEnabled: Boolean by lazy {
            Flags.enablePipUmoExperience()
        }

        // LC-Note: ENABLE_DESKTOP_WINDOWING_PIP is a hidden platform field whose presence drifts
        // across Android 16 builds -- a direct reference throws NoSuchFieldError on this device's
        // framework.jar and kills the launcher process on every RecentsView.finishRecentsAnimation
        // call (i.e. every Overview exit). Same reflective-with-safe-fallback pattern used
        // elsewhere in this fork (TaskbarActivityContext#isFlagTrueSafe).
        @JvmStatic
        val isDesktopWindowingPipEnabled: Boolean by lazy {
            runCatching {
                val flag = DesktopExperienceFlags::class.java
                    .getField("ENABLE_DESKTOP_WINDOWING_PIP")
                    .get(null)
                flag.javaClass.getMethod("isTrue").invoke(flag) as Boolean
            }.getOrDefault(false)
        }
    }
}
