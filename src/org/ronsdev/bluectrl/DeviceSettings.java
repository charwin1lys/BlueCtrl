/*
 * Copyright (C) 2012
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

package org.ronsdev.bluectrl;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Locale;

/**
 * Manages Bluetooth device specific settings.
 */
public class DeviceSettings {

    public static final String PREF_KEY_KEYMAP = "keymap";
    public static final String PREF_KEY_TOUCHPAD_BUTTONS = "touchpad_buttons";
    public static final String PREF_KEY_MOUSE_SENSITIVITY = "mouse_sensitivity";
    public static final String PREF_KEY_SCROLL_SENSITIVITY = "scroll_sensitivity";
    public static final String PREF_KEY_INVERT_SCROLL = "invert_scroll";
    public static final String PREF_KEY_FLING_SCROLL = "fling_scroll";
    public static final String PREF_KEY_FORCE_SMOOTH_SCROLL = "force_smooth_scroll";

    public static final String TOUCHPAD_BUTTONS_SHOW = "show";
    public static final String TOUCHPAD_BUTTONS_SHOW_PORTRAIT = "show_portrait";
    public static final String TOUCHPAD_BUTTONS_HIDE = "hide";

    public static final String DEFAULT_KEYMAP = "en_US";
    public static final String DEFAULT_TOUCHPAD_BUTTONS = TOUCHPAD_BUTTONS_SHOW_PORTRAIT;
    public static final float DEFAULT_MOUSE_SENSITIVITY = 3f;
    public static final float DEFAULT_SCROLL_SENSITIVITY = 2.5f;
    public static final boolean DEFAULT_INVERT_SCROLL = false;
    public static final boolean DEFAULT_FLING_SCROLL = true;
    public static final boolean DEFAULT_FORCE_SMOOTH_SCROLL = false;


    private static Context sContext = null;
    private static HashMap<String, DeviceSettings> sDeviceSettingsList = null;
    private static String sDefaultKeyMap = null;


    private String mDeviceId;

    private String mKeyMap;
    private String mTouchpadButtons;
    private float mMouseSensitivity;
    private float mScrollSensitivity;
    private boolean mInvertScroll;
    private boolean mFlingScroll;
    private boolean mForceSmoothScroll;


    private static String getDefaultKeyMap() {
        final Locale locale = Locale.getDefault();
        final String localeId = String.format("%s_%s", locale.getLanguage(), locale.getCountry());

        String[] keyMapList = sContext.getResources().getStringArray(R.array.keymap_values);
        for (String keyMap : keyMapList) {
            if (keyMap.equals(localeId)) {
                return keyMap;
            }
        }

        return DEFAULT_KEYMAP;
    }

    private static void initStaticMembers(Context context) {
        if (sContext == null) {
            sContext = context.getApplicationContext();
        }

        if (sDeviceSettingsList == null) {
            sDeviceSettingsList = new HashMap<String, DeviceSettings>();
        }

        if (sDefaultKeyMap == null) {
            sDefaultKeyMap = getDefaultKeyMap();
        }
    }

    private static String getDeviceId(BluetoothDevice device) {
        return device.getAddress().replace(":", "");
    }

    /** Gets a Bluetooth device specific settings object. */
    public static DeviceSettings get(Context context, BluetoothDevice device) {
        initStaticMembers(context);

        final String deviceId = getDeviceId(device);
        if (!sDeviceSettingsList.containsKey(deviceId)) {
            sDeviceSettingsList.put(deviceId, new DeviceSettings(deviceId));
        }

        return sDeviceSettingsList.get(deviceId);
    }


    private DeviceSettings(String deviceId) {
        mDeviceId = deviceId;

        loadFromPreferences();
    }


    /** Converts the given preference key to a Bluetooth Device specific preference key. */
    private String getKey(String key) {
        return String.format("device_%s_%s", mDeviceId, key);
    }

    private void loadFromPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(sContext);

        mKeyMap = preferences.getString(getKey(PREF_KEY_KEYMAP), sDefaultKeyMap);
        mTouchpadButtons = preferences.getString(getKey(PREF_KEY_TOUCHPAD_BUTTONS),
                DEFAULT_TOUCHPAD_BUTTONS);
        mMouseSensitivity = preferences.getFloat(getKey(PREF_KEY_MOUSE_SENSITIVITY),
                DEFAULT_MOUSE_SENSITIVITY);
        mScrollSensitivity = preferences.getFloat(getKey(PREF_KEY_SCROLL_SENSITIVITY),
                DEFAULT_SCROLL_SENSITIVITY);
        mInvertScroll = preferences.getBoolean(getKey(PREF_KEY_INVERT_SCROLL),
                DEFAULT_INVERT_SCROLL);
        mFlingScroll = preferences.getBoolean(getKey(PREF_KEY_FLING_SCROLL),
                DEFAULT_FLING_SCROLL);
        mForceSmoothScroll = preferences.getBoolean(getKey(PREF_KEY_FORCE_SMOOTH_SCROLL),
                DEFAULT_FORCE_SMOOTH_SCROLL);
    }

    public void saveToPreferences() {
        DeviceSettings oldSettings = new DeviceSettings(mDeviceId);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(sContext);
        SharedPreferences.Editor editor = preferences.edit();

        if (!mKeyMap.equals(oldSettings.mKeyMap)) {
            editor.putString(getKey(PREF_KEY_KEYMAP), mKeyMap);
        }
        if (!mTouchpadButtons.equals(oldSettings.mTouchpadButtons)) {
            editor.putString(getKey(PREF_KEY_TOUCHPAD_BUTTONS), mTouchpadButtons);
        }
        if (mMouseSensitivity != oldSettings.mMouseSensitivity) {
            editor.putFloat(getKey(PREF_KEY_MOUSE_SENSITIVITY), mMouseSensitivity);
        }
        if (mScrollSensitivity != oldSettings.mScrollSensitivity) {
            editor.putFloat(getKey(PREF_KEY_SCROLL_SENSITIVITY), mScrollSensitivity);
        }
        if (mInvertScroll != oldSettings.mInvertScroll) {
            editor.putBoolean(getKey(PREF_KEY_INVERT_SCROLL), mInvertScroll);
        }
        if (mFlingScroll != oldSettings.mFlingScroll) {
            editor.putBoolean(getKey(PREF_KEY_FLING_SCROLL), mFlingScroll);
        }
        if (mForceSmoothScroll != oldSettings.mForceSmoothScroll) {
            editor.putBoolean(getKey(PREF_KEY_FORCE_SMOOTH_SCROLL), mForceSmoothScroll);
        }

        editor.commit();
    }

    public void resetPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(sContext);
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove(getKey(PREF_KEY_KEYMAP));
        editor.remove(getKey(PREF_KEY_TOUCHPAD_BUTTONS));
        editor.remove(getKey(PREF_KEY_MOUSE_SENSITIVITY));
        editor.remove(getKey(PREF_KEY_SCROLL_SENSITIVITY));
        editor.remove(getKey(PREF_KEY_INVERT_SCROLL));
        editor.remove(getKey(PREF_KEY_FLING_SCROLL));
        editor.remove(getKey(PREF_KEY_FORCE_SMOOTH_SCROLL));

        editor.commit();

        loadFromPreferences();
    }


    public String getKeyMap() {
        return mKeyMap;
    }
    public void setKeyMap(String value) {
        mKeyMap = value;
    }

    public String getTouchpadButtons() {
        return mTouchpadButtons;
    }
    public void setTouchpadButtons(String value) {
        mTouchpadButtons = value;
    }

    public float getMouseSensitivity() {
        return mMouseSensitivity;
    }
    public void setMouseSensitivity(float value) {
        mMouseSensitivity = value;
    }

    public float getScrollSensitivity() {
        return mScrollSensitivity;
    }
    public void setScrollSensitivity(float value) {
        mScrollSensitivity = value;
    }

    public boolean getInvertScroll() {
        return mInvertScroll;
    }
    public void setInvertScroll(boolean value) {
        mInvertScroll = value;
    }

    public boolean getFlingScroll() {
        return mFlingScroll;
    }
    public void setFlingScroll(boolean value) {
        mFlingScroll = value;
    }

    public boolean getForceSmoothScroll() {
        return mForceSmoothScroll;
    }
    public void setForceSmoothScroll(boolean value) {
        mForceSmoothScroll = value;
    }
}
