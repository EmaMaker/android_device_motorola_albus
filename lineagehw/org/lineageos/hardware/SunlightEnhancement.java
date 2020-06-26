/*
 * Copyright (C) 2014 The CyanogenMod Project
 * Copyright (C) 2017-2020 The LineageOS Project
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

package org.lineageos.hardware;

import android.util.Log;

import org.lineageos.internal.util.FileUtils;
import vendor.lineage.livedisplay.V1_0.Feature;

/**
 * Facemelt mode!
 */
public class SunlightEnhancement {

    private static final String TAG = "SunlightEnhancement";

    private static final String FACEMELT_PATH = getFacemeltPath();
    private static final String FACEMELT_MODE = getFacemeltMode();

    private static final String FILE_HBM = "/sys/class/graphics/fb0/hbm";
    private static final String FILE_SRE = "/sys/class/graphics/fb0/sre";

    private static final boolean sHasNativeSupport =
            LiveDisplayVendorImpl.getInstance().hasNativeFeature(Feature.OUTDOOR_MODE);

    private static String getFacemeltPath() {
        if (FileUtils.fileExists(FILE_HBM)) {
            return FILE_HBM;
        } else {
            return FILE_SRE;
        }
    }

    private static String getFacemeltMode() {
        if (FileUtils.fileExists(FILE_HBM)) {
            return "1";
        } else {
            return "2";
        }
    }

    /**
     * Whether device supports sunlight enhancement
     *
     * @return boolean Supported devices must return always true
     */
    public static boolean isSupported() {
        if (sHasNativeSupport) {
            return true;
        }

        return FileUtils.isFileWritable(FACEMELT_PATH);
    }

    /**
     * This method return the current activation status of sunlight enhancement
     *
     * @return boolean Must be false when sunlight enhancement is not supported or not activated,
     * or the operation failed while reading the status; true in any other case.
     */
    public static boolean isEnabled() {
        try {
            if (sHasNativeSupport) {
                return LiveDisplayVendorImpl.getInstance().isOutdoorModeEnabled();
            }
            return Integer.parseInt(FileUtils.readOneLine(FACEMELT_PATH)) > 0;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    /**
     * This method allows to setup sunlight enhancement
     *
     * @param status The new sunlight enhancement status
     * @return boolean Must be false if sunlight enhancement is not supported or the operation
     * failed; true in any other case.
     */
    public static boolean setEnabled(boolean status) {
        if (sHasNativeSupport) {
            return LiveDisplayVendorImpl.getInstance().setOutdoorModeEnabled(status);
        }

        return FileUtils.writeLine(FACEMELT_PATH, status ? FACEMELT_MODE : "0");
    }

    /**
     * Whether adaptive backlight (CABL / CABC) is required to be enabled
     *
     * @return boolean False if adaptive backlight is not a dependency
     */
    public static boolean isAdaptiveBacklightRequired() {
        return false;
    }

    /**
     * Set this to true if the implementation is self-managed and does
     * it's own ambient sensing. In this case, setEnabled is assumed
     * to toggle the feature on or off, but not activate it. If set
     * to false, LiveDisplay will call setEnabled when the ambient lux
     * threshold is crossed.
     *
     * @return true if this enhancement is self-managed
     */
    public static boolean isSelfManaged() {
        return false;
    }
}
