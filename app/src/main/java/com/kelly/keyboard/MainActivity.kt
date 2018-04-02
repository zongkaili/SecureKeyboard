package com.kelly.keyboard

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.blankj.utilcode.util.Utils
import com.kelly.keyboard.databinding.ActivityMainBinding

class MainActivity : BaseBindingActivity<ActivityMainBinding>() {
    override fun getChildActivity(): Activity = this

    override fun getLayoutId(): Int  = R.layout.activity_main

    // 滚动距离
    var height = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.init(this)

        mBinding.viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java).apply {

        }

        mBinding.etKeyboard.setKeyboardType(mBinding.llContent, mBinding.contentKeyboard!!.viewKeyboard, true)
        mBinding.etKeyboard.listener = object : KeyBoardEditText.OnKeyboardStateChangeListener {
            override fun show() {
                mBinding.etKeyboard.post {
                    val rect = IntArray(2)
                    mBinding.etKeyboard.getLocationOnScreen(rect)
                    val y = rect[1]

                    mBinding.contentKeyboard!!.viewKeyboard?.getLocationOnScreen(rect)
                    val keyboardY = rect[1]

                    height = y-(keyboardY-mBinding.etKeyboard.measuredHeight)
                    mBinding.llRoot.scrollBy(0, height)
                }

            }

            override fun hide() {
                mBinding.llRoot.scrollBy(0, -height)
            }
        }
    }
}
