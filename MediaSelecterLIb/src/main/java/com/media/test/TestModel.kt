package com.media.test

import android.Manifest
import android.app.Activity
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.media.base.MyBaseModel
import com.media.adapter.AdapterImageList
import com.media.databinding.ActivityTestBinding
import com.media.db.AlbumHelper
import com.media.db.LocalMediaLoader
import com.media.utils.LogUtils
import com.media.utils.permission.MPermission

/**
 * yuxiu
 * 2021/8/25
 **/
class TestModel(binding: ActivityTestBinding, context: Activity) :
    MyBaseModel<ActivityTestBinding>(binding, context) {
    var adapter = TestLoadAdapter(context);
    var dbHelper= AlbumHelper.getHelper();
    init {
        MPermission.with(context).setRequestCode(0)
            .permissions( Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).request()

        dbHelper.init(context)
        binding.rv.layoutManager = LinearLayoutManager(context)
        binding.rv.adapter = adapter;
        adapter.setLoadListener({
            adapter.getmDatas()?.let {
               var a= System.currentTimeMillis()
//                while(true){
//                    var b=System.currentTimeMillis();
//                    if(b-a>3000){
//                        break
//                    }
//                }
//               var list=dbHelper.getImagesBucketList(true)
//
//               for (i in dbHelper.allImageList){
//                   it.add(i.imagePath)
//               }
                var list=LocalMediaLoader(context).loadAllMedia()

               for (i in list){
                   it.add(i.name)
               }
                it.add("dfdf")
            }
             true
        },{
            LogUtils.e("complete")
            adapter.loadComplete()
            adapter.notifyDataSetChanged()
        })
        adapter.setmDatas(ArrayList<String>())
        adapter.getmDatas()?.add("afa")
    }

    override fun onClick(v: View?) {

    }

    private fun loadMore() {
    }
}