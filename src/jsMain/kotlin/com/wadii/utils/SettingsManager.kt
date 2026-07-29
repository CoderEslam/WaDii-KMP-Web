package com.wadii.utils

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.get
import com.russhwolf.settings.minusAssign
import com.russhwolf.settings.set
import com.wadii.core.fromJson
import com.wadii.core.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLOutputElement
import org.w3c.dom.HTMLSelectElement

//fun getSettings(): Settings
private lateinit var input: HTMLInputElement
private lateinit var select: HTMLSelectElement
private lateinit var output: HTMLOutputElement

private val selectedItem: SettingConfig<*> get() = settingsRepository.mySettings[select.selectedIndex]
fun showOutput(value: String) {
    output.value = value
}

private val settingsRepository: SettingsRepository by lazy { SettingsRepository(StorageSettings()) }

class SettingsManager(private val settings: Settings) {

    fun saveString(key: String, value: String) {
        settings[key] = value
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return settings[key] ?: defaultValue
    }

    fun saveInt(key: String, value: Int) {
        settings[key] = value
    }

    // FIX: Changed defaultValue from (implicit) 0 to explicitly 0.
    //      This is already correct — the bug was in UserDataViewModel
    //      passing defaultValue = 1, which this method was just forwarding.
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return settings[key] ?: defaultValue
    }

    fun getStringFlow(key: String, defaultValue: String = "") = flow {
        emit(settings[key] ?: defaultValue)
    }.flowOn(Dispatchers.Default)

    fun saveBoolean(key: String, value: Boolean) {
        settings[key] = value
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return settings[key] ?: defaultValue
    }

    fun remove(key: String) {
        settings -= key
    }

    private inline fun <reified T> saveObject(value: T?, key: String) {
        value?.let {
            settings[key] = it.toJson()
        }
    }

    private inline fun <reified T> getObject(key: String, defaultValue: T? = null): T? {
        val result = (settings[key] ?: "").fromJson<T>()
        return result ?: defaultValue
    }

//    fun saveUser(user: User) {
//        saveObject(user, Constants.USER_DATE)
//    }

//    fun getUser(): User {
//        return getObject(Constants.USER_DATE, defaultValue = User()) ?: User()
//    }


//    fun getLanguage(): String {
//        return getString(LANGUAGE, getCurrentLanguage())
//    }
//
//    fun setLanguage(language: String) {
//        saveString(LANGUAGE, language)
//    }

    fun clearAllData() {
        settings.clear()
    }

//    fun clearUserData() {
//        saveObject(User(), Constants.USER_DATE)
//    }

    companion object {
        private const val TAG = "SettingsManager"
    }
}