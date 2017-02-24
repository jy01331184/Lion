package com.tech

import com.android.annotations.NonNull
import com.android.build.gradle.TestedExtension
import com.android.ide.common.xml.AndroidManifestParser
import com.android.io.FolderWrapper
import com.android.io.IAbstractFile
import com.android.io.StreamException
import com.android.xml.AndroidManifest
import com.android.xml.AndroidXPathFactory
import com.google.common.io.Closeables
import com.tech.util.Log
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.NotFoundException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.compile.JavaCompile
import org.xml.sax.InputSource

import javax.xml.xpath.XPath
import javax.xml.xpath.XPathExpressionException
/**
 * Created by tianyang on 16/7/28.
 * 标准项目 在application 或 mainactivity 添加 启动
 */
public class InjectCallTransform {

    Project project;

    public InjectCallTransform(Project project) {
        this.project = project;
    }

    public void transformClasses(File inputDir, JavaCompile javaCompile) {
        ClassPool classes = new ClassPool(true)
        TestedExtension android = project.extensions.findByName("android")
        classes.appendClassPath(inputDir.getAbsolutePath());
        //def androidJar = android.getSdkDirectory().absolutePath + "/platforms/" + android.getCompileSdkVersion() + "/android.jar"
        android.bootClasspath.each {
            classes.appendClassPath(it.absolutePath)
        }

        Log.log(this, 'inject transform for tech sdk:'+inputDir.absolutePath)

        javaCompile.classpath.each {
            classes.appendClassPath(it.absolutePath)
        }

        def target = findClassName()
        if(target == null || target.length() ==0)
        {
            Log.error(this, 'did not find target class to inject!')
            return
        }

        try {
            if (target != null) {
                inputDir.eachFileRecurse {
                    if (!it.isDirectory() && it.absolutePath.endsWith(".class")) {
                        String path = it.absolutePath.substring(inputDir.absolutePath.length() + 1, it.absolutePath.length() - 6)
                        String clsName = path.replaceAll("/", ".");
                        if (target.equals(clsName)) {
                            CtClass ctcls = classes.getCtClass(clsName);
                            if (ctcls.isFrozen()) {
                                ctcls.defrost()
                            }
                            try {
                                CtMethod onCreateMethod = ctcls.getDeclaredMethod('onCreate')

                                if (onCreateMethod == null)
                                    Log.error(this, 'class:' + clsName + ' does not have a onCreate method')
                                else {
                                    Log.log(this, 'tech sdk inject class:' + clsName)
                                    onCreateMethod.insertAfter('com.tech.TechManager.getInstance().init(this);')
                                    ctcls.writeFile(inputDir.absolutePath)
                                }
                            } catch (NotFoundException e) {
                                Log.error(this, 'class:' + clsName + 'does not have a onCreate method')
                            }
                            throw new Exception()
                        }
                    }
                }
            }
        } catch (Exception e) {

        }


    }

    private static String getStringValue(
            @NonNull IAbstractFile file,
            @NonNull String xPath) throws StreamException, XPathExpressionException {
        XPath xpath = AndroidXPathFactory.newXPath();

        InputStream is = null;
        try {
            is = file.getContents();
            return xpath.evaluate(xPath, new InputSource(is));
        } finally {
            try {
                Closeables.close(is, true /* swallowIOException */);
            } catch (IOException e) {
                // cannot happen
            }
        }
    }

    String findClassName() {
        Task manifestTask = project.tasks.findByName('processTechManifest')

        for (it in manifestTask.outputs.files) {
            if (it.name.endsWith('AndroidManifest.xml')) {
                IAbstractFile asFile = AndroidManifest.getManifest(new FolderWrapper(it.parentFile))
                if(asFile == null || !asFile.exists())
                    continue
                def applicationName = getStringValue(asFile, "/" + AndroidManifest.NODE_MANIFEST + "/" + AndroidManifest.NODE_APPLICATION + "/@" + AndroidXPathFactory.DEFAULT_NS_PREFIX + ":" + "name")
                if (applicationName != null && applicationName.length() > 0) {

                    return applicationName
                }

                def data = AndroidManifestParser.parse(asFile)
                if (data.launcherActivity != null) {
                    return data.launcherActivity.name
                }
            }
        }

        return null
    }


    @Override
    String toString() {
        return 'TechSdkTransform'
    }
}
