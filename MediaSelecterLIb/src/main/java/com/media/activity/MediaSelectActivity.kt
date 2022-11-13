package com.media.activity

import com.media.base.MyBaseActivity
import com.media.databinding.ActivityMediaSelectBinding
import com.media.model.MediaSelectModel

/**
 * yuxiu
 * 2021/8/27
 **/
class MediaSelectActivity: MyBaseActivity<ActivityMediaSelectBinding, MediaSelectModel>() {
    override fun onPrepare() {

    }
    override fun getBindingInstance(): ActivityMediaSelectBinding {
        return ActivityMediaSelectBinding.inflate(layoutInflater)
    }

    override fun getModelInstance(): MediaSelectModel {
       return MediaSelectModel(binding,this)
    }

}