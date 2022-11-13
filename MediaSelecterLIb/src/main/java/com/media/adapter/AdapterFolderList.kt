package com.media.adapter
import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.media.base.BaseAdapter
import com.media.R
import com.media.databinding.ItemFolderListBinding
import com.media.entity.LocalMediaFolder
import com.media.manager.SelectManager
import com.media.utils.GlideUtil

/**
 * yuxiu
 * 2021/8/27
 **/
class AdapterFolderList(context: Context) :
    BaseAdapter<LocalMediaFolder, ItemFolderListBinding>(context) {

    override fun onBindHolder(holder: ViewHolder<ItemFolderListBinding>, position: Int) {
        holder.binding?.let {
            binding->
            var data=getmDatas()
            SelectManager.getSelectFolder().get(data[position].bucketId)?.let {
                if(it>0){
                    binding.tvMark.visibility= View.VISIBLE
                    binding.tvSelectedNum.text=context.getString(R.string.picture_already_select,it)
                    binding.tvSelectedNum.visibility=View.VISIBLE
                }else{
                    binding.tvSelectedNum.visibility=View.GONE
                    binding.tvMark.visibility= View.GONE
                }
            }
            binding.tvFolderName.setText(context.getString(R.string.picture_camera_roll_num,data[position].name,data[position].data.size))
            GlideUtil.setImageUrl(binding.ivImage,data[position].firstImagePath)
            holder.setClick(binding.root)
        }
    }
    override fun setBinding(inflater: LayoutInflater, parent: ViewGroup): ItemFolderListBinding {
       return ItemFolderListBinding.inflate(inflater,parent,false)
    }
}