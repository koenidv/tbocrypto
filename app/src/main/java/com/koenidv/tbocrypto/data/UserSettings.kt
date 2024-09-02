package com.koenidv.tbocrypto.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

enum class Settings {
    SELECTED_COIN,
}

@ViewModelScoped
class UserSettings @Inject constructor(
    @ApplicationContext context: Context
) : SharedPrefs("settings", context) {

    fun setSelectedCoin(value: Coin) = set(Settings.SELECTED_COIN.name, value)
    fun getSelectedCoin(): Coin? = get(Settings.SELECTED_COIN.name, Coin::class.java) as Coin?

}