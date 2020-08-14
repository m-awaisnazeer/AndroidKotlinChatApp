package com.communisolve.androidkotlinchatapp.Model

class Chatlist {
    private var id: String = ""

    constructor(id: String) {
        this.id = id
    }

    constructor()

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id!!
    }

}