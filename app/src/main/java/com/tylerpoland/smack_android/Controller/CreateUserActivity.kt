package com.tylerpoland.smack_android.Controller

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.tylerpoland.smack_android.R
import com.tylerpoland.smack_android.Services.AuthService
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    private var userAvatar = "profiledefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
    }

    fun createCreateUserButtonClicked(view: View) {
        AuthService.registerUser(this, createUsernameField.text.toString(), createPasswordField.text.toString()) { complete ->
            if (complete) {
                println("user registered")
            } else {
                println("UH OH! SOMETHING BAAAAAAAD HAPPENED.")
            }
        }
    }

    fun generateUserAvatarClicked(view: View) {
        val random = Random()
        val color = random.nextInt(2) // upper bound, not inclusive
        val avatar = random.nextInt(28)

        userAvatar = if (color == 0) {
            "light$avatar"
        } else {
            "dark$avatar"
        }
        val resourceID = resources.getIdentifier(userAvatar, "drawable", packageName)
        createUserAvatarImageButton.setImageResource(resourceID)
    }

    fun generateColorClicked(view: View) {
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        createUserAvatarImageButton.setBackgroundColor(Color.rgb(r, g, b))

        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = b.toDouble() / 255
        avatarColor = "[$savedR, $savedG, $savedB]" // set avatarColor to something the server can consume (iOS & macOS require RGB values < 1)
    }
}
