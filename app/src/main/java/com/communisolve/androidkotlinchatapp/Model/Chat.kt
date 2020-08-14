package com.communisolve.androidkotlinchatapp.Model

class Chat {
    var sender: String = ""
    var message: String = ""
    var receiver: String = ""
    var isseen: Boolean = false
    var url: String = ""
    var messageId: String = ""

    constructor()
    constructor(
        sender: String,
        message: String,
        receiver: String,
        isseen: Boolean,
        url: String,
        messageId: String
    ) {
        this.sender = sender
        this.message = message
        this.receiver = receiver
        this.isseen = isseen
        this.url = url
        this.messageId = messageId
    }


}