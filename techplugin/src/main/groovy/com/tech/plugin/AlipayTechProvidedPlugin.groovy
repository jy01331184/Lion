package com.tech.plugin

import com.android.build.gradle.TestedExtension
import com.tech.InjectAspectTransform
import com.tech.util.Log
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
/**
 * 钱包 bundle 非 slink 插件
 */
public class AlipayTechProvidedPlugin implements Plugin<Project> {

    TestedExtension android;

    @Override
    void apply(Project o) {
        android = o.extensions.findByName("android")

        o.configurations.create('techsdk')

        o.dependencies.add('releaseProvided', 'com.alipay.techsdk:techsdk:' + Constants.SDK_VERSION + '@jar')

        //o.dependencies.add('techsdk', 'com.alipay.techsdk:techsdk:' + Constants.SDK_VERSION + ':AndroidManifest@xml')

        o.afterEvaluate {
            hookCompile(o)
        }

    }



    void hookCompile(Project o) {
        Log.log(this, 'alipay '+Constants.PLUGIN_VERSION+' hookCompile')
        JavaCompile javaCompile = o.tasks.findByName('compileReleaseJavaWithJavac')

        javaCompile.doLast {
            javaCompile.outputs.files.files.each {
                if(it.absolutePath.endsWith('/classes/release'))
                    new InjectAspectTransform(o).transformClasses(it,javaCompile)
            }
        }
    }

}