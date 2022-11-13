package com.media.base
import android.app.Activity
import android.content.Intent

/**
 * @author : yx^_^
 * @e-mail : 565749553@qq.com
 * @date : 2020/3/12 15:53
 * @desc : better than before
 */
abstract class NoBindingBaseModel(protected var context: Activity) {
    abstract fun onResume()
    abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    abstract fun onStop()
    abstract fun onPause()
    abstract fun onDestroy()
    abstract fun onNewIntent(intent: Intent?)
    abstract fun onRestart()
    abstract fun onBackPressed()
    abstract fun onStart()
    abstract fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    )

    protected fun finishAnim() {
         context.finish()
    }

    protected fun startActivityAnim(intent: Intent?) {
        context.startActivity(intent)

    }

    protected fun startActivityForResultAnim(intent: Intent?, requestCode: Int) {
        context.startActivityForResult(intent, requestCode)
    }

}