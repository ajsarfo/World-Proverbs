package com.sarftec.worldproverbs

import android.content.*
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val IS_DARK_MODE = booleanPreferencesKey("dark_mode")
val SHOULD_VIBRATE = booleanPreferencesKey("vibrate")
val SHOW_GRADIENT_BACKGROUND = booleanPreferencesKey("gradient")
val SHOW_AUTHOR = booleanPreferencesKey("show_author")
val SHOW_NOTIFICATIONS = booleanPreferencesKey("notifications")
val REMINDER_TIMER = intPreferencesKey("remainder_timer")
val APP_CREATED = booleanPreferencesKey("app_created")
val App_START_UP_TIMES = intPreferencesKey("app_start_up_times")
val APP_START_COUNT  = intPreferencesKey("app_start_count")
val SHOW_RATINGS = booleanPreferencesKey("show_ratings_interval")

val Context.dataStore by preferencesDataStore(name = "settings_store")

fun <T> Context.readSettings(key: Preferences.Key<T>, default: T): Flow<T> {
    return dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw Exception("Setting data store exception occurred")
        }
        .map { preferences ->
            preferences[key] ?: default
        }
}

suspend fun <T> Context.editSettings(key: Preferences.Key<T>, value: T) {
    dataStore.edit { preferences ->
        preferences[key] = value
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////
fun Context.toast(text: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, length).show()
}

fun Context.copy(text: String, label: String) {
    ContextCompat.getSystemService(this, ClipboardManager::class.java)?.apply {
        val clip = ClipData.newPlainText(label, text)
        setPrimaryClip(clip)
    }
}

fun Context.share(text: String, header: String, newTask: Boolean = false) {

    ContextCompat.getSystemService(this, ClipboardManager::class.java)?.apply {
        val clip = ClipData.newPlainText("label", text)
        setPrimaryClip(clip)
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }

    Intent.createChooser(intent, header).apply {
        putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(Intent()))
        if(newTask) { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        startActivity(this)
    }
}

fun Context.moreApps() {
    val webIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("market://search?q=sarftec&c=apps")
    )
    startActivity(webIntent)
}

fun Context.rateApp() {
    val appId = packageName
    val rateIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("market://details?id=$appId")
    )
    var marketFound = false
    // find all applications able to handle our rateIntent
    // find all applications able to handle our rateIntent
    val otherApps = packageManager
        .queryIntentActivities(rateIntent, 0)
    for (otherApp in otherApps) {
        // look for Google Play application
        if (otherApp.activityInfo.applicationInfo.packageName
            == "com.android.vending"
        ) {
            val otherAppActivity = otherApp.activityInfo
            val componentName = ComponentName(
                otherAppActivity.applicationInfo.packageName,
                otherAppActivity.name
            )
            // make sure it does NOT open in the stack of your activity
            rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            // task repeating if needed
            rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            // if the Google Play was already open in a search result
            //  this make sure it still go to the app page you requested
            rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // this make sure only the Google Play app is allowed to
            // intercept the intent
            rateIntent.component = componentName
            startActivity(rateIntent)
            marketFound = true
            break
        }
    }

    // if GP not present on device, open web browser

    // if GP not present on device, open web browser
    if (!marketFound) {
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$appId")
        )
        startActivity(webIntent)
    }
}

fun Context.vibrate(milliseconds: Long = 24, amplitude: Int = 18) {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, amplitude))
    } else {
        vibrator.vibrate(milliseconds)
    }
}