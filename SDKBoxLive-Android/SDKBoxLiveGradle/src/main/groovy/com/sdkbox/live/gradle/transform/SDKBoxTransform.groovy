package com.sdkbox.live.gradle.transform

import com.android.annotations.NonNull
import com.android.annotations.Nullable
import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.sdkbox.live.gradle.asm.ApplicationClassVisitor
import com.sdkbox.live.gradle.asm.InjectClassVisitor
import com.sdkbox.live.gradle.trace.Tracking
import com.sdkbox.live.gradle.utils.Log
import com.sdkbox.live.gradle.utils.Rule
import com.sdkbox.live.gradle.utils.Utils
import jdk.internal.org.objectweb.asm.ClassReader
import jdk.internal.org.objectweb.asm.ClassVisitor
import jdk.internal.org.objectweb.asm.ClassWriter
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

public class SDKBoxTransform extends Transform {
    private AppExtension android
    private Project project;
    private Rule rule;
    private String appName;
    private List<String> activityNames;

    public SDKBoxTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "SDKBox"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    public void transform(
            @NonNull Context context,
            @NonNull Collection<TransformInput> inputs,
            @NonNull Collection<TransformInput> referencedInputs,
            @Nullable TransformOutputProvider outputProvider,
            boolean isIncremental) throws IOException, TransformException, InterruptedException {
        android = project.extensions.getByType(AppExtension)

        def traceInfo = [:]
        def dependencies = getDependencies(project)
        traceInfo.put('deps', dependencies)
        Tracking.getInstance().trace(traceInfo)

        rule = Utils.loadJson()

        inputs.each { TransformInput input ->
            input.jarInputs.each { JarInput jarInput ->
                //Log.debug('jarinput:' + jarInput.file.absolutePath)
                def srcFile = handleJarInput(jarInput, context.temporaryDir)
                File dstFile = outputProvider.getContentLocation(
                        jarInput.file.name + "_" + DigestUtils.md5Hex(jarInput.file.absolutePath).substring(0, 8),
                        jarInput.contentTypes, jarInput.scopes, Format.JAR);
                FileUtils.copyFile(srcFile, dstFile)
            }

            input.directoryInputs.each { DirectoryInput directoryInput ->
                File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY);
                File src = handleDirectoryInput(directoryInput, context.temporaryDir)
                FileUtils.copyDirectory(src, dest);
            }
        }
    }

    private File handleJarInput(JarInput jarInput, File temporaryDir) {
        if (null == jarInput) {
            return null
        }
        if (null == jarInput.file) {
            return null
        }

        def pRule = this.findPackageRule(jarInput.file.absolutePath)
        if (null == pRule) {
            return jarInput.file
        }

        return unfoldJarAndInject(jarInput.file, temporaryDir, pRule)
    }

    static private void handleClassFile(File f, int type) {
        InputStream is = new FileInputStream(f);

        byte[] b = null
        switch (type) {
            case 1:
                b = handleApplicationClassByte(is)
                break
            default:
                b = handleClassByte(is, null)
                break
        }

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(b);
        fos.close();
    }

    static private boolean needRemove(def className, Rule.PackageRule pRule) {
        if (null == pRule) {
            return false
        }
        if (null == pRule.classRules) {
            return false
        }

        for (Rule.ClassRule cRule : pRule.classRules) {
            if (className.contains(cRule.targetClass.owner)) {
                Log.debug('need remove class:' + className)
                return true
            }
        }

        return false
    }

    static private byte[] handleClassByte(InputStream is, Rule.PackageRule pRule) {
        byte[] b = null
        try {
            ClassReader cr = new ClassReader(is);
            ClassWriter cw = new ClassWriter(0);
            ClassVisitor cv = new InjectClassVisitor(cw, pRule);
            cr.accept(cv, 0);

            b = cw.toByteArray()
        } catch (Exception e) {
            Log.error(e)
            b = null
        }

        if (null == b) {
            b = IOUtils.toByteArray(is)
        }
        return b
    }

    static private byte[] handleApplicationClassByte(InputStream is) {
        byte[] b = null
        try {
            ClassReader cr = new ClassReader(is);
            ClassWriter cw = new ClassWriter(0);
            ClassVisitor cv = new ApplicationClassVisitor(cw);
            cr.accept(cv, 0);

            b = cw.toByteArray()
        } catch (Exception e) {
            Log.error(e)
            b = null
        }

        if (null == b) {
            b = IOUtils.toByteArray(is)
        }
        return b
    }

    static private File unfoldJarAndInject(File jar, File tempDir, Rule.PackageRule pRule) {
        def jarInput = new JarFile(jar)
        def jarOutput = null

        def hexName = DigestUtils.md5Hex(jar.absolutePath).substring(0, 8);
        jarOutput = new File(tempDir, hexName + jar.name)
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(jarOutput));

        Enumeration enumeration = jarInput.entries()
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            String entryName = jarEntry.getName()
            if (needRemove(entryName, pRule)) {
                continue;
            }
            ZipEntry zipEntry = new ZipEntry(entryName);
            jarOutputStream.putNextEntry(zipEntry);

            byte[] bytes = null
            if (entryName.endsWith(".class")) {
                bytes = handleClassByte(jarInput.getInputStream(jarEntry), pRule);
            } else {
                bytes = IOUtils.toByteArray(jarInput.getInputStream(jarEntry));
            }
            jarOutputStream.write(bytes);
            jarOutputStream.closeEntry();
        }
        jarOutputStream.close();
        jarInput.close();

        return jarOutput
    }

    private File handleDirectoryInput(DirectoryInput dirInput, File tempDir) {
        if (null == dirInput) {
            return null
        }
        if (null == dirInput.file) {
            return null
        }

        if (appName) {
            String classPath = appName.replace('.', '/') + '.class'
            File classFile = new File(dirInput.file, classPath);
            if (classFile.exists()) {
                handleClassFile(classFile, 1)
            }
        }

//        activityNames.each { String activityName ->
//            String classPath = activityName.replace('.', '/') + '.class'
//            File classFile = new File(dirInput.file, classPath);
//            if (classFile.exists()) {
//                handleClassFile(classFile, 2)
//            }
//        }

        return dirInput.file
    }

    private Rule.PackageRule findPackageRule(String jarPath) {
        for (Rule.PackageRule pRule : rule.packageRule) {
            if (jarPath.contains(pRule.name)) {
                return pRule
            }
        }
        return null
    }

    public void setAppName(String name) {
        appName = name
    }

    public void setActivityNames(List<String> names) {
        activityNames = names
    }

    public static List<String> getDependencies(Project project) {
        List<String> dependencies = new ArrayList<String>()

        project.configurations.compile.resolvedConfiguration.resolvedArtifacts.each { artifact ->
            def id = artifact.moduleVersion.id
            dependencies.add("${id.group}:${id.name}:${id.version}")
        }
//        project.configurations.compile.resolvedConfiguration.files.each { file ->
//            println file
//        }

        return dependencies
    }

}
