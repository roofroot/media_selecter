package com.jiaoday.im.base

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
//import com.alibaba.android.arouter.launcher.ARouter

abstract class MyBaseActivity<T : ViewBinding, M : BaseModel<*>> :
    AppCompatActivity() {
    lateinit var binding: T;
    lateinit var model: M;
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        model?.onKeyDown(keyCode, event)
        return super.onKeyDown(keyCode, event)
    }



    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onPrepare()
        binding = getBindingInstance()
        setContentView(binding!!.root)
//        ARouter.getInstance().inject(this)
        model = getModelInstance()
    }

    override fun onDestroy() {
        super.onDestroy()
         model?.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        model?.onStop()
    }

    override fun onPause() {
        super.onPause()
        model?.onPause()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        model.onAttachedToWindow()
    }

    override fun onResume() {
        super.onResume()
        model?.onResume()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        model?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRestart() {
        super.onRestart()
        model?.onRestart()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        model?.onNewIntent(intent)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        model?.onKeyUp(keyCode, event)
        return super.onKeyUp(keyCode, event)
    }

    override fun onStart() {
        super.onStart()
        model?.onStart()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        model?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        model?.onBackPressed()
    }


    protected abstract fun onPrepare()
    protected abstract fun getBindingInstance(): T
    protected abstract fun getModelInstance(): M
 
}
