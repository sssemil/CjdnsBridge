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

#ifndef JNIUNIXUTILS_JNIUTILS_H
#define JNIUNIXUTILS_JNIUTILS_H

#include <jni.h>

int throwException(JNIEnv *env, const char *className, const char *msg);

int throwIoException(JNIEnv *env, const char *msg);

jclass getFileDescriptorClass(JNIEnv *env);

jobject intToJFileDescriptor(JNIEnv *env, int fd);

int jFileDescriptorToInt(JNIEnv *env, jobject jfd);

#endif //JNIUNIXUTILS_JNIUTILS_H