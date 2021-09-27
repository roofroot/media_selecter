package com.media.manager

import com.media.entity.LocalMedia
import com.media.entity.LocalMediaFolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.selects.select
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * yuxiu
 * 2021/8/27
 **/
class SelectManager{
    companion object {
        @Volatile
        private var selectFolder: HashMap<Long, Int>? = null
        @Volatile
        private var selectList: HashMap<Long,LocalMedia>? = null
        private var selectId: String = ""
        private var selectChangeObservable: SelectChangeObservable = SelectChangeObservable()

        @Volatile
        private var manager: SelectManager? = null
        fun getInstance() =
            manager ?: synchronized(this) {
//                if(selectFolder==null){
//                    selectFolder=HashMap<Long,Int>()
//                    selectList=ArrayList<LocalMedia>()
//                    selectId=""
//                }
                manager ?: SelectManager().also { manager = it }
            }
        fun getSelectFolder() =
             selectFolder ?: synchronized(this) {
                selectFolder ?: HashMap<Long, Int>().also { selectFolder = it }
            }
        fun getSelectList() =
            selectList ?: synchronized(this) {
                selectList ?:HashMap<Long,LocalMedia>().also { selectList = it }
            }

        fun destroy() {
            selectFolder = null
            selectList = null
            manager = null
        }
    }

    fun addListener(callback: SelectChangeObserver, isSticky: Boolean) {
        selectChangeObservable.addSelectChangeObserver(callback)
        if (isSticky) {
            notifyChange(true)
        }
    }

    fun notifyChange(isSticky: Boolean) {
        selectChangeObservable.setDataChange()
        selectChangeObservable.notifyObservers(OnChangeInfo(isSticky))
    }

    fun removeListener(callback: SelectChangeObserver) {
        selectChangeObservable.deleteSelectChangeObserver(callback)
    }

    fun addMedia(media: LocalMedia) {

                  val a=  getSelectList().put(media.id,media)

                        selectId += "${media.id};"
                        if (getSelectFolder().containsKey(media.bucketId)) {
                            getSelectFolder().get(media.bucketId)?.let {
                                getSelectFolder().put(media.bucketId, it + 1)
                            }
                        } else {
                            getSelectFolder().put(media.bucketId, 1)
                        }
                        notifyChange(false)




    }

    fun removeMedia(media: LocalMedia) {
                     getSelectList().remove(media.id)
                     selectId= selectId.replace("${media.id};", "")
                        if (getSelectFolder().containsKey(media.bucketId)) {
                            getSelectFolder().get(media.bucketId)?.let {
                                getSelectFolder().put(media.bucketId, it - 1)
                            }
                        }
                        notifyChange(false)

    }

    fun getIdIndex(id: String): Int {
        selectId?.let { selectId ->
            if(selectId.contains("${id};")) {
                var arr = selectId.split(";")
                for ((index, str) in arr.withIndex()) {
                    if (id == str) {
                        return index + 1;
                    }
                }
            }
        }
        return -1
    }

    fun clear() {
        selectFolder?.let {
            it.clear();
        }
        selectList?.let {
            it.clear()
        }
        selectId = ""
    }


}

class SelectChangeObservable : Observable() {
    fun addSelectChangeObserver(observer: Observer) {
        addObserver(observer)
    }

    fun deleteSelectChangeObserver(observer: Observer) {
        deleteObserver(observer)
    }

    fun setDataChange() {
        setChanged()
    }
}

data class OnChangeInfo(val isSticky: Boolean)
interface SelectChangeObserver : Observer {
    override fun update(o: Observable?, arg: Any?) {
        var info = arg as OnChangeInfo
        onSelectChange(info.isSticky)
    }

    fun onSelectChange(isSticky: Boolean)
}