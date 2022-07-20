package com.hzy.password

/**
 * Created by ziye_huang on 2017/9/25.
 */
interface Callback {
    /**
     * 忘记密码
     */
    fun onForgetPassword()

    /**
     * 密码输入完成
     */
    fun onInputCompleted(password: MutableList<CharSequence>)

    /**
     * 密码错误
     */
    fun onPasswordCorrectly()

    /**
     * 取消
     */
    fun onCancel()
}