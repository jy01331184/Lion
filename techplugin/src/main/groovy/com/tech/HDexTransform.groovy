package com.tech

import com.android.build.api.transform.*
import com.android.build.gradle.internal.transforms.DexTransform
import com.tech.plugin.TechPlugin
import com.tech.util.Log
import org.gradle.api.logging.Logger

import java.lang.reflect.Field

public class HDexTransform extends DexTransform
{
    private DexTransform dexTransform
    private static final String TECH_SDK = 'techsdk'

    public HDexTransform(DexTransform dexTransform, Logger logger) {

        super(v(dexTransform,"dexOptions"),v(dexTransform,"debugMode"),v(dexTransform,"multiDex"),v(dexTransform,"mainDexListFile"),v(dexTransform,"intermediateFolder"),v(dexTransform,"androidBuilder"),logger);
        this.dexTransform = dexTransform;
    }

    @Override
    public void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws TransformException, IOException, InterruptedException {

        Set<QualifiedContent.ContentType> contentTypeSet = new HashSet<>()
        contentTypeSet.add(QualifiedContent.DefaultContentType.CLASSES)

        Set<QualifiedContent.Scope> scopeSet = new HashSet<>()
        scopeSet.add(QualifiedContent.Scope.EXTERNAL_LIBRARIES)


        TransformInput target

        for (TransformInput input : inputs) {
            Collection<JarInput> jars = input.jarInputs
            for (JarInput jarInput : jars) {
                if(jarInput.name.contains(TECH_SDK))
                {
                    target = input
                    break
                }
            }
        }
        if(target != null)
        {
            JarInput jarInput = new HJarInput(TechPlugin.aspectjrtStr,new File(TechPlugin.aspectjrt),Status.NOTCHANGED,contentTypeSet,scopeSet)

            List<JarInput> list = new ArrayList<>();
            list.addAll(target.jarInputs)
            list.add(jarInput)

            Field field = target.class.getDeclaredField('jarInputs')
            field.setAccessible(true)
            field.set(target,list)
        }
        Log.log(this,"dex input:"+target)
        dexTransform.transform(context,inputs,referencedInputs,outputProvider,isIncremental)
    }

    private static <T> T v(Object obj, String name) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void s(Object obj, Object origin, String name) {
        try {
            Field field = origin.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, v(origin, name));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}