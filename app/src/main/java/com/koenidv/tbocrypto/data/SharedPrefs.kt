package com.koenidv.tbocrypto.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson

/**
 * Base class to store and retrieve serializable objects in SharedPreferences
 */
open class SharedPrefs(name: String, context: Context) {
    protected val prefs: SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    protected val gson = Gson()

    /**
     * Stores an object as gson-serialized json in SharedPreferences
     * @param key the key to store the object under
     * @param value the object to store
     */
    protected fun set(key: String, value: Any) {
        Log.d("SharedPrefsCache", "Setting $key to $value")
        prefs.edit()
            .putString(key, gson.toJson(value))
            .putString("${key}-timestamp", System.currentTimeMillis().toString())
            .apply()
    }

    /**
     * Retrieves an object from SharedPreferences deserialized with gson
     * @param key the key to retrieve the object from
     * @param classType the class of the object to retrieve, for gson
     * @return the object or null if it doesn't exist
     */
    protected fun <T> get(key: String, classType: Class<T>): Any? {
        val json = prefs.getString(key, null)
        return gson.fromJson(json, classType)
    }

    /**
     * Clears all stored data from this SharedPreferences
     */
    fun clear() = prefs.edit().clear().apply()


}