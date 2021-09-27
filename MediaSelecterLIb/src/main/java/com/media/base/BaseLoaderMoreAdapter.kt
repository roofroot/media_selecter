package com.media.base

import android.app.Activity
import android.media.SoundPool
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import com.media.utils.LogUtils
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


/**
 * yuxiu
 * 2021/8/25 3:29 下午
 **/
abstract class BaseLoaderMoreAdapter<T,  B:ViewBinding> :RecyclerView.Adapter<BaseLoaderMoreAdapter.ViewHolder<in ViewBinding>>, CoroutineScope
{
    private val job= Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    var onItemClick: ((view: View, position: Int)->Unit)? = null
    private var onLoadMore:(()->Boolean)?=null
    private var loadComplete:(()->Unit)?=null

    fun setLoadListener(loading:(()->Boolean),loadComplete:(()->Unit)){
        this.onLoadMore=loading
        this.loadComplete=loadComplete
    }

    protected var mDatas:ArrayList<T>? = null
    protected var context: android.content.Context
    fun getmDatas(): ArrayList<T>? {
        return mDatas
    }

    fun setmDatas(mDatas:ArrayList<T>?) {
        this.mDatas = mDatas
    }

    constructor(context: android.content.Context) {
        this.context = context
    }

    constructor(context: android.content.Context, mDatas:ArrayList<T>?) {
        this.mDatas = mDatas
        this.context = context
    }
    private val state_no_more=1;
    private val state_load=2;
    private var loadState=state_load;
    private val view_type_footer=1;
    private val view_type_item=0;
    private var isLoading=false;
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
            if(loadState==state_no_more) {
                val binding = setFootNoMoreBinding(inflater, parent)
                val viewHolder = ViewHolder<ViewBinding>(binding.root)
                viewHolder.binding = binding;
                if (onItemClick != null) {
                    viewHolder.onItemClick = onItemClick
                }
                return viewHolder;
            }else{
                val binding = setFootLoadingBinding(inflater, parent)
                val viewHolder = ViewHolder<ViewBinding>(binding.root)
                viewHolder.binding = binding;
                if (onItemClick != null) {
                    viewHolder.onItemClick = onItemClick
                }
                return viewHolder;
            }
        }
    }

    override fun onBindViewHolder(
        holder: ViewHolder<in ViewBinding>,
        position: Int
    ) {
        if (position == mDatas?.size) {
            val myholder =
                holder
            onBindFooterHolder(myholder, position)
        } else {
            val myholder = holder as ViewHolder<B>
            onBindHolder(myholder, position)
        }
    }

    protected abstract fun onBindHolder(
        holder: ViewHolder<B>,
        position: Int
    )

    private fun onBindFooterHolder(
        holder: ViewHolder<in ViewBinding>,
        position: Int
    ){
        holder.binding?.let {
            it as ViewBinding
            if (isLoading) {
                it.root.visibility = View.VISIBLE
            } else {
                it.root.visibility = View.GONE
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == mDatas!!.size) view_type_footer else view_type_item
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getItemCount(): Int {
        return  if (mDatas == null) 0
        else if(mDatas!!.size==0)
            0
        else mDatas!!.size + 1
    }

    protected abstract fun setBinding(inflater: LayoutInflater, parent: ViewGroup): B
    protected abstract fun setFootNoMoreBinding(inflater: LayoutInflater, parent: ViewGroup): ViewBinding
    protected abstract fun setFootLoadingBinding(inflater: LayoutInflater, parent: ViewGroup): ViewBinding

    class ViewHolder<B:ViewBinding> (itemView:View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var onItemClick: ((view: View, position: Int)->Unit)? = null
        var onFooterClick:((view:View)->Unit)?=null
        var binding: B? = null


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

    override fun onViewAttachedToWindow(holder: ViewHolder<in ViewBinding>) {
        super.onViewAttachedToWindow(holder)
        if (getItemViewType(holder.getLayoutPosition())==view_type_footer) {
            val lp = holder.itemView.layoutParams
            if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams) {
                lp.isFullSpan = true
            }
        }
    }
    private var rv:RecyclerView?=null;

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            val gridManager = layoutManager
            gridManager.spanSizeLookup = object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (getItemViewType(position)==view_type_footer) {
                        gridManager.spanCount
                    } else 1
                }
            }
        }
        rv=recyclerView
        rv?.let {
            startLoadMore(it,layoutManager)
        }

    }

    private fun startLoadMore(
        recyclerView: RecyclerView,
        layoutManager: RecyclerView.LayoutManager?
    ) {
       layoutManager?.let {

           recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
               override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                   super.onScrollStateChanged(recyclerView, newState)
                   if(newState==RecyclerView.SCROLL_STATE_IDLE) {
                       if (findLastVisibleItemPosition(layoutManager) + 1 === itemCount) {
                           recyclerView.post {
                               onScrollToBottom()
                           }

                       }
                   }
               }
               override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                   super.onScrolled(recyclerView, dx, dy)
//                   if (findLastVisibleItemPosition(layoutManager)+1  === itemCount) {
//                       recyclerView.post {
//                           onScrollToBottom()
//                       }
//
//                   }
               }
           })

       }

    }

    private  fun findLastVisibleItemPosition(layoutManager: RecyclerView.LayoutManager):Int{
        var position=-10
        if(isLoading){
            return position
        }else
           if(layoutManager is LinearLayoutManager){
            position=layoutManager.findLastVisibleItemPosition()

        }else if(layoutManager is GridLayoutManager){
            position=layoutManager.findLastVisibleItemPosition()
        }
        LogUtils.e("${position}")
        return position
    }
    fun loadComplete()
    {
        isLoading=false
        notifyItemChanged(itemCount-1)

    }
    private fun onScrollToBottom(){
        isLoading=true
            notifyItemChanged(itemCount-1)
        launch {
            var a= loadMore()
            LogUtils.e("result${a}")
            loadComplete?.let {
                it()
            }
        }
    }
    private suspend fun loadMore():Boolean=withContext(Dispatchers.IO){
        onLoadMore?.let {
           it()
        }
        return@withContext true
    }

}