package com.szfp.szfp.inter;

/**
 * 作者：ct on 2017/6/19 09:58
 * 邮箱：cnhttt@163.com
 */

public interface OnLoginListener {

    /**
     * Login successfully returns user information
     */
    void loginSuccess();

    /**
     * ＬＯＧＩＮ  Error   reason
     * @param reason
     */
    void loginError(String reason);
}
