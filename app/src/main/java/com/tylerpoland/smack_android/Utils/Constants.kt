package com.tylerpoland.smack_android.Utils

const val BASE_URL = "https://poland-smack-api.herokuapp.com/v1/"
const val URL_REGISTER = "${BASE_URL}account/register" // POST - email, password
const val URL_LOGIN = "${BASE_URL}account/login" // POST - email, password
const val URL_LOGOUT = "${BASE_URL}account/logout" // GET
const val URL_ME = "${BASE_URL}account/me" // GET - user
const val URL_CHANNEL = "${BASE_URL}channel/"
const val URL_CHANNEL_ADD = "${BASE_URL}channel/add"
const val URL_MESSAGE = "${BASE_URL}message/"
const val URL_MESSAGE_BY_CHANNEL = "${BASE_URL}message/byChannel/"
const val URL_MESSAGE_ADD = "${BASE_URL}message/add"
const val URL_USER = "${BASE_URL}user/"
const val URL_CREATE_USER = "${BASE_URL}user/add"
const val URL_USER_BY_EMAIL = "${BASE_URL}user/byEmail/"

/*
Broadcast Constants
 */
const val BROADCAST_USER_DATA_CHANGE = "BROADCAST_USER_DATA_CHANGE"


/*
****CHANNEL:****
POST - /channel/add
    name = req.body.name;
    description = req.body.description;
GET (all) - /channel
GET (id) - /channel/id
DELETE - /channel/id

****MESSAGE:****
POST - /message/add
    messageBody
    userId
    channelId
    userName
    userAvatar
    userAvatarColor
 PUT (id) - /message
    messageBody
    userId
    channelId
    userName
    userAvatar
    userAvatarColor
 GET (channelId) - /message/byChannel/channelId
    channelId
 DELETE (id) - /message/id
 DELETE - /message

****USER:****
POST - /user/add
    name
    email
    avatarName
    avatarColor
GET - /user
GET (id) - /user/id
PUT (id) - /user/id
    name
    email
    avatarName
    avatarColor
GET (email) - /user/byEmail/email
DELETE - /user/id
DELETE - /user
 */