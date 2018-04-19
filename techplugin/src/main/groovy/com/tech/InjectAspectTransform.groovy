package com.tech

import com.android.build.gradle.TestedExtension
import com.tech.util.Log
import javassist.*
import javassist.expr.ExprEditor
import javassist.expr.NewExpr
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

/**
 * Created by tianyang on 16/7/28.
 */
public class InjectAspectTransform {

    Project project;

    public InjectAspectTransform(Project project) {
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


        Log.log(this, 'inject transform for tech sdk:' + inputDir.absolutePath)

        javaCompile.classpath.each {
            classes.appendClassPath(it.absolutePath)
        }


        try {
            inputDir.eachFileRecurse {
                if (!it.isDirectory() && it.absolutePath.endsWith(".class")) {
                    String path = it.absolutePath.substring(inputDir.absolutePath.length() + 1, it.absolutePath.length() - 6)
                    String clsName = path.replaceAll("/", ".");

                    CtClass ctcls = classes.getCtClass(clsName);
                    if (ctcls.isFrozen()) {
                        ctcls.defrost()
                    }


                    CtField techInject;
                    try {
                        techInject = ctcls.getDeclaredField('techInject');
                    } catch (Exception e) {
                    }

                    if (techInject != null || ctcls.isAnnotation() || ctcls.isEnum() || ctcls.isInterface() || Modifier.isAbstract(ctcls.modifiers) || Modifier.isNative(ctcls.modifiers)) {

                    } else {
                        try {

                            if (ctcls.getSuperclass().name.equals("com.alipay.mobile.framework.app.ActivityApplication")) {
                                //Log.log(this, 'tech as sdk inject class:' + clsName)
                                CtMethod onCreateMethod = ctcls.getDeclaredMethod('onCreate')

                                if (onCreateMethod == null)
                                    Log.error(this, 'class:' + clsName + ' does not have a onCreate method')
                                else {
                                    Log.log(this, 'tech as sdk inject class:' + clsName)
                                    onCreateMethod.insertAfter('com.tech.TechManager.getInstance().init( getMicroApplicationContext().getApplicationContext());')
                                }
                            }

                            CtMethod[] ms = ctcls.getDeclaredMethods()

                            List<NewExpr> exprs = new ArrayList<>()
                            for (CtMethod method : ms) {
                                if (Modifier.isNative(method.getModifiers()) || Modifier.isAbstract(method.getModifiers()))
                                    continue;
                                method.insertBefore("com.tech.TechManager.getInstance().handleMethodCutIn(com.tech.AsJoinPoint.of(\"" + clsName + "\",\"" + method.getName() + "\"));")
                                method.insertAfter("com.tech.TechManager.getInstance().handleMethodCutOut();")

                                exprs.clear()
                                method.instrument(new ExprEditor() {
                                    @Override
                                    void edit(NewExpr e) throws CannotCompileException {
                                        exprs.add(0, e)
                                    }
                                })
                                for (NewExpr expr : exprs) {
                                    //Log.log(this, 'new:' + clsName + ' method:'+expr.className+"->"+expr.lineNumber)
                                    method.insertAt(expr.lineNumber, "com.tech.TechManager.getInstance().handleConstructor(com.tech.AsJoinPoint.of(\"" + expr.className + "\",\"" + method.getName() + "\"));")
                                }
                                //Log.log(this, 'class:' + clsName + ' method:'+method.getName())
                            }

                            CtConstructor[] cms = ctcls.getDeclaredConstructors()
                            for (CtConstructor ctConstructor : cms) {
                                exprs.clear()
                                ctConstructor.instrument(new ExprEditor() {
                                    @Override
                                    void edit(NewExpr e) throws CannotCompileException {
                                        exprs.add(0, e)
                                        //ctConstructor.insertAt(e.lineNumber,"com.tech.TechManager.getInstance().handleConstructor(com.tech.AsJoinPoint.of(\""+e.className+"\",\""+ctConstructor.getName()+"\"));")
                                        //Log.log(TechASTransform.this, 'new:' + clsName + ' method:'+ctConstructor.getName()+"->"+e.lineNumber)
                                    }
                                })
                                for (NewExpr expr : exprs) {
                                    ctConstructor.insertAt(expr.lineNumber, "com.tech.TechManager.getInstance().handleConstructor(com.tech.AsJoinPoint.of(\"" + expr.className + "\",\"" + ctConstructor.getName() + "\"));")
                                }
                                //Log.log(this, 'class:' + clsName + ' method:'+ctConstructor.getName())
                                ctConstructor.insertBefore("com.tech.TechManager.getInstance().handleMethodCutIn(com.tech.AsJoinPoint.of(\"" + clsName + "\",\"" + ctConstructor.getName() + "\"));")
                                ctConstructor.insertAfter("com.tech.TechManager.getInstance().handleMethodCutOut();")
                            }

                            CtConstructor staticInit = ctcls.getClassInitializer()
                            if (staticInit != null) {
                                exprs.clear()
                                staticInit.instrument(new ExprEditor() {
                                    @Override
                                    void edit(NewExpr e) throws CannotCompileException {
                                        exprs.add(0, e)
                                        //staticInit.insertAt(e.lineNumber,"com.tech.TechManager.getInstance().handleConstructor(com.tech.AsJoinPoint.of(\""+e.className+"\",\""+staticInit.getName()+"\"));")
                                        //Log.log(TechASTransform.this, 'static:' + clsName + ' method:'+staticInit.getName()+"->"+e.lineNumber)
                                    }
                                })
                                for (NewExpr expr : exprs) {
                                    staticInit.insertAt(expr.lineNumber, "com.tech.TechManager.getInstance().handleConstructor(com.tech.AsJoinPoint.of(\"" + expr.className + "\",\"" + staticInit.getName() + "\"));")
                                }
                                //Log.log(this, 'class:' + clsName + ' method:'+staticInit.getName())
                                staticInit.insertBefore("com.tech.TechManager.getInstance().handleMethodCutIn(com.tech.AsJoinPoint.of(\"" + clsName + "\",\"" + staticInit.getName() + "\"));")
                                staticInit.insertAfter("com.tech.TechManager.getInstance().handleMethodCutOut();")

                            }

                            ctcls.addField(CtField.make("public int techInject = 0;", ctcls))

                            ctcls.writeFile(inputDir.absolutePath)
                        } catch (NotFoundException e) {
                            Log.error(this, 'class:' + clsName + 'does not have a onCreate method')
                        }
                    }


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    String toString() {
        return 'InjectAspectTransform'
    }
}
