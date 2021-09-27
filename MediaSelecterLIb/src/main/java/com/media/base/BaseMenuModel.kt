package com.media.base

import android.animation.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.viewbinding.ViewBinding
import com.media.R

/**
 * @author : yx^_^
 * @e-mail : 565749553@qq.com
 * @date : 2020/3/6 10:59
 * @desc : better than before
 */

abstract class BaseMenuModel<T : ViewBinding>{
    protected val binding: T
    protected val context: Activity
    protected var constraintLayout:ConstraintLayout
    var view:View?=null
    constructor(context: Activity,view:View) {
        this.view=view
        this.context=context
        this.binding=getBindingInstance()
        binding.root.id= View.generateViewId()

        if((context.findViewById<ConstraintLayout>(R.id.widget_bottom_menu_root)==null)){
           constraintLayout = ConstraintLayout(context)
            addView()
        }else{
            constraintLayout=context.findViewById(R.id.widget_bottom_menu_root)
        }
    }
    protected var menuWidth=ViewGroup.LayoutParams.MATCH_PARENT
    protected var menuHeight=ViewGroup.LayoutParams.MATCH_PARENT
    protected var gravityH=Gravity.LEFT
    protected var gravityV=Gravity.TOP


    protected abstract fun getBindingInstance(): T
    private fun addView(){

        constraintLayout.layoutParams= ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT)
        addAnim(constraintLayout)
        constraintLayout.id=R.id.widget_bottom_menu_root

        if(view==null) {
            (context.window.decorView as ViewGroup).addView(constraintLayout)
        }else{
            view?.let {
                (it.parent as ViewGroup).addView(constraintLayout)
            }
        }
    }
    fun showMenu(){
        if( constraintLayout.childCount>0){
            return
        }
        constraintLayout.isClickable=true
        constraintLayout.setBackgroundColor(Color.parseColor("#80000000"))
        constraintLayout.setOnClickListener({
            if(constraintLayout.childCount>0) {
                hindMenu()
            }
        })
        var cs=ConstraintSet()
        constraintLayout.addView(binding.root,menuWidth,menuHeight)
        cs.clone(constraintLayout)
        if(gravityH!=Gravity.LEFT){
            cs.connect(binding.root.id,ConstraintSet.RIGHT,constraintLayout.id,ConstraintSet.RIGHT)
        }
        if(gravityH!=Gravity.RIGHT) {
            cs.connect(binding.root.id, ConstraintSet.LEFT, constraintLayout.id, ConstraintSet.LEFT)
        }
        if(gravityV!=Gravity.TOP){
            cs.connect(binding.root.id,ConstraintSet.BOTTOM,constraintLayout.id,ConstraintSet.BOTTOM)
        }
        if(gravityV!=Gravity.BOTTOM){
            cs.connect(binding.root.id,ConstraintSet.TOP,constraintLayout.id,ConstraintSet.TOP)
        }
        cs.applyTo(constraintLayout)
    }
    fun hindMenu(){
        constraintLayout.isClickable=false
        constraintLayout.setBackgroundColor(Color.parseColor("#00000000"))
        constraintLayout.removeView(binding.root)
    }
    private fun addAnim(v:ViewGroup){

        var mLayoutTransition =  LayoutTransition();
        mLayoutTransition.setAnimator(LayoutTransition.APPEARING, getAppearingAnimation());
        mLayoutTransition.setDuration(LayoutTransition.APPEARING, 200);
        mLayoutTransition.setStartDelay(LayoutTransition.APPEARING, 0);//源码中带有默认300毫秒的延时，需要移除，不然view添加效果不好！！

        mLayoutTransition.setAnimator(LayoutTransition.DISAPPEARING, getDisappearingAnimation());
        mLayoutTransition.setDuration(LayoutTransition.DISAPPEARING, 200);

        mLayoutTransition.setAnimator(LayoutTransition.CHANGE_APPEARING,getAppearingChangeAnimation());
        mLayoutTransition.setDuration(200);

        mLayoutTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING,getDisappearingChangeAnimation());
        mLayoutTransition.setDuration(200);

        mLayoutTransition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
        mLayoutTransition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);//源码中带有默认300毫秒的延时，需要移除，不然view添加效果不好！！
        mLayoutTransition.addTransitionListener( object:LayoutTransition.TransitionListener {
            override fun startTransition(
                transition: LayoutTransition?,
                container: ViewGroup?,
                view: View?,
                transitionType: Int
            ) {

            }

            override fun endTransition(
                transition: LayoutTransition?,
                container: ViewGroup?,
                view: View?,
                transitionType: Int
            ) {

            }

        });

            v.setLayoutTransition(mLayoutTransition);
    }
    @SuppressLint("ObjectAnimatorBinding")
    open protected  fun getAppearingAnimation(): Animator {
        var mSet = AnimatorSet();
        mSet.playTogether(
//            ObjectAnimator.ofFloat(null, "ScaleX", 2.0f, 1.0f),
//            ObjectAnimator.ofFloat(null, "ScaleY", 2.0f, 1.0f),
//            ObjectAnimator.ofFloat(null, "Alpha", 0.0f, 1.0f),
            ObjectAnimator.ofFloat(null,"translationY",400f,0f))
        return mSet;
    }

    @SuppressLint("ObjectAnimatorBinding")
    open protected fun getDisappearingAnimation():Animator {
        var mSet = AnimatorSet();
        mSet.playTogether(
//            ObjectAnimator.ofFloat(null, "ScaleX", 1.0f, 0f),
//            ObjectAnimator.ofFloat(null, "ScaleY", 1.0f, 0f),
//            ObjectAnimator.ofFloat(null, "Alpha", 1.0f, 0.0f),
            ObjectAnimator.ofFloat(null,"translationY",0f,400f));
        return mSet;
    }

    open protected fun getDisappearingChangeAnimation():Animator{
        var pvhLeft = PropertyValuesHolder.ofInt("left", 0, 0);
        var pvhTop = PropertyValuesHolder.ofInt("top", 0, 0);
        var pvhRight = PropertyValuesHolder.ofInt("right", 0, 0);
        var pvhBottom = PropertyValuesHolder.ofInt("bottom", 0, 0);
        var scaleX = PropertyValuesHolder.ofFloat("scaleX",1.0f,0f,1.0f);
        var scaleY = PropertyValuesHolder.ofFloat("scaleY",1.0f,0f,1.0f);
        var rotate = PropertyValuesHolder.ofFloat("rotation",0f,0f,0f);
        return ObjectAnimator.ofPropertyValuesHolder(pvhLeft, pvhTop, pvhRight, pvhBottom,scaleX,scaleY,rotate);
    }

    open protected fun getAppearingChangeAnimation():Animator{
        var pvhLeft = PropertyValuesHolder.ofInt("left", 0, 0);
        var pvhTop = PropertyValuesHolder.ofInt("top", 0, 0);
        var pvhRight = PropertyValuesHolder.ofInt("right", 0, 0);
        var pvhBottom = PropertyValuesHolder.ofInt("bottom", 0, 0);
        var scaleX = PropertyValuesHolder.ofFloat("scaleX",1.0f,3f,1.0f);
        var scaleY = PropertyValuesHolder.ofFloat("scaleY",1.0f,3f,1.0f);
        return ObjectAnimator.ofPropertyValuesHolder(pvhLeft, pvhTop, pvhRight, pvhBottom,scaleX,scaleY);
    }

    fun removeView() {
        constraintLayout.removeView(binding.root)
        if(view==null) {
            (context.window.decorView as ViewGroup).removeView(constraintLayout)
        }else{
                view?.let {
                    (it.parent as ViewGroup).removeView(constraintLayout)
                }

        }
    }

}
