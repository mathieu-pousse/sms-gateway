package com.zenika.sms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import static android.content.DialogInterface.BUTTON_NEUTRAL;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }


    /**
     * This fragment shows the preferences for the first header.
     */
    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private EditTextPreference portNumber;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            portNumber = (EditTextPreference) findPreference("listener.port-number");
            portNumber.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Boolean rtnval = true;
                    try {
                        int port = Integer.valueOf((String) newValue);
                        if (port < 1 || port > 65535) {
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                            alertDialog.setTitle("Invalid");
                            alertDialog.setMessage("Port number must be between 1 to 65535.");
                            alertDialog.setButton(BUTTON_NEUTRAL, getString(android.R.string.ok), (DialogInterface.OnClickListener) null);
                            alertDialog.show();
                            return false;
                        }
                    } catch (NumberFormatException nfe) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Invalid");
                        alertDialog.setMessage("Should be a number.");
                        alertDialog.setButton(BUTTON_NEUTRAL, getString(android.R.string.ok), (DialogInterface.OnClickListener) null);
                        alertDialog.show();
                        return false;
                    }
                    Log.e("SMS-SERVER", newValue.getClass().getName());
                    return rtnval;
                }
            });
        }


        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
            // Let's do something a preference value changes
            if (key.equals("listener.port-number")) {
                setupSummary();
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Configuration");
                alertDialog.setMessage("This parameter will be taken into account at next start up of the application.\n\n Do you want to restart " + "the application?");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int which) {
                        Intent i = getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int which) {
                    }
                });
                alertDialog.show();

            }
        }

        private void setupSummary() {
            portNumber.setSummary("Listening on " + PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("listener.port-number",
                    "8888"));
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            setupSummary();
        }
    }

}