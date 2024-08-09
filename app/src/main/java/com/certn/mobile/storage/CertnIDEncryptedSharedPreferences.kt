package com.certn.mobile.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class CertnIDEncryptedSharedPreferences(
    context: Context,
    private val masterKeyAlias: String = DEFAULT_MASTER_KEY_ALIAS_NAME,
    private val sharedPreferenceName: String = DEFAULT_SHARED_PREFERENCE_NAME
) {
    private val masterKey by lazy {
        MasterKey.Builder(context, masterKeyAlias)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .setUserAuthenticationRequired(false)
            .build()
    }

    private val sharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context, sharedPreferenceName, masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun getValue(key: String): String? = sharedPreferences.getString(key, null)

    fun setValue(key: String, value: String?) {
        sharedPreferences.edit()
            .putString(key, value)
            .apply()
    }

    fun getKeys(): Set<String> =
        sharedPreferences.all.keys

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        const val DEFAULT_MASTER_KEY_ALIAS_NAME = "_certn_id_master_key_"
        const val DEFAULT_SHARED_PREFERENCE_NAME = "certn_id_storage"
    }
}