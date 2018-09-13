package com.tylerpoland.smack_android.Controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.tylerpoland.smack_android.Model.Channel
import com.tylerpoland.smack_android.R
import com.tylerpoland.smack_android.Services.AuthService
import com.tylerpoland.smack_android.Services.MessageService
import com.tylerpoland.smack_android.Services.UserDataService
import com.tylerpoland.smack_android.Utils.BROADCAST_USER_DATA_CHANGE
import com.tylerpoland.smack_android.Utils.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import com.tylerpoland.smack_android.Model.Message

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>
    var selectedChannel: Channel? = null

    private fun setUpAdapters() {
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // connect to server
        socket.connect()
        // what to do when channel is created
        socket.on("channelCreated", onNewChannel)
        socket.on("messageCreated", onNewMessage)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        setUpAdapters()

        channel_list.setOnItemClickListener { _, _, position, _ ->
            selectedChannel = MessageService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }

        if (App.sharedPreferences.isLoggedIn) {
            println("User was previously logged in.")
            AuthService.findUserByEmail(this) {}
        }
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGE))
        super.onResume()
    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        super.onDestroy()
    }

    private val userDataChangeReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (App.sharedPreferences.isLoggedIn) {
                usernameNavheader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                userImageNavHeader.setImageResource(resourceId)
                userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                loginButtonNavHeader.text = "Log out"

                MessageService.getChannels { complete ->
                    if (complete) {
                        if (MessageService.channels.count() > 0) {
                            selectedChannel = MessageService.channels.first()
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }
                    }
                }
            }
        }
    }

    fun updateWithChannel() {
        mainChannelName.text = "#${selectedChannel!!.name}"
        // download messages for channel
        if (selectedChannel != null) {
            MessageService.getMessages(selectedChannel!!.id) { complete ->
                if (complete) {
                    for (message in MessageService.messages) {
                        println(message.message)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginButtonNavClicked(view: View) {
        if (App.sharedPreferences.isLoggedIn) {
            UserDataService.logout()
            usernameNavheader.text = ""
            userEmailNavHeader.text = ""
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginButtonNavHeader.text = "Login"
        } else {
            val loginActivity = Intent(this, LoginActivity::class.java)
            startActivity(loginActivity)
        }
    }

    fun addChannelButtonClicked(view: View) {
        // display alert dialog
        if (App.sharedPreferences.isLoggedIn) {
            val alertBuilder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            alertBuilder.setView(dialogView)
                    .setPositiveButton("Add") { _, _ ->
                        // get text field text entries
                        val nameTextField = dialogView.findViewById<EditText>(R.id.addChannelNameTxt)
                        val descriptionTxtField = dialogView.findViewById<EditText>(R.id.addChannelDescTxt)
                        val channelName = nameTextField.text.toString()
                        val channelDesc = descriptionTxtField.text.toString()

                        // create channel with name and desc
                        socket.emit("newChannel", channelName, channelDesc)
                    }
                    .setNegativeButton("Cancel") { dialog, which ->
                        // cancel and close the dialog
                    }
                    .show()
        }
    }

    private val onNewChannel = Emitter.Listener { args ->
        if (App.sharedPreferences.isLoggedIn) {
            runOnUiThread {
                val channelName = args[0] as String
                val channelDesc = args[1] as String
                val channelId = args[2] as String

                val newChannel = Channel(channelName, channelDesc, channelId)
                MessageService.channels.add(newChannel)
                channelAdapter.notifyDataSetChanged()
            }
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        // logged in and msg.channel == selectedchannel
        if (App.sharedPreferences.isLoggedIn) {
            runOnUiThread {
                val channelId = args[2] as String
                if (channelId == selectedChannel?.id) {
                    val msgBody = args[0] as String
                    val userName = args[3] as String
                    val userAvatar = args[4] as String
                    val userAvatarColor = args[5] as String
                    val id = args[6] as String
                    val timeStamp = args[7] as String

                    val newMessage = Message(msgBody, userName, channelId, userAvatar, userAvatarColor, id, timeStamp)
                    MessageService.messages.add(newMessage)
                }
            }
        }
    }

    fun sendMessageButtonClicked(view: View) {
        if (App.sharedPreferences.isLoggedIn && messageTextField.text.isNotEmpty() && selectedChannel != null) {
            val userID = UserDataService.id
            val channel = selectedChannel?.id
            socket.emit("newMessage", messageTextField.text.toString(), userID, channel, UserDataService.name, UserDataService.avatarName, UserDataService.avatarColor)
            messageTextField.text.clear()
            hideKeyboard()
        } else if (!App.sharedPreferences.isLoggedIn) {
            Toast.makeText( this, "Please log in.", Toast.LENGTH_SHORT).show()
        } else if (messageTextField.text.isEmpty()) {
            Toast.makeText( this, "Please input some text.", Toast.LENGTH_SHORT).show()
        } else if (selectedChannel == null) {
            Toast.makeText( this, "Please select a channel.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText( this, "An error occurred.", Toast.LENGTH_SHORT).show()
        }
        hideKeyboard()
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

}
