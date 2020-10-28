package com.hzy.password

import android.content.Context
import android.graphics.Paint
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.GridLayout
import java.util.*

/**
 * 密码键盘
 */
class PasswordKeyboard : GridLayout, View.OnClickListener, View.OnTouchListener {

    val DEL = "删除"
    val DONE = "OK"
    private val DELETE = 1

    private var paint: Paint? = null

    /**
     * 因为UED给的是iphone的设计稿，所以是按照等比的思想设置键盘key的高度和宽度
     */
    private val IPHONE = 779

    /**
     * 每个键盘key的宽度，为屏幕宽度的三分之一
     */
    private val keyWidth =
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.width / 3

    /**
     * 每个键盘key的高度
     */
    private val keyHeight =
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.height * 59 / IPHONE

    /**
     * 屏幕的宽度
     */
    private val screenWidth =
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.width

    /**
     * List集合存储key，方便每次输错都能再次随机数字键盘
     */
    private val keyButtons = arrayListOf<Button>()

    private var workHandler: WorkHandler? = null


    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {

    }

    override fun onClick(v: View?) {

    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (workHandler == null) {
//            workHandler =
        }

        return false
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        setMeasuredDimension(screenWidth, keyHeight * 4)
    }

    /**
     * 重新设置键盘key位置
     */
    fun resetKeyboard() {

    }

    /**
     * 随机生成键盘key数字
     */
    fun randomKeys(num: Int): List<String> {
        val keys = Array(num) { it }

        return arrayListOf()
    }

    /**
     * 监听密码输入
     */
    interface OnPasswordInputListener {
        fun onInput(number: String)
    }

    inner class WorkHandler : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                DELETE -> {
                    val pk = msg.obj as PasswordKeyboard

                }
            }
        }

    }
}