package com.tech

import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dependency.ManifestDependencyImpl
import com.android.builder.core.AndroidBuilder
import com.android.builder.dependency.ManifestDependency
import com.android.builder.sdk.TargetInfo
import com.android.manifmerger.ManifestMerger2
import com.android.utils.ILogger
import com.tech.util.Log
import org.gradle.api.Project
import org.gradle.api.Task

import java.lang.reflect.Field
/**
 * Created by tianyang on 16/7/28.
 */
public class MedusaAndroidBuilder extends AndroidBuilder {

    private TestedExtension android;
    private Task task;
    private Project project;
    private final ILogger mLogger;
    private TargetInfo mTargetInfo;

    private String mCreatedBy;

    public MedusaAndroidBuilder(AndroidBuilder androidBuilder, TestedExtension android, Task task, Project project) {
        super(v(androidBuilder, "mProjectId"), v(androidBuilder, "mCreatedBy"), v(androidBuilder, "mProcessExecutor"), v(androidBuilder, "mJavaProcessExecutor"), v(androidBuilder, "mErrorReporter"), v(androidBuilder, "mLogger"), v(androidBuilder, "mVerboseExec"));
        s(this, androidBuilder, "mSdkInfo");
        s(this, androidBuilder, "mTargetInfo");
        s(this, androidBuilder, "mBootClasspathFiltered");
        s(this, androidBuilder, "mBootClasspathAll");
        s(this, androidBuilder, "mLibraryRequests");
        s(this, androidBuilder, "mCreatedBy")
        mLogger = v(androidBuilder, "mLogger")
        mTargetInfo = v(androidBuilder, "mTargetInfo")

        this.android = android;
        this.task = task;
        this.project = project;
    }

    @Override
    void mergeManifests(File mainManifest, List<File> manifestOverlays, List<? extends ManifestDependency> libraries, String packageOverride, int versionCode, String versionName, String minSdkVersion, String targetSdkVersion, Integer maxSdkVersion, String outManifestLocation, String outAaptSafeManifestLocation, ManifestMerger2.MergeType mergeType, Map<String, Object> placeHolders, File reportFile) {
        Log.log(this, "merge manifest:"+manifestOverlays+":"+packageOverride+"-"+reportFile.absolutePath)
        project.configurations.each {
            if (it.name.equals("techsdk")) {
                it.each {
                    List<? extends  ManifestDependency> wrapList = new ArrayList<>(libraries)
                    ManifestDependencyImpl manifestDependency = new ManifestDependencyImpl(it, new ArrayList<ManifestDependencyImpl>())
                    wrapList.add(manifestDependency)
                    Log.log(this, "add merge manifest:" + it.absolutePath)
                    libraries = wrapList
                }
            }
        }
        mergeType = ManifestMerger2.MergeType.APPLICATION
        super.mergeManifests(mainManifest, manifestOverlays, libraries, packageOverride, versionCode, versionName, minSdkVersion, targetSdkVersion, maxSdkVersion, outManifestLocation, outAaptSafeManifestLocation, mergeType, placeHolders, reportFile)
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

    @Override
    String toString() {
        return getClass().name
    }
}
