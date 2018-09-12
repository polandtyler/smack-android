package com.tylerpoland.smack_android.Controller

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.tylerpoland.smack_android.R
import com.tylerpoland.smack_android.Services.AuthService
import com.tylerpoland.smack_android.Services.UserDataService
import com.tylerpoland.smack_android.Utils.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginSpinner.visibility = View.INVISIBLE
    }

    fun loginLoginButtonClicked(view: View) {
        enableSpinner(true)
        val email = this.loginEmailText.text.toString()
        val password = this.loginPasswordText.text.toString()
        hideKeyboard()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            AuthService.loginUser(this, email, password) { authSuccess ->
                if (authSuccess) {
                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
                    AuthService.findUserByEmail(this) { findSuccess ->
                        if (findSuccess) {
                            App.sharedPreferences.isLoggedIn = true
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
            Toast.makeText(this, "Make sure email and password fields have a value before continuing.", Toast.LENGTH_SHORT).show()
            enableSpinner(false)
        }
    }

    fun loginCreateUserButtonClicked(view: View) {
        val createUserActivity = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserActivity)
        finish()
    }

    private fun errorToast(enable: Boolean) {
        Toast.makeText(this, "There was an error logging in the user.", Toast.LENGTH_SHORT).show()
        App.sharedPreferences.isLoggedIn = false
        enableSpinner(enable)
    }

    private fun enableSpinner(enable: Boolean) {
        if (enable) {
            loginSpinner.visibility = View.VISIBLE
        } else {
            loginSpinner.visibility = View.INVISIBLE
        }
        loginCreateUserButton.isEnabled = !enable
        loginLoginButton.isEnabled = !enable
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }
}
