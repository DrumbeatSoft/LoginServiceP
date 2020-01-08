package com.hailong.zbase.http.kalle;

import android.text.TextUtils;

import com.hailong.zbase.helper.SPStaticHelper;
import com.hailong.zbase.http.constant.HttpConstant;
import com.yanzhenjie.kalle.Headers;
import com.yanzhenjie.kalle.Request;
import com.yanzhenjie.kalle.Response;
import com.yanzhenjie.kalle.connect.Interceptor;
import com.yanzhenjie.kalle.connect.http.Chain;

import java.io.IOException;
import java.util.Set;

/**
 * Request 加签拦截器
 * Created by ZuoHailong on 2019/12/30.
 */
public class SignInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Headers headers = request.headers();
        headers.getDate();
        // 添加Headers
        Set<String> headerSet = SPStaticHelper.getStringSet(HttpConstant.SP_HEADERS);
        if (headerSet != null && !headerSet.isEmpty()) {
            for (String header : headerSet) {
                if (!TextUtils.isEmpty(header)) {
                    String[] keyValue = header.split(",");
                    if (keyValue.length == 2) {
                        // 新增header
                        request.headers().add(keyValue[0], keyValue[1]);
                    } else if (keyValue.length == 1) {
                        // 移除header
                        request.headers().remove(keyValue[0]);
                    }
                }
            }
        }
        // 执行请求
        return chain.proceed(request);
    }

}
