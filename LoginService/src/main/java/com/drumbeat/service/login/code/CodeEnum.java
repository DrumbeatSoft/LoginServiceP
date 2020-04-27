package com.drumbeat.service.login.code;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.drumbeat.service.login.R;

import static com.drumbeat.service.login.code.CodeFlag.FLAG_0_SCAN_CODE;
import static com.drumbeat.service.login.code.CodeFlag.FLAG_10_MODIFY_PWD;

/**
 * 接口状态码
 *
 * @author ZuoHailong
 * @date 2020/4/27
 */
public enum CodeEnum {

    /**
     * 依据语雀状态码表
     */
    FAIL_STRING_UNKNOW_WITH_CODE(R.string.dblogin_fail_uncontrolled_code),
    FAIL_STRING_NEGATIVE_1(R.string.dblogin_fail_negative_1),
    FAIL_STRING_0(R.string.dblogin_fail_0),
    FAIL_STRING_0_SCAN_CODE(R.string.dblogin_fail_0_scancode),
    FAIL_STRING_10(R.string.dblogin_fail_10),
    FAIL_STRING_10_MODIFY_PWD(R.string.dblogin_fail_10_modifypwd),
    FAIL_STRING_11(R.string.dblogin_fail_11),
    FAIL_STRING_12(R.string.dblogin_fail_12),
    FAIL_STRING_13(R.string.dblogin_fail_13),
    FAIL_STRING_14(R.string.dblogin_fail_14),
    FAIL_STRING_15(R.string.dblogin_fail_15),
    FAIL_STRING_16(R.string.dblogin_fail_16),
    FAIL_STRING_17(R.string.dblogin_fail_17),
    FAIL_STRING_18(R.string.dblogin_fail_18),
    FAIL_STRING_20(R.string.dblogin_fail_20),
    FAIL_STRING_21(R.string.dblogin_fail_21),
    FAIL_STRING_22(R.string.dblogin_fail_22),
    FAIL_STRING_23(R.string.dblogin_fail_23),
    FAIL_STRING_30(R.string.dblogin_fail_30),
    FAIL_STRING_80(R.string.dblogin_fail_80),
    FAIL_STRING_81(R.string.dblogin_fail_81),
    FAIL_STRING_100(R.string.dblogin_fail_100),
    FAIL_STRING_101(R.string.dblogin_fail_101),
    FAIL_STRING_102(R.string.dblogin_fail_102),
    FAIL_STRING_103(R.string.dblogin_fail_103),
    FAIL_STRING_104(R.string.dblogin_fail_104),
    FAIL_STRING_200(R.string.dblogin_fail_200),
    FAIL_STRING_201(R.string.dblogin_fail_101),
    FAIL_STRING_401(R.string.dblogin_fail_401),
    FAIL_STRING_403(R.string.dblogin_fail_403),
    FAIL_STRING_412(R.string.dblogin_fail_412),
    FAIL_STRING_500(R.string.dblogin_fail_500),
    FAIL_STRING_1000(R.string.dblogin_fail_1000),
    FAIL_STRING_1001(R.string.dblogin_fail_1001),
    FAIL_STRING_1002(R.string.dblogin_fail_1002),
    FAIL_STRING_1003(R.string.dblogin_fail_1003),
    FAIL_STRING_1004(R.string.dblogin_fail_1004),
    FAIL_STRING_1005(R.string.dblogin_fail_1005),
    FAIL_STRING_1006(R.string.dblogin_fail_1006),
    FAIL_STRING_1200(R.string.dblogin_fail_1200),
    FAIL_STRING_1201(R.string.dblogin_fail_1201);

    private int stringResId;

    private CodeEnum(@StringRes int stringResId) {
        this.stringResId = stringResId;
    }

    public int getStringResId() {
        return this.stringResId;
    }

    public static CodeEnum valueOf(int code) {
        return valueOf(code, null);
    }

    /**
     * @param code 接口返回状态码
     * @param flag 状态码含义有冲突时的标识位
     * @return
     */
    public static CodeEnum valueOf(int code, @NonNull @CodeFlagDef String flag) {
        switch (code) {
            case -1:
                return FAIL_STRING_NEGATIVE_1;
            case 0:
                if (FLAG_0_SCAN_CODE.equals(flag)) {
                    return FAIL_STRING_0_SCAN_CODE;
                } else {
                    return FAIL_STRING_0;
                }
            case 10:
                if (FLAG_10_MODIFY_PWD.equals(flag)) {
                    return FAIL_STRING_10_MODIFY_PWD;
                } else {
                    return FAIL_STRING_10;
                }
            case 11:
                return FAIL_STRING_11;
            case 12:
                return FAIL_STRING_12;
            case 13:
                return FAIL_STRING_13;
            case 14:
                return FAIL_STRING_14;
            case 15:
                return FAIL_STRING_15;
            case 16:
                return FAIL_STRING_16;
            case 17:
                return FAIL_STRING_17;
            case 18:
                return FAIL_STRING_18;
            case 20:
                return FAIL_STRING_20;
            case 21:
                return FAIL_STRING_21;
            case 22:
                return FAIL_STRING_22;
            case 23:
                return FAIL_STRING_23;
            case 30:
                return FAIL_STRING_30;
            case 80:
                return FAIL_STRING_80;
            case 81:
                return FAIL_STRING_81;
            case 100:
                return FAIL_STRING_100;
            case 101:
                return FAIL_STRING_101;
            case 102:
                return FAIL_STRING_102;
            case 103:
                return FAIL_STRING_103;
            case 104:
                return FAIL_STRING_104;
            case 200:
                return FAIL_STRING_200;
            case 201:
                return FAIL_STRING_201;
            case 401:
                return FAIL_STRING_401;
            case 403:
                return FAIL_STRING_403;
            case 412:
                return FAIL_STRING_412;
            case 500:
                return FAIL_STRING_500;
            case 1000:
                return FAIL_STRING_1000;
            case 1001:
                return FAIL_STRING_1001;
            case 1002:
                return FAIL_STRING_1002;
            case 1003:
                return FAIL_STRING_1003;
            case 1004:
                return FAIL_STRING_1004;
            case 1005:
                return FAIL_STRING_1005;
            case 1006:
                return FAIL_STRING_1006;
            case 1200:
                return FAIL_STRING_1200;
            case 1201:
                return FAIL_STRING_1201;
            default:
                return FAIL_STRING_UNKNOW_WITH_CODE;
        }
    }
}
