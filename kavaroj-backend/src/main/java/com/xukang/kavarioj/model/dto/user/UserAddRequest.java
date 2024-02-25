package com.xukang.kavarioj.model.dto.user;

import java.io.Serializable;

import lombok.Data;

/**
 * 用户创建请求
 */
@Data
public class UserAddRequest implements Serializable {

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 重复密码
     */
    private String checkPassword;





    private static final long serialVersionUID = 1L;
}