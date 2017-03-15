package com.sdkbox.live.gradle.asm

import jdk.internal.org.objectweb.asm.ClassVisitor
import jdk.internal.org.objectweb.asm.MethodVisitor
import jdk.internal.org.objectweb.asm.Opcodes

public class ApplicationClassVisitor extends ClassVisitor implements Opcodes {

    private boolean modify;

    public ApplicationClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv)
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if ('onCreate' != name) {
            return mv
        }
        def customMV = new MethodVisitor(mv) {

            @Override
            void visitInsn(int opcode) {
                if (opcode == Opcodes.RETURN) {
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                            "android/app/Application",
                            "getApplicationContext",
                            "()Landroid/content/Context;",
                            false)
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                            "com/sdkbox/live/SDKBox", "start",
                            "(Landroid/content/Context;)V",
                            false)
                }
                super.visitInsn(opcode)
            }
        }

        return customMV;
    }

}
