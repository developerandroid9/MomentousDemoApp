package com.momentous

import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide

fun loadWithGlide(url: String?, iv: ImageView?) {
    Log.e("url ", url ?: "")
    iv?.let { iv.context?.let { it1 -> Glide.with(it1).load(url).into(it) } }
}

fun showToast(message: String?, context: Context?) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

enum class ListController { HOST_DATA, LOCAL_DATA }




