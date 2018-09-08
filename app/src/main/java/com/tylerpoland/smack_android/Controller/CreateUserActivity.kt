package com.tylerpoland.smack_android.Controller

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.tylerpoland.smack_android.R
import com.tylerpoland.smack_android.Services.AuthService
import com.tylerpoland.smack_android.Services.UserDataService
import com.tylerpoland.smack_android.Utils.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    private var userAvatar = "profiledefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        createSpinner.visibility = View.INVISIBLE
    }

    fun createUserClicked(view: View) {
        enableSpinner(true)

        val userName = createUsernameField.text.toString()
        val email = createEmailField.text.toString()
        val password = createPasswordField.text.toString()

        if (userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            AuthService.registerUser(this, email, password) { registerSuccess ->
                if (registerSuccess) {
                    AuthService.loginUser(this, email, password) { loginSuccess ->
                        if (loginSuccess) {
                            AuthService.createUser(this, userName, email, userAvatar, avatarColor) {createSuccess ->
                                if (createSuccess) {
                                    //local broadcast
                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)

                                    enableSpinner(false)
                                    finish()
                                } else {
                                    errorToast(false)
                                }
                            }
                        } else {
                            errorToast(false)
                        }
                    }
                } else {
                    Log.d("ERROR", "There was an error registering the user.")
                    errorToast(false)
                }
            }
        } else {
            Toast.makeText(this, "Make sure user name, email, and password fields have a value before continuing.", Toast.LENGTH_SHORT).show()
            enableSpinner(false)
        }
    }

    private fun errorToast(enable: Boolean) {
        Toast.makeText(this, "There was an error creating the user.", Toast.LENGTH_SHORT).show()
        enableSpinner(enable)
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

    private fun enableSpinner(enable: Boolean) {
        if (enable) {
            createSpinner.visibility = View.VISIBLE
        } else {
            createSpinner.visibility = View.INVISIBLE
        }
        createCreateUserButton.isEnabled = !enable
        generateNewColorButton.isEnabled = !enable
        createUserAvatarImageButton.isEnabled = !enable
    }
}
