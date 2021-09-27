package com.media.base
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


/**
 * yuxiu
 * 2021/8/25 3:29 下午
 **/
abstract class BaseHaveFooterAdapter<T,  B:ViewBinding, F : ViewBinding> :RecyclerView.Adapter<BaseHaveFooterAdapter.ViewHolder<in ViewBinding>>{
    var onItemClick: ((view: View, position: Int)->Unit)? = null
    var onFooterClick:((view:View)->Unit)?=null

    protected var mDatas:List<T>? = null
    protected var context: android.content.Context
    fun getmDatas(): List<T>? {
        return mDatas
    }

    fun setmDatas(mDatas:List<T>?) {
        this.mDatas = mDatas
    }

    constructor(context: android.content.Context) {
        this.context = context
    }

    constructor(context: android.content.Context, mDatas:List<T>?) {
        this.mDatas = mDatas
        this.context = context
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder<in ViewBinding> {
        val inflater = LayoutInflater.from(context)
        if (viewType == 0) {
            val binding = setBinding(inflater, parent)
            val viewHolder =  ViewHolder<B>(binding.root)
            viewHolder.binding=binding;
            if (onItemClick != null) {
                viewHolder.onItemClick = onItemClick
            }
            return viewHolder as ViewHolder<in ViewBinding>;
        } else {
            val binding = setFootBinding(inflater, parent)
            val viewHolder =
                BaseHaveFooterAdapter.ViewHolder<F>(binding!!.root)
            viewHolder.binding=binding;
            if (onItemClick != null) {
                viewHolder.onItemClick = onItemClick
            }
            return viewHolder as ViewHolder<in ViewBinding>;
        }
    }

    override fun onBindViewHolder(
        holder: ViewHolder<in ViewBinding>,
        position: Int
    ) {
        if (position == mDatas?.size) {
            val myholder =
                holder as ViewHolder<B>
            onBindFooterHolder(myholder, position)
        } else {
            val myholder =
                holder as ViewHolder<F>
            onBindHolder(myholder, position)
        }
    }

    protected abstract fun onBindHolder(
        holder: ViewHolder<F>,
        position: Int
    )

    protected abstract fun onBindFooterHolder(
        holder: ViewHolder<B>,
        position: Int
    )

    override fun getItemViewType(position: Int): Int {
        return if (position == mDatas!!.size) 1 else 0
    }

    override fun getItemId(position: Int): kotlin.Long {
        return position.toLong()
    }

    protected abstract fun setBinding(inflater: LayoutInflater?, parent: ViewGroup?): B
    protected abstract fun setFootBinding(inflater: LayoutInflater?, parent: ViewGroup?): F

    override fun getItemCount(): Int {
        return  if (mDatas == null) 0
        else if(mDatas!!.size==0)
            0
        else mDatas!!.size + 1
    }

    class ViewHolder<ViewBinding> (itemView:View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var onItemClick: ((view: View, position: Int)->Unit)? = null
        var onFooterClick:((view:View)->Unit)?=null
        var binding: ViewBinding? = null


        fun setClick(vararg views: View){
           onItemClick?.let {
               for (view in views) {
                   view.setOnClickListener(this)
               }
           }
        }

        fun setOnFooterClick(vararg views:View) {
            onFooterClick?.let {
                for (view in views) {
                    view.setOnClickListener { v ->
                        it(v)
                    }
                }
            }
        }

        override fun onClick(v: View){
            onItemClick?.let {
                it(v,adapterPosition)
            }
        }
    }

}