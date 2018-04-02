package com.kelly.keyboard

import android.app.Activity
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

/**
 * Created by zongkaili on 2017/9/21.
 */
abstract class BaseBindingActivity<B : ViewDataBinding> : AppCompatActivity() {

    lateinit var mBinding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(getChildActivity(), getLayoutId())
    }

    abstract fun getChildActivity(): Activity

    abstract fun getLayoutId(): Int

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}