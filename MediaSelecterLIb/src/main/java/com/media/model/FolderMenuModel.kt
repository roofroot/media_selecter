package com.media.model

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.media.adapter.AdapterFolderList
import com.media.base.BaseMenuModel
import com.media.databinding.MenuFolderListBinding
import com.media.manager.DataManager
import com.media.manager.SelectChangeObserver
import com.media.manager.SelectManager

/**
 * yuxiu
 * 2021/8/27
 **/
class FolderMenuModel(context: Activity, view: View) :
    BaseMenuModel<MenuFolderListBinding>(context, view),SelectChangeObserver {
    var adapter: AdapterFolderList
    var selectChange:((pos:Int)->Unit)?=null
    init {
        SelectManager.getInstance().addListener(this,isSticky = false)
//        menuHeight = ViewGroup.LayoutParams.WRAP_CONTENT
        gravityH = Gravity.LEFT
        gravityV=Gravity.TOP
        adapter = AdapterFolderList(context)
        adapter.onItemClick={
            _, pos->
            selectChange?.let {
                it(pos)
            }
        }
        binding.rvFolder.layoutManager = LinearLayoutManager(context)
        binding.rvFolder.adapter = adapter
        DataManager.getInstance().getMediaData(context) { mediaData ->
            adapter.setmDatas(mediaData)
            adapter.notifyDataSetChanged()
        }

    }

    @SuppressLint("ObjectAnimatorBinding")
    override fun getAppearingAnimation(): Animator {
        var mSet = AnimatorSet();
        mSet.playTogether(
            ObjectAnimator.ofFloat(null, "translationY", -300f, 0f)
        )
        return mSet;
    }

    @SuppressLint("ObjectAnimatorBinding")
    override fun getDisappearingAnimation(): Animator {
        var mSet = AnimatorSet();
        mSet.playTogether(
            ObjectAnimator.ofFloat(null, "translationY", 0f, -300f)
        );
        return mSet;
    }

    override fun getBindingInstance(): MenuFolderListBinding {
        return MenuFolderListBinding.inflate(context.layoutInflater)
    }

    override fun onSelectChange(isSticky: Boolean) {
        adapter.notifyDataSetChanged()
    }
}