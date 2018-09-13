package com.tylerpoland.smack_android.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.tylerpoland.smack_android.Controller.App
import com.tylerpoland.smack_android.Model.Channel
import com.tylerpoland.smack_android.Model.Message
import com.tylerpoland.smack_android.Utils.URL_GET_CHANNELS
import com.tylerpoland.smack_android.Utils.URL_MESSAGE_BY_CHANNEL
import org.json.JSONException

object MessageService {

    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    private val queue = App.sharedPreferences.requestQueue

    fun getChannels(completion: (Boolean) -> Unit) {
        val channelsRequest = object: JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener { response ->
            try {
                for (index in 0 until response.length()) {
                    val channel = response.getJSONObject(index)
                    val name = channel.getString("name")
                    val channelDesc = channel.getString("description")
                    val channelId = channel.getString("_id")

                    val newChannel = Channel(name, channelDesc, channelId)
                    this.channels.add(newChannel)
                }
                completion(true)
            } catch (e: JSONException) {
                Log.d("JSON", "EXC: " + e.localizedMessage)
                completion(false)
            }
        }, Response.ErrorListener {error ->
            Log.d("ERROR", "Could not retrieve channels.")
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
        queue.add(channelsRequest)
    }

    fun getMessages(channelId: String, completion: (Boolean) -> Unit) {
        val url = "$URL_MESSAGE_BY_CHANNEL$channelId"

        val messagesRequest = object: JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->
            clearMessages()
            try {
                for (index in 0 until response.length()) {
                    val message = response.getJSONObject(index)
                    val msgBody = message.getString("messageBody")
                    val channelId = message.getString("channelId")
                    val userName = message.getString("userName")
                    val userAvatar = message.getString("userAvatar")
                    val userAvatarColor = message.getString("userAvatarColor")
                    val userId = message.getString("_id")
                    val timeStamp = message.getString("timeStamp")

                    val newMessage = Message(msgBody, userName, channelId, userAvatar, userAvatarColor, userId, timeStamp)
                    this.messages.add(newMessage)
                }
                completion(true)
            } catch (e: JSONException) {
                Log.d("JSON", "EXC: " +e.localizedMessage)
                completion(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Error getting messages: $error")
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
        queue.add(messagesRequest)
    }

    fun clearMessages() {
        messages.clear()
    }

    fun clearChannels() {
        channels.clear()
    }
}