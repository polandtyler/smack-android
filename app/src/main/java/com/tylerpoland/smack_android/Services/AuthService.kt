package com.tylerpoland.smack_android.Services

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.tylerpoland.smack_android.Controller.App
import com.tylerpoland.smack_android.Utils.*
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    var isLoggedIn = false
    var userEmail = ""
    var authToken = ""
    private val queue = App.sharedPreferences.requestQueue

    fun registerUser(email: String, password: String, completion: (Boolean) -> Unit) {
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
        queue.add(registerRequest)
    }

    fun loginUser(email: String, password: String, completion: (Boolean) -> Unit?) {
        val url = URL_LOGIN

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val loginRequest = object: JsonObjectRequest(Request.Method.POST, url, null, Response.Listener { response ->
            try {
                App.sharedPreferences.authToken = response.getString("token")
                App.sharedPreferences.userEmail = response.getString("user")
                App.sharedPreferences.isLoggedIn = true
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
        queue.add(loginRequest)
    }

    fun createUser(name: String, email: String, avatarName: String, avatarColor: String, completion: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarColor)
        val requestBody = jsonBody.toString()

        val createUserRequest = object: JsonObjectRequest(Request.Method.POST, URL_CREATE_USER, null, Response.Listener { response ->
            try {
                UserDataService.name = response.getString("name")
                UserDataService.email = response.getString("email")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.avatarColor = response.getString("avatarColor")
                UserDataService.id = response.getString("_id")
                completion(true)
            } catch (e: JSONException) {
                Log.d("JSON", "EXC: " + e.localizedMessage)
                completion(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not add user: $error")
            completion(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json;charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${App.sharedPreferences.authToken}"
                return headers
            }
        }
        queue.add(createUserRequest)
    }
    
    fun findUserByEmail(context: Context, completion: (Boolean) -> Unit) {
        val findUserRequest = object: JsonObjectRequest(Method.GET, "$URL_GET_USER${App.sharedPreferences.userEmail}", null, Response.Listener { response ->
            try {
                UserDataService.name = response.getString("name")
                UserDataService.email = response.getString("email")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.avatarColor = response.getString("avatarColor")
                UserDataService.id = response.getString("_id")

                val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)

                completion(true)
            } catch (e: JSONException) {
                Log.d("JSON ERROR", "EXC: " + e.localizedMessage)
                completion(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not find user.")
            completion(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json;charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${App.sharedPreferences.authToken}"
                return headers
            }
        }
        queue.add(findUserRequest)
    }
}