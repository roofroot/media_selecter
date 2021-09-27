package com.jiaoday.im.base

import android.app.Activity
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * @author : yx^_^
 * @e-mail : 565749553@qq.com
 * @date : 2020/3/5 18:09
 * @desc : better than before
 */
abstract class BaseModel<T : ViewBinding?> : View.OnClickListener {
    protected var binding: T
    lateinit var context: Activity;

    constructor(binding: T, context: Activity) {
        this.binding = binding
        this.context = context
    }

    constructor(binding: T) {
        this.binding = binding
    }

    abstract fun onResume()
    abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    abstract fun onStop()
    abstract fun onPause()
    abstract fun onDestroy()
    abstract fun onNewIntent(intent: Intent?)
    abstract fun onRestart()
    abstract fun onBackPressed()
    abstract fun onStart()
    abstract fun dispatchKeyEvent(event: KeyEvent)
    abstract fun onKeyDown(keyCode: Int, event: KeyEvent)
    abstract fun onKeyUp(keyCode: Int, event: KeyEvent)
    abstract fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    )
    abstract fun  onAttachedToWindow()

    protected abstract fun toLoginView()
    protected fun bindListener(vararg views: View) {
        for (view in views) {
            view.setOnClickListener(this)
        }
    }
}