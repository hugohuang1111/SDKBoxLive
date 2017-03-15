package com.sdkbox.live.gradle

import com.android.build.gradle.BaseExtension
import com.sdkbox.live.gradle.manifest.ModifyXML
import com.sdkbox.live.gradle.trace.Tracking
import com.sdkbox.live.gradle.transform.SDKBoxTransform
import com.sdkbox.live.gradle.utils.Log
import org.gradle.api.Plugin
import org.gradle.api.Project

public class SDKBox implements Plugin<Project> {

    static final String PLUGIN_NAME = "SDKBox"
    SDKBoxTransform transform

    Project project
    SDKBoxExtension extension

    @Override
    void apply(Project project) {
        Log.showSDKBoxLogo()
        this.project = project

        createExtension()
        // createTasks()
        registerTransform()

        project.android.applicationVariants.all { variant ->
            variant.outputs.each { output ->
                output.processManifest.doLast {
                    Tracking.getInstance().appID = extension.appID
                    Tracking.getInstance().appSec = extension.appSec

                    def manifestOutFile = output.processManifest.manifestOutputFile

                    def xml = new ModifyXML(manifestOutFile.absolutePath)
                    xml.setAppID(extension.appID)
                    xml.setAppSec(extension.appSec)
                    xml.process()

                    transform.setAppName(xml.getAppName())
                    transform.setActivityNames(xml.getActivityNames())
                }
            }
        }
    }

    void createExtension() {
        extension = project.extensions.create(PLUGIN_NAME, SDKBoxExtension)
    }

    void createTasks() {
        project.task('modifyXML', group: 'SDKBox', description: 'reconfig class').doLast {
        }
    }

    void registerTransform() {
        BaseExtension android = project.extensions.getByType(BaseExtension)
        transform = new SDKBoxTransform(project)
        android.registerTransform(transform)
    }
}
