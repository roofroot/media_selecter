package com.media.manager

import android.content.Context
import com.media.db.LocalMediaLoader
import com.media.entity.LocalMediaFolder
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * yuxiu
 * 2021/8/27
 **/
class DataManager : CoroutineScope {
    private val job= Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    companion object{
        private var mediaData:ArrayList<LocalMediaFolder>?=null
        @Volatile
        private var manager: DataManager? = null
        fun getInstance() =
            manager ?: synchronized(this) {
                manager ?: DataManager().also { manager = it }
            }
        fun destroy(){
            mediaData=null
            manager=null
        }

    }
    fun getMediaData(context: Context,success:((mediaData:ArrayList<LocalMediaFolder>)->Unit)){
           if(mediaData==null) {
               launch {
                   mediaData = loadData(context)
                   mediaData?.let {
                       success(it)
                   }
               }
           }else{
               mediaData?.let{
                   success(it)
               }
           }
    }
    private suspend fun loadData(context: Context):ArrayList<LocalMediaFolder>?= withContext(Dispatchers.IO) {
        return@withContext LocalMediaLoader(context).loadAllMedia()
    }
}