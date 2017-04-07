package com.sdkbox.live.gradle.utils;

public class Rule {

    public class MethodInfo {
        public String owner;
        public String func;
        public String signature;
    }

    public class MethodRule {
        public MethodInfo targetMethod;
        public MethodInfo newMethod;
    }

    public class ClassInfo {
        public String owner;
    }

    public class ClassRule {
        public ClassInfo targetClass;
    }

    public class PackageRule {
        public String name;
        public MethodRule[] methodRules;
        public ClassRule[] classRules;
    }

    public PackageRule[] packageRule;
}

/*

{
    "packageRule": [
        {
            "name": "inmobi",
            "methodRules": [
                {
                    "targetMethod": {
                        "owner": "java/net/URL",
                        "func": "openConnection",
                        "signature": "()Ljava/net/URLConnection;"
                    },
                    "newMethod": {
                        "owner": "com/sdkbox/live/bridge/InMobiBridge",
                        "func": "URL_openConnection"
                    }
                }
            ]
        },
        {
            "name": "SDKBoxLiveClient",
            "classRules": [
                {
                    "targetClass": {
                        "owner": "com/sdkbox/live/test/InMobiTest"
                    }
                }
            ]
        }
    ]
}

 */