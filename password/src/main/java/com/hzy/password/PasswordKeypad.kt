package com.hzy.password

import android.app.Activity
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.hzy.password.MDProgressBar.OnPasswordCorrectlyListener
import com.hzy.password.PasswordKeyboard.OnPasswordInputListener

/**
 * Created by ziye_huang on 2017/9/25.
 */
class PasswordKeypad : DialogFragment(), View.OnClickListener, OnPasswordInputListener,
    OnPasswordCorrectlyListener {
    private var errorMsgTv: TextView? = null
    private var mCallback: Callback? = null
    private var passwordContainer: RelativeLayout? = null
    private var progressBar: MDProgressBar? = null
    private var passwordView: PasswordView? = null
    private var passwordCount = 0
    private var passwordState = true
    var numberKeyBoard: PasswordKeyboard? = null
    private val mPasswordBuffer = StringBuffer()
    override fun onAttach(context: Activity) {
        super.onAttach(context)
        if (context is Callback) {
            mCallback = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_password_keypad, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
        //则进行cancel，状态检测依次onCancel()和onDismiss()。如参数为false，则按空白处或返回键无反应。缺省为true
        isCancelable = false
    }

    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        val window = dialog!!.window
        //去掉边框
        window!!.setBackgroundDrawable(ColorDrawable(-0x1))
        window.setLayout(dm.widthPixels, window.attributes.height)
        window.setWindowAnimations(R.style.exist_menu_animstyle)
        window.setGravity(Gravity.BOTTOM)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorMsgTv = view.findViewById(R.id.error_msg)
        val forgetPasswordTv = view.findViewById<TextView>(R.id.forget_password)
        val cancelTv = view.findViewById<ImageView>(R.id.cancel_dialog)
        passwordContainer = view.findViewById(R.id.password_content)
        progressBar = view.findViewById(R.id.password_progressBar)
        progressBar?.setOnPasswordCorrectlyListener(this)
        passwordView = view.findViewById(R.id.password_inputBox)
        //设置密码长度
        if (passwordCount > 0) {
            passwordView?.passwordCount = passwordCount
        }
        numberKeyBoard = view.findViewById(R.id.password_keyboard)
        numberKeyBoard?.setOnPasswordInputListener(this)
        cancelTv.setOnClickListener(this)
        forgetPasswordTv.setOnClickListener(this)
    }

    /**
     * 设置密码长度
     */
    fun setPasswordCount(passwordCount: Int) {
        this.passwordCount = passwordCount
    }

    override fun onClick(v: View) {
        if (R.id.cancel_dialog == v.id) {
            if (mCallback != null) {
                mCallback!!.onCancel()
            }
            dismiss()
        } else if (R.id.forget_password == v.id) {
            if (mCallback != null) {
                mCallback!!.onForgetPassword()
            }
        }
    }

    fun setCallback(callBack: Callback?) {
        mCallback = callBack
    }

    fun setPasswordState(correct: Boolean) {
        setPasswordState(correct, "")
    }

    fun setPasswordState(correct: Boolean, msg: String?) {
        passwordState = correct
        if (correct) {
            progressBar!!.setSuccessfullyStatus()
        } else {
            numberKeyBoard!!.resetKeyboard()
            passwordView!!.clearPassword()
            progressBar!!.visibility = View.GONE
            passwordContainer!!.visibility = View.VISIBLE
            errorMsgTv!!.text = msg
        }
    }

    override fun onPasswordCorrectly() {
        if (mCallback != null) {
            mCallback!!.onPasswordCorrectly()
        }
    }

    private fun startLoading(password: CharSequence) {
        passwordContainer!!.visibility = View.INVISIBLE
        progressBar!!.visibility = View.VISIBLE
        if (mCallback != null) {
            mCallback!!.onInputCompleted(password)
        }
    }

    override fun onInput(character: String) {
        if (numberKeyBoard!!.DEL == character) {
            if (mPasswordBuffer.length > 0) {
                mPasswordBuffer.delete(mPasswordBuffer.length - 1, mPasswordBuffer.length)
            }
        } else if (numberKeyBoard!!.DONE == character) {
            dismiss()
        } else {
            if (!passwordState) {
                if (!TextUtils.isEmpty(errorMsgTv!!.text)) {
                    errorMsgTv!!.text = ""
                }
            }
            mPasswordBuffer.append(character)
        }
        passwordView!!.setPassword(mPasswordBuffer)
        if (mPasswordBuffer.length == passwordView!!.passwordCount) {
            Handler().postDelayed({ startLoading(mPasswordBuffer) }, 100)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (mPasswordBuffer.length > 0) {
            mPasswordBuffer.delete(0, mPasswordBuffer.length)
        }
    }
}