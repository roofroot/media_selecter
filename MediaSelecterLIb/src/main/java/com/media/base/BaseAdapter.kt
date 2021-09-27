package com.jiaoday.im.base
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import java.util.ArrayList
/**
 * Created by Administrator on 16-7-18.
 */
abstract class BaseAdapter<T, B : ViewBinding?> :
    RecyclerView.Adapter<BaseAdapter.ViewHolder<B>> {
    var onItemClick: ((View, Int) -> Unit)? =null
    protected var mDatas: ArrayList<T> ?= null
    protected var context: android.content.Context
    fun getmDatas(): ArrayList<T> {
        return mDatas?:ArrayList<T>().also{
            mDatas=it
        }
    }

    fun setmDatas(mDatas:ArrayList<T>){
        this.mDatas = mDatas
    }

    constructor(context: android.content.Context) {
        this.context = context
    }

    constructor(context: android.content.Context, mDatas: ArrayList<T>) {
        this.mDatas = mDatas
        this.context = context
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseAdapter.ViewHolder<B> {
        val inflater = LayoutInflater.from(context)
        val binding = setBinding(inflater, parent)
        val viewHolder =
            BaseAdapter.ViewHolder<B>(binding!!.root,onItemClick)
        viewHolder.setBindingInstance(binding)
        return viewHolder
    }

    override fun onBindViewHolder(
        holder: ViewHolder<B>,
        position: Int
    ) {
        val myholder =
            holder
        onBindHolder(myholder, position)
    }

    protected abstract fun onBindHolder(
        holder: BaseAdapter.ViewHolder<B>,
        position: Int
    )

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    protected abstract fun setBinding(inflater: LayoutInflater, parent: ViewGroup): B
    override fun getItemCount(): Int {
        return if (mDatas == null) 0 else mDatas!!.size
    }

    class ViewHolder<B : ViewBinding?>(itemView:View,  var onItemClick: ((View, Int) -> Unit)?) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var binding: B? = null

        fun setBindingInstance(binding: B) {
            this.binding = binding
        }

        fun setClick(vararg views:View){
            onItemClick?.let {
                for (view in views) {
                    view.setOnClickListener(this)
                }
            }
        }

        override fun onClick(v: View) {
            onItemClick?.let {
                it(v,adapterPosition)
            }
        }
    }

}