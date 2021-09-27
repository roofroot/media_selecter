package com.media.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.media.R
import java.io.File


/**
 * yuxiu
 * 2021/7/21 2:10 下午
 **/
class GlideUtil {
    companion object{


        fun setImageUrl(imageView: ImageView?, url: String) {
            val options = RequestOptions()
                .dontAnimate()
                .placeholder(R.drawable.picture_image_placeholder)
                .error(R.drawable.picture_image_placeholder)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide.with(imageView!!)
                .load(url)
//            .thumbnail(Glide.with(imageView).load(R.mipmap.load))
                .apply(options)
                .into(imageView)
        }

        fun setImageFile(imageView: ImageView, filepath: String?) {
            val options = RequestOptions()
                .dontAnimate()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide.with(imageView)
                .load(File(filepath))
                .apply(options)
                .into(imageView)
        }

    }
}