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

#include <stdio.h>
#include <unistd.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <jni.h>
#include "JniUtlis.h"

JNIEXPORT jobject JNICALL
Java_sssemil_com_bridge_jni_UnixSocketUtils_allocate(JNIEnv *env, jobject obj, jstring sock_name) {
    struct sockaddr_un addr;
    char buf[1600];
    int fd, cl;
    ssize_t rc;

    const char *socket_path = (*env)->GetStringUTFChars(env, sock_name, NULL);

    if ((fd = socket(AF_UNIX, SOCK_STREAM, 0)) == -1) {
        throwIoException(env, "Socket error!");
    }

    memset(&addr, 0, sizeof(addr));
    addr.sun_family = AF_UNIX;
    if (*socket_path == '\0') {
        *addr.sun_path = '\0';
        strncpy(addr.sun_path + 1, socket_path + 1, sizeof(addr.sun_path) - 2);
    } else {
        strncpy(addr.sun_path, socket_path, sizeof(addr.sun_path) - 1);
        unlink(socket_path);
    }

    if (bind(fd, (struct sockaddr *) &addr, sizeof(addr)) == -1) {
        throwIoException(env, "Bind error!");
    }

    if (listen(fd, 5) == -1) {
        throwIoException(env, "Listen error!");
    }

    /*while (1) {
        if ((cl = accept(fd, NULL, NULL)) == -1) {
            perror("accept error");
            continue;
        }

        while ((rc = read(cl, buf, sizeof(buf))) > 0) {
            printf("read %u bytes: %.*s\n", rc, rc, buf);
        }
        if (rc == -1) {
            perror("read");
            exit(-1);
        } else if (rc == 0) {
            printf("EOF\n");
            close(cl);
        }
    }*/

    (*env)->ReleaseStringUTFChars(env, sock_name, socket_path);

    return intToJFileDescriptor(env, fd);
}


JNIEXPORT jobject JNICALL
Java_sssemil_com_bridge_jni_UnixSocketUtils_accept(JNIEnv *env, jobject obj, jobject jfd) {
    int fd, cl;

    fd = jFileDescriptorToInt(env, jfd);

    if ((cl = accept(fd, NULL, NULL)) == -1) {
        throwIoException(env, "Couldn't get a valid client fd!");
    }

    return intToJFileDescriptor(env, cl);
}

JNIEXPORT void JNICALL
Java_sssemil_com_bridge_jni_UnixSocketUtils_subscribe(JNIEnv *env, jobject obj, jobject jfd, jobject callback) {
    int fd, cl;

    jobject jcl;
    jboolean keepRunning;
    jclass callbackObjectClass = (*env)->GetObjectClass(env, callback);

    jmethodID acceptedMethodId = (*env)->GetMethodID(env, callbackObjectClass, "accepted",
            "(Ljava/io/FileDescriptor;)V");
    jmethodID keepRunningMethodId = (*env)->GetMethodID(env, callbackObjectClass, "keepRunning", "()Z");

    if (acceptedMethodId == 0) {
        throwIoException(env, "Couldn't get accepted callback method!");
        return;
    }

    if (keepRunningMethodId == 0) {
        throwIoException(env, "Couldn't get keepRunning callback method!");
        return;
    }

    fd = jFileDescriptorToInt(env, jfd);

    do {
        if ((cl = accept(fd, NULL, NULL)) == -1) {
            //throwIoException(env, "Couldn't get a valid client fd!");
            perror("Couldn't get a valid client fd!");
            continue;
        }

        jcl = intToJFileDescriptor(env, cl);

        (*env)->CallVoidMethod(env, callback, acceptedMethodId, jcl);

        close(cl);

        keepRunning = (*env)->CallBooleanMethod(env, callback, keepRunningMethodId, jcl);
    } while (keepRunning == JNI_TRUE);
}
