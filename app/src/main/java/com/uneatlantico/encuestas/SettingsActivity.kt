package com.uneatlantico.encuestas

import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.preference.PreferenceActivity
import android.util.Log
import android.widget.Button



class SettingsActivity : PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Add a button to the header list.
        if (hasHeaders()) {
            val button = Button(this)
            button.text = "Some action"
            setListFooter(button)
        }
    }

    /**
     * Populate the activity with the top-level headers.
     */
    override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
        loadHeadersFromResource(R.xml.preference_headers, target)
    }

    /**
     * This fragment shows the preferences for the first header.
     */
    class PreferencesFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Make sure default values are applied.  In a real app, you would
            // want this in a shared function that is used to retrieve the
            // SharedPreferences wherever they are needed.
            //PreferenceManager.setDefaultValues(activity, R.xml.advanced_preferences, false)

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.fragmented_preferences)
        }
    }

    /**
     * This fragment contains a second-level set of preference that you
     * can get to by tapping an item in the first preferences fragment.
     */
    class PreferencesFragmentInner : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Can retrieve arguments from preference XML.
            Log.i("args", "Arguments: $arguments")

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.fragmented_preferences)
        }
    }

    /**
     * This fragment shows the preferences for the second header.
     */
    class MiscFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Can retrieve arguments from headers XML.
            Log.i("args", "Arguments: $arguments")

            // Load the preferences from an XML resource
            //addPreferencesFromResource(R.xml.preference_dependencies)
        }
    }
}