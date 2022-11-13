package com.media.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.media.base.BaseAdapter
import com.media.R
import com.media.config.PictureMimeType
import com.media.databinding.ItemMediaListBinding
import com.media.entity.LocalMedia
import com.media.manager.SelectManager
import com.media.tools.DateUtils
import com.media.utils.GlideUtil

/**
 * yuxiu
 * 2021/8/27
 **/
class AdapterImageList(context: Context) : BaseAdapter<LocalMedia, ItemMediaListBinding>(context) {
    override fun onBindHolder(holder: ViewHolder<ItemMediaListBinding>, position: Int) {
        holder.binding?.let {
            binding->
            var data=getmDatas()[position]
            GlideUtil.setImageUrl(binding.ivPic,data.path)
            var selectNum= SelectManager.getInstance().getIdIndex("${data.id}");
            if(selectNum!=-1){
                binding.tvCheck.text="${selectNum}"
                binding.tvCheck.visibility= View.VISIBLE
            }else{
                binding.tvCheck.visibility= View.GONE
            }
            holder.setClick(binding.root)
            if(data.mimeType==PictureMimeType.MIME_TYPE_PREFIX_AUDIO){
                binding.tvDuration.visibility=View.VISIBLE
                binding.tvDuration.setText("")
            }else{
                binding.tvDuration.visibility=View.GONE
            }

        }
    }
    override fun setBinding(inflater: LayoutInflater, parent: ViewGroup): ItemMediaListBinding {
       return ItemMediaListBinding.inflate(inflater,parent,false)
    }
}