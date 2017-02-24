package com.tech.util;

import com.android.builder.dependency.ManifestDependency;
import com.android.manifmerger.XmlDocument;
import com.android.utils.Pair;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by tianyang on 16/8/1.
 */
public class Utils {

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static String readFile(File file) {
        if (file == null || !file.exists())
            return null;
        try {
            InputStream is = new FileInputStream(file);
            BufferedReader bis = new BufferedReader(new InputStreamReader(is));

            StringBuilder builder = new StringBuilder();

            String line = null;
            while ((line = bis.readLine()) != null)
                builder.append(line);
            bis.close();
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Collect the list of libraries' manifest files.
     *
     * @param libraries declared dependencies
     * @return a list of files and names for the libraries' manifest files.
     */
    public static ImmutableList<Pair<String, File>> collectLibraries(
            List<? extends ManifestDependency> libraries) {

        ImmutableList.Builder<Pair<String, File>> manifestFiles = ImmutableList.builder();
        if (libraries != null) {
            collectLibraries(libraries, manifestFiles);
        }
        return manifestFiles.build();
    }

    /**
     * recursively calculate the list of libraries to merge the manifests files from.
     *
     * @param libraries     the dependencies
     * @param manifestFiles list of files and names identifiers for the libraries' manifest files.
     */
    private static void collectLibraries(List<? extends ManifestDependency> libraries,
                                         ImmutableList.Builder<Pair<String, File>> manifestFiles) {

        for (ManifestDependency library : libraries) {
            manifestFiles.add(Pair.of(library.getName(), library.getManifest()));
            List<? extends ManifestDependency> manifestDependencies = library
                    .getManifestDependencies();
            if (!manifestDependencies.isEmpty()) {
                collectLibraries(manifestDependencies, manifestFiles);
            }
        }
    }

    public static void save(XmlDocument xmlDocument, File out) {
        try {
            Files.write(xmlDocument.prettyPrint(), out, Charsets.UTF_8);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
