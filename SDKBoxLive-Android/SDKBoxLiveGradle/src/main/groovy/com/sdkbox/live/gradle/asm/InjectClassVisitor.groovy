package com.sdkbox.live.gradle.asm

import com.sdkbox.live.gradle.utils.Rule
import jdk.internal.org.objectweb.asm.ClassVisitor
import jdk.internal.org.objectweb.asm.MethodVisitor
import jdk.internal.org.objectweb.asm.Opcodes

public class InjectClassVisitor extends ClassVisitor implements Opcodes {

    private boolean modify;
    private Rule.PackageRule pRule;

    public InjectClassVisitor(ClassVisitor cv, Rule.PackageRule r) {
        super(Opcodes.ASM5, cv)
        pRule = r
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        InjectMethodVisitor bmv = new InjectMethodVisitor(mv, pRule)

        return bmv;
    }

}
