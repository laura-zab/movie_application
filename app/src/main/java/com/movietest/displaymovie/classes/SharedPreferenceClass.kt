package com.movietest.displaymovie.classes

import android.content.Context
import android.preference.PreferenceManager

object SharedPreferenceClass {
    fun save(context: Context?, valueKey: String?, value: String?) {
        val prefs = PreferenceManager
            .getDefaultSharedPreferences(context)
        val edit = prefs.edit()
        edit.putString(valueKey, value)
        edit.apply()
    }

    fun read(context: Context?, valueKey: String?, valueDefault: String?): String? {
        val prefs = PreferenceManager
            .getDefaultSharedPreferences(context)
        return prefs.getString(valueKey, valueDefault)
    }

    fun remove(context: Context?, valueKey: String?) {
        val prefs = PreferenceManager
            .getDefaultSharedPreferences(context)
        val edit = prefs.edit()
        edit.remove(valueKey)
        edit.apply()
    }
}