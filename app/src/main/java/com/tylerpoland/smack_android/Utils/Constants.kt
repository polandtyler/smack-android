package com.tylerpoland.smack_android.Utils

/*
Constants
 */
const val BASE_URL = "https://poland-smack-api.herokuapp.com/v1/"
const val SOCKET_URL = "https://poland-smack-api.herokuapp.com/"
const val URL_REGISTER = "${BASE_URL}account/register" // POST - email, password
const val URL_LOGIN = "${BASE_URL}account/login" // POST - email, password
const val URL_LOGOUT = "${BASE_URL}account/logout" // GET
const val URL_ME = "${BASE_URL}account/me" // GET - user
const val URL_GET_CHANNELS = "${BASE_URL}channel/"
const val URL_CHANNEL_ADD = "${BASE_URL}channel/add"
const val URL_MESSAGE = "${BASE_URL}message/"
const val URL_MESSAGE_BY_CHANNEL = "${BASE_URL}message/byChannel/"
const val URL_MESSAGE_ADD = "${BASE_URL}message/add"
const val URL_USER = "${BASE_URL}user/"
const val URL_CREATE_USER = "${BASE_URL}user/add"
const val URL_GET_USER = "${BASE_URL}user/byEmail/"


/*
Broadcast Constants
 */
const val BROADCAST_USER_DATA_CHANGE = "BROADCAST_USER_DATA_CHANGE"