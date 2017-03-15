package com.sdkbox.live.gradle.asm

import com.sdkbox.live.gradle.utils.Rule
import jdk.internal.org.objectweb.asm.MethodVisitor
import jdk.internal.org.objectweb.asm.Opcodes

public class InjectMethodVisitor extends MethodVisitor {

    private Rule.PackageRule pRule

    public class methInfo {
        def owner;
        def name;
        def signature;
    }

    public class TransMethInfo {
        def methInfoOrigin;
        def methInfoNew;

        public TransMethInfo() {
            methInfoOrigin = new methInfo()
            methInfoNew = new methInfo()
        }
    }

    def transMap = null;

    public class Callback {
        public void onModify() {
        }
    }

    private Callback cb;

    public InjectMethodVisitor(MethodVisitor mv, Rule.PackageRule r) {
        super(Opcodes.ASM5, mv)
        pRule = r
        transMap = new ArrayList<TransMethInfo>()
    }

    public void registerCallback(Callback cb) {
        this.cb = cb;
    }

    public void addTransMethInfo(TransMethInfo m) {
        transMap.add(m)
    }

    private List<TransMethInfo> findTransMethInfo(String owner, String name, String signature) {
        List<TransMethInfo> l = new ArrayList<TransMethInfo>();

        for (TransMethInfo m : transMap) {
            if (m.methInfoOrigin.owner == owner
                    && m.methInfoOrigin.name == name
                    && m.methInfoOrigin.signature == signature) {
                l.add(m)
            }
        }

        return l
    }

    private Rule.MethodInfo findNewMethod(String owner, String name, String signature) {
        for (Rule.MethodRule mRule : pRule.methodRules) {
            def tMethod = mRule.targetMethod
            if (tMethod.owner == owner
                    && tMethod.func == name
                    && tMethod.signature == signature) {
                return mRule.newMethod
            }
        }

        return null
    }

    @Override
    void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        def replace = false
        def m = findNewMethod(owner, name, desc)
        if (null != m) {
            def nSignature = m.signature
            if (Opcodes.INVOKESTATIC == opcode) {
                nSignature = desc
            }
            if (null == nSignature) {
                def signatureSuff = desc.substring(1)
                nSignature = "(L$owner;$signatureSuff"
            }
            super.visitMethodInsn(Opcodes.INVOKESTATIC,
                    m.owner, m.func, nSignature, itf)
            replace = true
        }

        if (!replace) {
            super.visitMethodInsn(opcode, owner, name, desc, itf)
        }
    }

}

