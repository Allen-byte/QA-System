package com.example.questions.common;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.Data;

//用于对用户密码加密并返回加密后的密码
@Data
public class MD5 {
    private static final int PASSWORD_SALT_LENGTH=64;

    //返回加密md5码
    public static String generate(String rawPwd){
        String salt = IdUtil.simpleUUID();
        String encryptedPwd = DigestUtil.md5Hex(rawPwd + salt);
        char[] md5 = new char[PASSWORD_SALT_LENGTH];
        for(int i=0;i<PASSWORD_SALT_LENGTH;i++){
            md5[i] = encryptedPwd.charAt(i/2);
            md5[++i] = salt.charAt(i/2);
        }
        return new String(md5);
    }

    //比较
    public static boolean verify(String password, String md5){
        char[] pwd = new char[32];
        char[] salt = new char[32];
        for(int i=0;i<PASSWORD_SALT_LENGTH;i++){
            pwd[i/2] = md5.charAt(i);
            salt[i/2] = md5.charAt(++i);
        }
        return DigestUtil.md5Hex(password + new String(salt)).equals(new String(pwd));
    }

    public static void main(String[] args) {
        String pwd = "123456";
        String md5 = "c6d595b538554769c52562bfd44dfc860a3d81786df18fe1979fdfefd55dc230";
        System.out.println(verify(pwd, md5));
    }

}
