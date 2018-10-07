/*
 * Copyright 2018 Emil Suleymanov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <jni.h>
#include <string.h>
#include <errno.h>

int throwException(JNIEnv *env, const char *className, const char *msg) {
    jclass exClass = (*env)->FindClass(env, className);
    if (exClass == NULL) return -1;

    if ((*env)->ThrowNew(env, exClass, msg) != JNI_OK) {
        return -1;
    }

    return 0;
}

int throwIoException(JNIEnv *env, const char *msg) {
    return throwException(env, "java/io/IOException", msg);
}

jclass getFileDescriptorClass(JNIEnv *env) {
    jclass fdClass;

    fdClass = (*env)->FindClass(env, "java/io/FileDescriptor");
    if (fdClass == NULL) {
        throwIoException(env, "Couldn't get java.io.FileDescriptor class!");
    }

    return fdClass;
}

jobject intToJFileDescriptor(JNIEnv *env, int fd) {
    jfieldID fieldFd;
    jmethodID methodId;
    jclass fdClass;
    jobject ret;

    fdClass = getFileDescriptorClass(env);

    if (fd < 0) {
        // open returned an error. Throw an IOException with the error string
        char buf[1024];
        sprintf(buf, "open: %s", strerror(errno));

        throwIoException(env, buf);

        return NULL;
    }

    // construct a new FileDescriptor
    methodId = (*env)->GetMethodID(env, fdClass, "<init>", "()V");
    if (methodId == NULL) return NULL;
    ret = (*env)->NewObject(env, fdClass, methodId);

    // poke the "fd" field with the file descriptor
    fieldFd = (*env)->GetFieldID(env, fdClass, "fd", "I");
    if (fieldFd == NULL) return NULL;
    (*env)->SetIntField(env, ret, fieldFd, fd);

    // and return it
    return ret;
}

int jFileDescriptorToInt(JNIEnv *env, jobject jfd) {
    jfieldID fieldFd;
    jclass fdClass;

    fdClass = getFileDescriptorClass(env);

    // poke the "fd" field with the file descriptor
    fieldFd = (*env)->GetFieldID(env, fdClass, "fd", "I");
    if (fieldFd == NULL) return -1;

    return (*env)->GetIntField(env, jfd, fieldFd);
}