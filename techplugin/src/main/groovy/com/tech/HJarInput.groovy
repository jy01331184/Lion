package com.tech

import com.android.annotations.NonNull
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Status
import com.google.common.base.Joiner
import com.google.common.base.Objects
import com.google.common.collect.ImmutableSet

public class HJarInput implements JarInput {

    @NonNull
    private final Status status;
    @NonNull
    private final String name;
    @NonNull
    private final File file;
    @NonNull
    private final Set<QualifiedContent.ContentType> contentTypes;
    @NonNull
    private final Set<QualifiedContent.Scope> scopes;

    public HJarInput(
            @NonNull String name,
            @NonNull File file,
            @NonNull Status status,
            @NonNull Set<QualifiedContent.ContentType> contentTypes,
            @NonNull Set<QualifiedContent.Scope> scopes) {
        this.name = name;
        this.file = file;
        this.contentTypes = ImmutableSet.copyOf(contentTypes);
        this.scopes = ImmutableSet.copyOf(scopes);
        this.status = status;
    }

    @NonNull
    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", getName())
                .add("file", getFile())
                .add("contentTypes", Joiner.on(',').join(getContentTypes()))
                .add("scopes", Joiner.on(',').join(getScopes()))
                .add("status", status)
                .toString();
    }

    @Override
    String getName() {
        return name
    }

    @Override
    File getFile() {
        return file
    }

    @Override
    Set<QualifiedContent.ContentType> getContentTypes() {
        return contentTypes
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return scopes
    }
}