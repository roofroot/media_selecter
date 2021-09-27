package com.media.test

import com.jiaoday.im.base.MyBaseActivity
import com.media.databinding.ActivityTestBinding

/**
 * yuxiu
 * 2021/8/25
 **/
class TestActivity: MyBaseActivity<ActivityTestBinding, TestModel>() {
    override fun getBindingInstance(): ActivityTestBinding {
        return ActivityTestBinding.inflate(layoutInflater)
    }

    override fun getModelInstance(): TestModel {
        return TestModel(binding,this)
    }

    override fun onPrepare() {

    }
}