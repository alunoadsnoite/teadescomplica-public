package br.com.teadescomplica.core.datastore

/**
 * Preferência de tema da interface.
 *
 * system -> segue o tema do dispositivo. light -> força tema claro. dark -> força tema escuro.
 */
enum class ThemePreference(val value: Int, val key: String) {
    SYSTEM(0, "system"),
    LIGHT(1, "light"),
    DARK(2, "dark");

    fun toStringKey(): String = key

    companion object {
        fun fromStringKey(key: String): ThemePreference =
            values().firstOrNull { it.key == key } ?: SYSTEM

        fun fromInt(value: Int): ThemePreference =
            values().firstOrNull { it.value == value } ?: SYSTEM
    }
}
