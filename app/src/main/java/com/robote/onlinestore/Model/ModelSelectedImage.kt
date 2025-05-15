package com.robote.onlinestore.Model

import android.net.Uri

class ModelSelectedImage {
    var id = ""
    var imageUri: Uri? = null
    var imageUrl: String? = null
    var ofInternet = false

    constructor()
    constructor(id: String, imageUri: Uri?, imageUrl: String?, ofInternet: Boolean) {
        this.id = id
        this.imageUri = imageUri
        this.imageUrl = imageUrl
        this.ofInternet = ofInternet
    }


}