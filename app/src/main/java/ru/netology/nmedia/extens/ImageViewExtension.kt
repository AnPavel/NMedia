package ru.netology.nmedia.extens

import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R

fun ImageView.loadCircle(url: String){
    Glide.with(this)
        .load(url)
        .circleCrop()
        .placeholder(R.drawable.ic_avatar_loading)
        .error(R.drawable.ic_avatar_error)
        .timeout(10_000)
        .into(this)
}

fun ImageView.load(url: String){
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.ic_avatar_loading)
        .error(R.drawable.ic_avatar_error)
        .timeout(10_000)
        .into(this)
}
