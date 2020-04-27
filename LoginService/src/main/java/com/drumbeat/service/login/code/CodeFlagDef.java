package com.drumbeat.service.login.code;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * CodeFlag 注解
 *
 * @author ZuoHailong
 * @date 2020/4/27
 */
@StringDef({
        CodeFlag.FLAG_0_SCAN_CODE,
        CodeFlag.FLAG_10_MODIFY_PWD,
})
@Retention(RetentionPolicy.SOURCE)
public @interface CodeFlagDef {
}
