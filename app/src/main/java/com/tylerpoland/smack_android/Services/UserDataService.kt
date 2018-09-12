package com.tylerpoland.smack_android.Services

import android.graphics.Color
import com.tylerpoland.smack_android.Controller.App
import com.tylerpoland.smack_android.Utils.URL_CREATE_USER
import java.util.*

object UserDataService {

    var url = URL_CREATE_USER
    var id = ""
    var avatarColor = ""
    var avatarName = ""
    var email = ""
    var name = ""

    fun logout() {
        id = ""
        avatarColor = ""
        avatarName = ""
        email = ""
        name = ""
        App.sharedPreferences.authToken = ""
        App.sharedPreferences.userEmail = ""
        App.sharedPreferences.isLoggedIn = false

    }

    fun returnAvatarColor(components: String) : Int {
        // "[0.8392156862745098, 0.6352941176470588, 0.796078431372549]"
        // 0.8392156862745098 0.6352941176470588 0.796078431372549

        val strippedColor = components.replace("[", "")
            .replace("]", "")
            .replace(",", "")

        var r = 0
        var g = 0
        var b = 0
        val scanner = Scanner(strippedColor)
        if (scanner.hasNext()) {
            r = (scanner.nextDouble() * 255).toInt()
            g = (scanner.nextDouble() * 255).toInt()
            b = (scanner.nextDouble() * 255).toInt()
        }
        return Color.rgb(r, g, b)
    }
}