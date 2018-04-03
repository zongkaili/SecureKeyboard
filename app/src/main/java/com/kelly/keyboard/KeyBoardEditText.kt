package com.kelly.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.KeyboardUtils

/**
 * Author zongkaili
 * Date 2018/4/2
 * Dsc:
 */
class KeyBoardEditText: AppCompatEditText {
    private var mContext: Context? = null
    private var mKeyboardNumber: Keyboard? = null
    private var mKeyboardLetter: Keyboard? = null
    private var mKeyboardSymbol: Keyboard? = null
    private var mKeyboardView: KeyboardView? = null
    private var mKeyboardParent: ViewGroup? = null
    private var mKeyboardType = KEYBOARD_TYPE_NUMBER// 当前键盘类型，默认数字键盘
    var mListener: OnKeyboardStateChangeListener? = null
    var isCapital = false   // 是否为大写字母

    companion object {
        private const val KEYCODE_SPACE = 32
        private const val KEYCODE_LETTER = -33
        private const val KEYCODE_SYMBOL = -34
        private const val KEYBOARD_TYPE_NUMBER = 0x001
        private const val KEYBOARD_TYPE_LETTER = 0x002
        private const val KEYBOARD_TYPE_SYMBOL = 0x003
    }

    constructor(context: Context) : super(context) {
        mContext = context
        initEditView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        initEditView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        initEditView()
    }

    private fun initEditView() {
        mKeyboardNumber = Keyboard(mContext, R.xml.keyboard_number)
        mKeyboardLetter = Keyboard(mContext, R.xml.keyboard_letter)
        mKeyboardSymbol = Keyboard(mContext, R.xml.keyboard_symbol)
    }

    fun setKeyboardType(viewGroup: ViewGroup, keyboardView: KeyboardView, number: Boolean) {
        this.mKeyboardView = keyboardView
        this.mKeyboardParent = viewGroup
        if (number) {
            keyboardView.keyboard = mKeyboardNumber
        }
        keyboardView.isPreviewEnabled = true
        keyboardView.setOnKeyboardActionListener(object : KeyboardView.OnKeyboardActionListener {
            override fun swipeRight() {

            }

            override fun swipeLeft() {

            }

            override fun swipeUp() {

            }

            override fun swipeDown() {

            }

            override fun onPress(primaryCode: Int) {
                canShowPreview(primaryCode)
            }

            override fun onRelease(primaryCode: Int) {

            }

            override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
                val editable = text
                val start = selectionStart
                // 删除
                if (primaryCode == Keyboard.KEYCODE_DELETE) {
                    if (editable.isNotEmpty() && start>0) {
                        editable.delete(start-1, start)
                    }
                }
                // 数字键盘
                else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {
                    changeKeyBoard(KEYBOARD_TYPE_NUMBER)
                }
                // 字母键盘
                else if (primaryCode == KEYCODE_LETTER) {
                    changeKeyBoard(KEYBOARD_TYPE_LETTER)
                }
                // 符号键盘
                else if (primaryCode == KEYCODE_SYMBOL) {
                    changeKeyBoard(KEYBOARD_TYPE_SYMBOL)
                }
                // 完成 (暂时未用)
                else if (primaryCode == Keyboard.KEYCODE_DONE) {
                    keyboardView.visibility = View.GONE
                    viewGroup.visibility = View.GONE
                    mListener?.hide()
                }
                // 切换大小写
                else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
                    changeCapital(!isCapital)
                    keyboardView.keyboard = mKeyboardLetter
                }
                else {
                    editable.insert(start, Character.toString(primaryCode.toChar()))
                }
            }

            override fun onText(text: CharSequence?) {

            }
        })
    }

    fun onFinishClick() {
        mKeyboardView?.visibility = View.GONE
        mKeyboardParent?.visibility = View.GONE
        mListener?.hide()
    }

    fun changeKeyBoard(type: Int) {
        mKeyboardType = type
        mKeyboardView?.keyboard = when (type) {
            KEYBOARD_TYPE_NUMBER-> mKeyboardNumber
            KEYBOARD_TYPE_LETTER-> mKeyboardLetter
            KEYBOARD_TYPE_SYMBOL-> mKeyboardSymbol
            else-> mKeyboardNumber
        }
    }

    fun canShowPreview(primaryCode: Int) {
        val noPreviewKeys = arrayOf(Keyboard.KEYCODE_SHIFT, Keyboard.KEYCODE_MODE_CHANGE, Keyboard.KEYCODE_CANCEL,
                Keyboard.KEYCODE_DONE, Keyboard.KEYCODE_DELETE, Keyboard.KEYCODE_ALT, KEYCODE_SPACE, KEYCODE_LETTER, KEYCODE_SYMBOL)
        mKeyboardView?.isPreviewEnabled = !noPreviewKeys.contains(primaryCode)
    }

    fun changeCapital(isCapital: Boolean) {
        val lowercase = "abcdefghijklmnopqrstuvwxyz"

        val keyList = mKeyboardLetter?.keys
        keyList?.forEach {
            if (it?.label != null && lowercase.indexOf(it.label.toString().toLowerCase()) != -1) {
                if (isCapital) {
                    it.label = it.label.toString().toUpperCase()
                    it.codes[0] -= 32
                }
                else {
                    it.label = it.label.toString().toLowerCase()
                    it.codes[0] += 32
                }
            }
            if (it?.label!=null && it?.label == "小写" && isCapital) {
                it.label = "大写"
            }
            else if (it?.label!=null && it?.label == "大写" && !isCapital) {
                it.label = "小写"
            }
        }
        this.isCapital = isCapital
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        KeyboardUtils.hideSoftInput(this)
        if (event?.action == MotionEvent.ACTION_UP) {
            if (mKeyboardView?.visibility != View.VISIBLE) {
                mKeyboardView?.visibility = View.VISIBLE
                mKeyboardParent?.visibility = View.VISIBLE
                mListener?.show()
            }
        }
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mKeyboardView?.visibility != View.GONE
                && mKeyboardParent?.visibility != View.GONE) {
            mKeyboardView?.visibility = View.GONE
            mKeyboardParent?.visibility = View.GONE
            mListener?.hide()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        KeyboardUtils.hideSoftInput(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        KeyboardUtils.hideSoftInput(this)
    }

    interface OnKeyboardStateChangeListener {
        fun show()
        fun hide()
    }
}