package com.unity3d.ads;

import java.util.concurrent.Executors;

/**
 * Created by Administrator
 * 创建时间：2018/6/9
 * 更新时间：
 * 更新人：
 * 描述：
 */

public class ThreadTool {

    public static void excute(Runnable runnable) {
        Executors.newSingleThreadExecutor().execute(runnable);
    }
}
