package com.media.model

import android.app.Activity
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.jiaoday.im.base.MyBaseModel
import com.media.R
import com.media.adapter.AdapterImageList
import com.media.databinding.ActivityMediaSelectBinding
import com.media.manager.DataManager
import com.media.manager.SelectChangeObserver
import com.media.manager.SelectManager

/**
 * yuxiu
 * 2021/8/27
 **/
class MediaSelectModel(binding: ActivityMediaSelectBinding, context: Activity) :
    MyBaseModel<ActivityMediaSelectBinding>(binding, context),SelectChangeObserver {
    private var folderMenuModel:FolderMenuModel=FolderMenuModel(context,binding.rvImage)
    val adapter=AdapterImageList(context)
    init {

        binding.rvImage.layoutManager=GridLayoutManager(context,4)
        binding.rvImage.adapter=adapter
        SelectManager.getInstance().addListener(this,isSticky = false)
        adapter.onItemClick={
            view, pos ->
            val manager=SelectManager.getInstance();
            if(manager.getIdIndex("${adapter.getmDatas()[pos].id}")!=-1){
                manager.removeMedia(adapter.getmDatas()[pos])
            }else{
                manager.addMedia(adapter.getmDatas()[pos])
            }
        }
        bindListener(binding.tvTitle)
        DataManager.getInstance().getMediaData(context) { mediaData ->
            if (mediaData.size > 0) {
                adapter.setmDatas(mediaData[0].data)
                adapter.notifyDataSetChanged()
                binding.tvTitle.setText(mediaData[0].name)
            }
        }
        folderMenuModel?.selectChange={
                pos ->
            DataManager.getInstance().getMediaData(context) { mediaData ->
                if (mediaData.size > 0) {
                    adapter.setmDatas(mediaData[pos].data)
                    adapter.notifyDataSetChanged()
                    folderMenuModel.hindMenu()
                }
            }
        }

    }

    override fun onDestroy() {
        SelectManager.destroy()
        DataManager.destroy()
        folderMenuModel?.removeView()
    }
    override fun onClick(v: View) {
        if(v.id== R.id.tv_title){
            folderMenuModel?.showMenu()
        }
    }

    override fun onSelectChange(isSticky: Boolean) {
        adapter.notifyDataSetChanged()
    }
}