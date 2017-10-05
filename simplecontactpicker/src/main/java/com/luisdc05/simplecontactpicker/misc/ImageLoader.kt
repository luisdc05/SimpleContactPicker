package com.luisdc05.simplecontactpicker.misc

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

/**
 * Created by user1 on 10/5/17.
 */
object ImageLoader {

    fun loadDrawable(context: Context, drawable: Int, view: ImageView) {
        Picasso
                .with(context)
                .load(drawable)
                .transform(CropCircleTransformation())
                .into(view)
    }

    fun loadImagePath(context: Context, path: String, backupDrawable: Int, view: ImageView) {
        Picasso
                .with(context)
                .load(path)
                .placeholder(backupDrawable)
                .error(backupDrawable)
                .transform(CropCircleTransformation())
                .into(view)
    }

}