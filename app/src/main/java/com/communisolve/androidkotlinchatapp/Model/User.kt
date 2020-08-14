package com.communisolve.androidkotlinchatapp.Model

class User {
    lateinit var uid: String
    lateinit var username: String
    lateinit var profile: String
    lateinit var cover: String
    lateinit var status: String
    lateinit var search: String
    lateinit var facebook: String
    lateinit var instagram: String
    lateinit var website: String
    lateinit var fullname: String

    constructor()

    constructor(
        uid: String,
        fullname: String,
        username: String,
        profile: String,
        cover: String,
        status: String,
        search: String,
        facebook: String,
        instagram: String,
        website: String
    ) {
        this.uid = uid
        this.fullname = fullname
        this.username = username
        this.profile = profile
        this.cover = cover
        this.status = status
        this.search = search
        this.facebook = facebook
        this.instagram = instagram
        this.website = website
    }


}