package com.media.test

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.media.base.BaseLoaderMoreAdapter
import com.media.databinding.ItemTestBinding
import com.media.databinding.LayoutLoadingBinding

/**
 * yuxiu
 * 2021/8/25
 **/
class TestLoadAdapter(context: Context) : BaseLoaderMoreAdapter<String, ItemTestBinding>(context) {
    override fun onBindHolder(holder: ViewHolder<ItemTestBinding>, position: Int) {
        mDatas?.let {
                mDatas->
            holder.binding?.let {
                binding->
                binding.tvItem.text=mDatas[position]
            }
        }

    }

    override fun setBinding(inflater: LayoutInflater, parent: ViewGroup): ItemTestBinding {
       return ItemTestBinding.inflate(inflater,parent,false)
    }

    override fun  setFootNoMoreBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): LayoutLoadingBinding {
       return LayoutLoadingBinding.inflate(inflater,parent,false)
    }

    override fun setFootLoadingBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ):LayoutLoadingBinding{
        return LayoutLoadingBinding.inflate(inflater,parent,false)
    }

}