package com.chenu.pvcstumansys.compoent;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * 基本作用：自定义的MD5加密者
 * 详细解释：用于Spring Security
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
public class MyMd5PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        Md5PasswordEncoder md5PasswordEncoder = new Md5PasswordEncoder();
        String epwd = md5PasswordEncoder.encodePassword(String.valueOf(rawPassword), "chenu");
        return epwd;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodePassword) {
        Md5PasswordEncoder md5PasswordEncoder = new Md5PasswordEncoder();
        String mw = md5PasswordEncoder.encodePassword(String.valueOf(rawPassword), "chenu");
        return mw.equals(encodePassword);
    }

}
