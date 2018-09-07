package com.tylerpoland.smack_android.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.tylerpoland.smack_android.R
import com.tylerpoland.smack_android.Services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginLoginButtonClicked(view: View) {
        AuthService.loginUser(this, loginEmailText.text.toString(), loginPasswordText.text.toString()) { token ->
            if (token != null) {
                println("TOKEN: $token")
            } else {
                println("OH NO! THERE WAS A PROBLEM LOGGING IN!")
            }
        }
    }

    fun loginCreateUserButtonClicked(view: View) {
        val createUserActivity = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserActivity)
    }
}
