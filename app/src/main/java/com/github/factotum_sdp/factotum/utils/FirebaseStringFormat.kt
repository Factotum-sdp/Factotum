package com.github.factotum_sdp.factotum.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * This class contains some utility functions to format strings for Firebase
 *
 */
class FirebaseStringFormat {
    companion object {
        const val FIREBASE_SAFE_DOT = "_dot_"
        const val FIREBASE_SAFE_HASH = "_hash_"
        const val FIREBASE_SAFE_DOLLAR = "_dollar_"
        const val FIREBASE_SAFE_OPENBRACKET = "_openbracket_"
        const val FIREBASE_SAFE_CLOSEBRACKET = "_closebracket_"
        const val FIREBASE_SAFE_SLASH = "_slash_"


        /**
         * This function replaces some characters that are not allowed in Firebase keys
         * @param str the string to be formatted
         * @return the formatted string
         */
        fun firebaseSafeString(str: String): String {
            return str.replace(".", FIREBASE_SAFE_DOT)
                .replace("#", FIREBASE_SAFE_HASH)
                .replace("$", FIREBASE_SAFE_DOLLAR)
                .replace("[", FIREBASE_SAFE_OPENBRACKET)
                .replace("]", FIREBASE_SAFE_CLOSEBRACKET)
                .replace("/", FIREBASE_SAFE_SLASH)
        }

        /**
         * This function reverts the formatting done by firebaseSafeString
         * @param str the string to be reverted
         * @return the reverted string
         */
        fun firebaseSafeStringRevert(str: String): String {
            return str.replace(FIREBASE_SAFE_DOT, ".")
                .replace(FIREBASE_SAFE_HASH, "#")
                .replace(FIREBASE_SAFE_DOLLAR, "$")
                .replace(FIREBASE_SAFE_OPENBRACKET, "[")
                .replace(FIREBASE_SAFE_CLOSEBRACKET, "]")
                .replace(FIREBASE_SAFE_SLASH, "/")
        }


        /**
         * This function returns the current date formatted as ddMMyyyy
         *
         * @return the formatted date
         */
        fun firebaseDateFormatted(): String {
            return SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(Date())
        }
    }
}