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
import android.widget.EditText
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
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // connect to server
        socket.connect()
        // what to do when channel is created
        socket.on("channelCreated", onNewChannel)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
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
        override fun onReceive(context: Context?, intent: Intent?) {
            if (AuthService.isLoggedIn) {
                usernameNavheader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                userImageNavHeader.setImageResource(resourceId)
                userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                loginButtonNavHeader.text = "Log out"
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
        if (AuthService.isLoggedIn) {
            UserDataService.logout()
            usernameNavheader.text = "Login"
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
        if (AuthService.isLoggedIn) {
            val alertBuilder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            alertBuilder.setView(dialogView)
                    .setPositiveButton("Add") { dialog, which ->
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
        runOnUiThread {
            val channelName = args[0] as String
            val channelDesc = args[1] as String
            val channelId = args[2] as String

            val newChannel = Channel(channelName, channelDesc, channelId)
            MessageService.channels.add(newChannel)
            println("${newChannel.name} - ${newChannel.description} - ${newChannel.id}")
        }
    }

    fun sendMessageButtonClicked(view: View) {
        hideKeyboard()
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

}
