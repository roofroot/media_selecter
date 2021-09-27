package com.jiaoday.im.base
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Created by Administrator on 1,6-7-18.
 */
abstract class BaseMapAdapter< K,V,B : ViewBinding?> :
    RecyclerView.Adapter<BaseMapAdapter.ViewHolder<B>> {
    var onItemClick: BaseMapAdapter.OnItemClick? = null
    protected var mDatas: Map<K,V>?= null
    protected var context: android.content.Context
    fun getmDatas(): Map<K,V>? {
        return mDatas
    }
    fun setmDatas(mDatas:Map<K,V>?){
        this.mDatas = mDatas
    }

    constructor(context: android.content.Context) {
        this.context = context
    }

    constructor(context: android.content.Context, mDatas: Map<K,V>?) {
        this.mDatas = mDatas
        this.context = context
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseMapAdapter.ViewHolder<B> {
        val inflater = LayoutInflater.from(context)
        val binding = setBinding(inflater, parent)
        val viewHolder =
            BaseMapAdapter.ViewHolder<B>(binding!!.root)
        viewHolder.setBindingInstance(binding)
        if (onItemClick != null) {
            viewHolder.onItemClick = onItemClick
        }
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
        holder: BaseMapAdapter.ViewHolder<B>,
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

    class ViewHolder<B : ViewBinding?>(itemView: android.view.View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView), android.view.View.OnClickListener {
        var onItemClick: BaseMapAdapter.OnItemClick? = null
        var binding: B? = null

        fun setBindingInstance(binding: B): kotlin.Unit {
            this.binding = binding
        }

        fun setClick(vararg views: android.view.View){
            if (onItemClick != null) {
                for (view in views) {
                    view.setOnClickListener(this)
                }
            }
        }

        override fun onClick(v: android.view.View) {
            onItemClick?.onClick(v, getAdapterPosition())
        }
    }

    interface OnItemClick {
        fun onClick(view: android.view.View?,position: Int);
    }
}