/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2019 The LineageOS Project
 *               2020-2023 Paranoid Android
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

package co.aospa.glyph.Settings;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.CompoundButton;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

import com.android.settingslib.PrimarySwitchPreference;
import com.android.settingslib.widget.MainSwitchPreference;

import co.aospa.glyph.R;
import co.aospa.glyph.Constants.Constants;
import co.aospa.glyph.Manager.SettingsManager;
import co.aospa.glyph.Utils.ServiceUtils;

public class SettingsFragment extends PreferenceFragment implements CompoundButton.OnCheckedChangeListener, 
        Preference.OnPreferenceChangeListener {

    private MainSwitchPreference mSwitchBar;

    private SwitchPreference mFlipPreference;
    private SeekBarPreference mBrightnessPreference;
    private PrimarySwitchPreference mNotifsPreference;
    private PrimarySwitchPreference mCallPreference;
    private SwitchPreference mChargingLevelPreference;
    private SwitchPreference mChargingPowersharePreference;
    private SwitchPreference mVolumeLevelPreference;
    private SwitchPreference mMusicVisualizerPreference;

    private ContentResolver mContentResolver;
    private SettingObserver mSettingObserver;

    private Handler mHandler = new Handler();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.glyph_settings);

        mContentResolver = getActivity().getContentResolver();
        mSettingObserver = new SettingObserver();
        mSettingObserver.register(mContentResolver);

        boolean glyphEnabled = SettingsManager.isGlyphEnabled();

        mSwitchBar = (MainSwitchPreference) findPreference(Constants.GLYPH_ENABLE);
        mSwitchBar.addOnSwitchChangeListener(this);
        mSwitchBar.setChecked(glyphEnabled);

        mFlipPreference = (SwitchPreference) findPreference(Constants.GLYPH_FLIP_ENABLE);
        mFlipPreference.setEnabled(glyphEnabled);
        mFlipPreference.setOnPreferenceChangeListener(this);

        mBrightnessPreference = (SeekBarPreference) findPreference(Constants.GLYPH_BRIGHTNESS);
        mBrightnessPreference.setEnabled(glyphEnabled);
        mBrightnessPreference.setMin(1);
        mBrightnessPreference.setMax(Constants.getBrightnessLevels().length);
        mBrightnessPreference.setValue(SettingsManager.getGlyphBrightnessSetting());
        mBrightnessPreference.setUpdatesContinuously(true);
        mBrightnessPreference.setOnPreferenceChangeListener(this);

        mNotifsPreference = (PrimarySwitchPreference) findPreference(Constants.GLYPH_NOTIFS_ENABLE);
        mNotifsPreference.setChecked(SettingsManager.isGlyphNotifsEnabled());
        mNotifsPreference.setEnabled(glyphEnabled);
        mNotifsPreference.setSwitchEnabled(glyphEnabled);
        mNotifsPreference.setOnPreferenceChangeListener(this);

        mCallPreference = (PrimarySwitchPreference) findPreference(Constants.GLYPH_CALL_ENABLE);
        mCallPreference.setChecked(SettingsManager.isGlyphCallEnabled());
        mCallPreference.setEnabled(glyphEnabled);
        mCallPreference.setSwitchEnabled(glyphEnabled);
        mCallPreference.setOnPreferenceChangeListener(this);

        mChargingLevelPreference = (SwitchPreference) findPreference(Constants.GLYPH_CHARGING_LEVEL_ENABLE);
        mChargingLevelPreference.setEnabled(glyphEnabled);
        mChargingLevelPreference.setOnPreferenceChangeListener(this);

        mChargingPowersharePreference = (SwitchPreference) findPreference(Constants.GLYPH_CHARGING_POWERSHARE_ENABLE);
        mChargingPowersharePreference.setEnabled(glyphEnabled);
        mChargingPowersharePreference.setOnPreferenceChangeListener(this);

        mVolumeLevelPreference = (SwitchPreference) findPreference(Constants.GLYPH_VOLUME_LEVEL_ENABLE);
        mVolumeLevelPreference.setEnabled(glyphEnabled);
        mVolumeLevelPreference.setOnPreferenceChangeListener(this);

        mMusicVisualizerPreference = (SwitchPreference) findPreference(Constants.GLYPH_MUSIC_VISUALIZER_ENABLE);
        mMusicVisualizerPreference.setEnabled(glyphEnabled);
        mMusicVisualizerPreference.setOnPreferenceChangeListener(this);
        if (mMusicVisualizerPreference.isChecked()) {
            mFlipPreference.setEnabled(false);
            //mBrightnessPreference.setEnabled(false);
            mNotifsPreference.setEnabled(false);
            mNotifsPreference.setSwitchEnabled(false);
            mCallPreference.setEnabled(false);
            mCallPreference.setSwitchEnabled(false);
            mChargingLevelPreference.setEnabled(false);
            mVolumeLevelPreference.setEnabled(false);
            mChargingPowersharePreference.setEnabled(false);
        }

        mHandler.post(() -> ServiceUtils.checkGlyphService());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String preferenceKey = preference.getKey();

        if (preferenceKey.equals(Constants.GLYPH_CALL_ENABLE)) {
            SettingsManager.setGlyphCallEnabled(!mCallPreference.isChecked());
        }

        if (preferenceKey.equals(Constants.GLYPH_NOTIFS_ENABLE)) {
            SettingsManager.setGlyphNotifsEnabled(!mNotifsPreference.isChecked());
        }

        if (preferenceKey.equals(Constants.GLYPH_MUSIC_VISUALIZER_ENABLE)) {
            boolean isChecked = mMusicVisualizerPreference.isChecked();
            mFlipPreference.setEnabled(isChecked);
            //mBrightnessPreference.setEnabled(isChecked);
            mNotifsPreference.setEnabled(isChecked);
            mNotifsPreference.setSwitchEnabled(isChecked);
            mCallPreference.setEnabled(isChecked);
            mCallPreference.setSwitchEnabled(isChecked);
            mChargingLevelPreference.setEnabled(isChecked);
            mVolumeLevelPreference.setEnabled(isChecked);
            mChargingPowersharePreference.setEnabled(isChecked);
        }

        mHandler.post(() -> ServiceUtils.checkGlyphService());

        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SettingsManager.enableGlyph(isChecked);

        mSwitchBar.setChecked(isChecked);

        mFlipPreference.setEnabled(isChecked && !mMusicVisualizerPreference.isChecked());
        mBrightnessPreference.setEnabled(isChecked);
        mNotifsPreference.setEnabled(isChecked && !mMusicVisualizerPreference.isChecked());
        mNotifsPreference.setSwitchEnabled(isChecked && !mMusicVisualizerPreference.isChecked());
        mCallPreference.setEnabled(isChecked && !mMusicVisualizerPreference.isChecked());
        mCallPreference.setSwitchEnabled(isChecked && !mMusicVisualizerPreference.isChecked());
        mChargingLevelPreference.setEnabled(isChecked && !mMusicVisualizerPreference.isChecked());
        mChargingPowersharePreference.setEnabled(isChecked && !mMusicVisualizerPreference.isChecked());
        mVolumeLevelPreference.setEnabled(isChecked && !mMusicVisualizerPreference.isChecked());
        mMusicVisualizerPreference.setEnabled(isChecked);

        mHandler.post(() -> ServiceUtils.checkGlyphService());
    }

    @Override
    public void onDestroy() {
        mSettingObserver.unregister(mContentResolver);
        super.onDestroy();
    }

    private class SettingObserver extends ContentObserver {
        public SettingObserver() {
            super(new Handler());
        }

        public void register(ContentResolver cr) {
            cr.registerContentObserver(Settings.Secure.getUriFor(
                Constants.GLYPH_CALL_ENABLE), false, this);
            cr.registerContentObserver(Settings.Secure.getUriFor(
                Constants.GLYPH_NOTIFS_ENABLE), false, this);
        }

        public void unregister(ContentResolver cr) {
            cr.unregisterContentObserver(this);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (uri.equals(Settings.Secure.getUriFor(Constants.GLYPH_CALL_ENABLE))) {
                mCallPreference.setChecked(SettingsManager.isGlyphCallEnabled());
            }
            if (uri.equals(Settings.Secure.getUriFor(Constants.GLYPH_NOTIFS_ENABLE))) {
                mNotifsPreference.setChecked(SettingsManager.isGlyphNotifsEnabled());
            }
        }
    }
}
