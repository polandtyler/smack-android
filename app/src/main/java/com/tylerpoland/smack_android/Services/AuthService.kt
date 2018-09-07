package com.tylerpoland.smack_android.Services

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.tylerpoland.smack_android.Utils.URL_LOGIN
import com.tylerpoland.smack_android.Utils.URL_REGISTER
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    var isLoggedIn = false
    var userEmail = ""
    var authToken = ""

    fun registerUser(context: Context, email: String, password: String, completion: (Boolean) -> Unit) {
        val url = URL_REGISTER

        // json body to send
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        // web request (specify request method type)
        val registerRequest = object: StringRequest(Request.Method.POST, url, Response.Listener { _ -> // listen for response and do something with it
            completion(true)
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not register user: $error")
            completion(false)
        }) {
            // content type body
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            // turn body into byte array
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        // add to queue
        Volley.newRequestQueue(context).add(registerRequest)
    }

    fun loginUser(context: Context, email: String, password: String, completion: (Boolean) -> Unit?) {
        val url = URL_LOGIN

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val loginRequest = object: JsonObjectRequest(Request.Method.POST, url, null, Response.Listener { response ->
            try {
                authToken = response.getString("token")
                userEmail = response.getString("user")
                isLoggedIn = true
                completion(true)
            } catch (e: JSONException) {
                Log.d("JSON", "EXC: " + e.localizedMessage)
                completion(false)
            }

        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Login error: $error")
            completion(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json;charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(loginRequest)
    }
}