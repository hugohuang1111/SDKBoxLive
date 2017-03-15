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

    public class PackageRule {
        public String name;
        public MethodRule[] methodRules;
    }

    public PackageRule[] packageRule;
}
