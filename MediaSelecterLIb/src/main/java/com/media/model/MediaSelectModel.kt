package com.media.model

import android.app.Activity
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.jiaoday.im.base.MyBaseModel
import com.media.R
import com.media.adapter.AdapterImageList
import com.media.databinding.ActivityMediaSelectBinding
import com.media.manager.DataManager
import com.media.manager.SelectChangeObserver
import com.media.manager.SelectManager
import com.media.utils.permission.BegPermissionsUtils
import kotlinx.coroutines.NonCancellable.start

/**
 * yuxiu
 * 2021/8/27
 **/
class MediaSelectModel(binding: ActivityMediaSelectBinding, context: Activity) :
    MyBaseModel<ActivityMediaSelectBinding>(binding, context), SelectChangeObserver {
    private var folderMenuModel: FolderMenuModel = FolderMenuModel(context, binding.rvImage)
    val adapter = AdapterImageList(context)
    lateinit var begPermissionsUtils: BegPermissionsUtils

    init {

        begPermissionsUtils =
            BegPermissionsUtils(context, object : BegPermissionsUtils.TodoBackFromBeg {
                override fun backTodo(requestCode: Int) {
                    init()
                }

                override fun cancelTodo(requestCode: Int) {
                    context.finish()
                }

                override fun settingBack(requsetCode: Int) {
                    if (!begPermissionsUtils.checkPermissions(requsetCode)) {
                        finishAnim()
                    }
                }
            })
        init()
//       var a:VectorDrawable=VectorDrawable()

        begPermissionsUtils.checkPermissions(BegPermissionsUtils.CAMERA_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        begPermissionsUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun init() {
        (binding.ivClose.drawable as AnimatedVectorDrawable).start()
        binding.rvImage.layoutManager = GridLayoutManager(context, 4)
        binding.rvImage.adapter = adapter
        SelectManager.getInstance().addListener(this, isSticky = false)
        adapter.onItemClick = { view, pos ->
            val manager = SelectManager.getInstance();
            if (manager.getIdIndex("${adapter.getmDatas()[pos].id}") != -1) {
                manager.removeMedia(adapter.getmDatas()[pos])
            } else {
                manager.addMedia(adapter.getmDatas()[pos])
            }
        }
        bindListener(binding.tvTitle,binding.ivClose)
        DataManager.getInstance().getMediaData(context) { mediaData ->
            if (mediaData.size > 0) {
                adapter.setmDatas(mediaData[0].data)
                adapter.notifyDataSetChanged()
                binding.tvTitle.setText(mediaData[0].name)
            }
        }
        folderMenuModel?.selectChange = { pos ->
            DataManager.getInstance().getMediaData(context) { mediaData ->
                if (mediaData.size > 0) {
                    adapter.setmDatas(mediaData[pos].data)
                    adapter.notifyDataSetChanged()
                    binding.tvTitle.setText(mediaData[pos].name)
                    if (folderMenuModel.isMunuShow()) {
                        folderMenuModel.hindMenu()
                        binding.ivArrow.setImageResource(R.drawable.arrow_path_to_down)
                        (binding.ivArrow.drawable as AnimatedVectorDrawable).start()
                    }
                }
            }
        }

    }

    override fun onDestroy() {
        SelectManager.destroy()
        DataManager.destroy()
        folderMenuModel?.removeView()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(v: View) {
        if (v.id == R.id.tv_title) {
            if (!folderMenuModel.isMunuShow()) {
//                binding.ivArrow.setImageResource(R.drawable.arrow_adimated_drawable_to_up)
                binding.ivArrow.setImageResource(R.drawable.arrow_path_to_up)
                (binding.ivArrow.drawable as AnimatedVectorDrawable).start()
                folderMenuModel?.showMenu()
            }
        } else if(v.id == R.id.iv_close){
            (binding.ivClose.drawable as AnimatedVectorDrawable).start()
        }
    }


    override fun onSelectChange(isSticky: Boolean) {
        adapter.notifyDataSetChanged()
    }
}