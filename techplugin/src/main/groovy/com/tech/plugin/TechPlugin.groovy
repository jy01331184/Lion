package com.tech.plugin

import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.SigningConfig
import com.android.build.gradle.internal.tasks.BaseTask
import com.android.builder.core.AndroidBuilder
import com.tech.MedusaAndroidBuilder
import com.tech.InjectAspectTransform
import com.tech.InjectCallTransform
import com.tech.util.Log
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.compile.JavaCompile

/**
 * 标准gradle 插件
 */
public class TechPlugin implements Plugin<Project> {

    TestedExtension android;

    @Override
    void apply(Project o) {
        android = o.extensions.findByName("android")

        SigningConfig sign
        android.buildTypes.each {
            if (it.name.equals("debug"))
                sign = it.signingConfig
        }

        android.buildTypes {
            tech {
                debuggable true
                signingConfig sign
            }
        }

        o.configurations.create('techsdk')
        o.dependencies.add('techCompile', 'com.alipay.techsdk:techsdk:' + Constants.SDK_VERSION + '@jar')
        o.dependencies.add('techCompile', 'com.github.lecho:hellocharts-library:1.5.8@aar')
        o.dependencies.add('techsdk', 'com.alipay.techsdk:techsdk:' + Constants.SDK_VERSION + ':AndroidManifest@xml')

        o.afterEvaluate {
            hookCompile(o)
            hookProcessManifest(o)
        }

    }

    void hookProcessManifest(Project project) {

        Task rTask = project.tasks.findByName("processTechManifest")

        BaseTask.class.getDeclaredFields().find {
            it.name.equals("androidBuilder")
        }.each {
            Log.log(this, Constants.PLUGIN_VERSION+' hookProcessManifest')
            it.setAccessible(true)
            AndroidBuilder originBuilder = it.get(rTask);
            MedusaAndroidBuilder mBuilder = new MedusaAndroidBuilder(originBuilder, android, rTask, project);
            it.set(rTask, mBuilder)
        }
    }

    void hookCompile(Project o) {
        Log.log(this, Constants.PLUGIN_VERSION+' hookCompile')
        JavaCompile javaCompile = o.tasks.findByName('compileTechJavaWithJavac')

        javaCompile.doLast {
            javaCompile.outputs.files.files.each {
                if(it.absolutePath.endsWith('/classes/tech')){
                    new InjectCallTransform(o).transformClasses(it,javaCompile)
                    new InjectAspectTransform(o).transformClasses(it,javaCompile)
                }
            }
        }
    }
}