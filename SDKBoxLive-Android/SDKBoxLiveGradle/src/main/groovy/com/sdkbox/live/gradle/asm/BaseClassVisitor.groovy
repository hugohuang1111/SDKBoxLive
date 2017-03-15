package com.sdkbox.live.gradle.asm

import jdk.internal.org.objectweb.asm.ClassVisitor
import jdk.internal.org.objectweb.asm.Opcodes

public class BaseClassVisitor extends ClassVisitor implements Opcodes {

    public BaseClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv)
    }

}