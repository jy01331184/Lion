package com.tech.plugin

import com.android.build.gradle.TestedExtension
import com.android.manifmerger.ICallback
import com.android.manifmerger.IMergerLog
import com.android.manifmerger.ManifestMerger
import com.tech.InjectAspectTransform
import com.tech.util.Log
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.compile.JavaCompile

/**
 * 钱包 bundle 插件
 */
public class AlipayTechPlugin implements Plugin<Project> {

    TestedExtension android;

    @Override
    void apply(Project o) {
        android = o.extensions.findByName("android")
        //o.extensions.create('tech', TechExt)
        o.configurations.create('techsdk')

        o.dependencies.add('releaseCompile', 'com.alipay.techsdk:techsdk:' + Constants.SDK_VERSION + '@jar')
        o.dependencies.add('releaseCompile', 'com.github.lecho:hellocharts-library:1.5.8@aar')

//        Log.log(this, 'alipay main:'+mainCompile)

        o.dependencies.add('techsdk', 'com.alipay.techsdk:techsdk:' + Constants.SDK_VERSION + ':AndroidManifest@xml')

        o.afterEvaluate {
            hookCompile(o)
            hookProcessManifest(o)
        }

    }

    void hookProcessManifest(Project project) {
        Log.log(this, 'alipay '+Constants.PLUGIN_VERSION+' hookProcessManifest')
        Task rTask = project.tasks.findByName("processReleaseManifest")

        rTask.doLast{
            java.io.File libManifest;
            project.configurations.each {
                if (it.name.equals("techsdk")) {
                    it.each {
                        libManifest = it;
                    }
                }
            }
            if(libManifest != null && libManifest.exists()){
                Log.log(this,"add merge manifest:" + libManifest)

                java.io.File manifest = it.outputs.files.files[0]

                IMergerLog iMergerLog = new IMergerLog() {
                    @Override
                    void error(IMergerLog.Severity severity, IMergerLog.FileAndLine location, String message, Object... msgParams) {
                        println(location.toString()+"--"+message)
                    }

                    @Override
                    void conflict(IMergerLog.Severity severity, IMergerLog.FileAndLine location1, IMergerLog.FileAndLine location2, String message, Object... msgParams) {
                        println(location1.toString()+"--"+location2.toString()+"::"+message)
                    }
                }
                ManifestMerger manifestMerger = new ManifestMerger(iMergerLog,new ICallback() {
                    @Override
                    int queryCodenameApiLevel(String codename) {
                        return 0
                    }
                })

                File[] fs = new File[1];
                fs[0] = libManifest;
                manifestMerger.process(it.outputs.files.files[0],manifest,fs,new HashMap<String, String>(),null)
                if(it.outputs.files.size() >= 2){
                    FileUtils.copyFile(it.outputs.files.files[0],it.outputs.files.files[1])
                }
            }
        }
    }

    void hookCompile(Project o) {
        Log.log(this, 'alipay hookCompile')
        JavaCompile javaCompile = o.tasks.findByName('compileReleaseJavaWithJavac')

        javaCompile.doLast {
            javaCompile.outputs.files.files.each {
                if(it.absolutePath.endsWith('/classes/release'))
                    new InjectAspectTransform(o).transformClasses(it,javaCompile)
            }
        }
    }

}