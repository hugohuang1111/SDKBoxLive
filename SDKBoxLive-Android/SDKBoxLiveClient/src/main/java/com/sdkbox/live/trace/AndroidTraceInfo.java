package com.sdkbox.live.trace;

import java.lang.reflect.Type;

/**
 * Created by htl on 07/03/2017.
 */

public class AndroidTraceInfo extends TraceInfo {
    public AndroidTraceInfo() {
        super();
        platform = "android";
    }

    public String toString() {
        return super.toString(AndroidTraceInfo.class);
    }
}
