package com.tylerpoland.smack_android.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.tylerpoland.smack_android.R
import com.tylerpoland.smack_android.Services.AuthService
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
        val email = loginEmailText.text.toString()
        val password = loginPasswordText.text.toString()

        AuthService.loginUser(this, email, password) { authSuccess ->
            if (authSuccess) {
                AuthService.findUserByEmail(this) { findSuccess ->
                    if (findSuccess) {
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
    }

    fun loginCreateUserButtonClicked(view: View) {
        val createUserActivity = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserActivity)
        finish()
    }

    private fun errorToast(enable: Boolean) {
        Toast.makeText(this, "There was an error logging in the user.", Toast.LENGTH_SHORT).show()
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
}
