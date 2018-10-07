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
#include <linux/if.h>
#include <fcntl.h>
#include <string.h>
#include <sys/ioctl.h>
#include <linux/if_tun.h>
#include <unistd.h>
#include "JniUtlis.h"

int allocateTunDevice(char *dev, int flags) {
    struct ifreq ifr;
    int fd, err;
    char *cloneDev = "/dev/net/tun";

    /* Arguments taken by the function:
     *
     * char *dev: the name of an interface (or '\0'). MUST have enough
     *   space to hold the interface name if '\0' is passed
     * int flags: interface flags (eg, IFF_TUN etc.)
     */

    /* open the clone device */
    if ((fd = open(cloneDev, O_RDWR)) < 0) {
        return fd;
    }

    /* preparation of the struct ifr, of type "struct ifreq" */
    memset(&ifr, 0, sizeof(ifr));

    ifr.ifr_flags = (short) flags;   /* IFF_TUN or IFF_TAP, plus maybe IFF_NO_PI */

    if (*dev) {
        /* if a device name was specified, put it in the structure; otherwise,
         * the kernel will try to allocate the "next" device of the
         * specified type */
        strncpy(ifr.ifr_name, dev, IFNAMSIZ);
    }

    /* try to create the device */
    if ((err = ioctl(fd, TUNSETIFF, (void *) &ifr)) < 0) {
        close(fd);
        return err;
    }

    /* if the operation was successful, write back the name of the
     * interface to the variable "dev", so the caller can know
     * it. Note that the caller MUST reserve space in *dev (see calling
     * code below) */
    strcpy(dev, ifr.ifr_name);

    /* this is the special file descriptor that the caller will use to talk
     * with the virtual interface */
    return fd;
}

JNIEXPORT jobject JNICALL
Java_sssemil_com_bridge_jni_TunUtils_allocate(JNIEnv *env, jobject obj, jstring ifName) {
    char tun_name[IFNAMSIZ];

    const char *fname = (*env)->GetStringUTFChars(env, ifName, NULL);

    strcpy(tun_name, fname);

    (*env)->ReleaseStringUTFChars(env, ifName, fname);

    int tunFd = allocateTunDevice(tun_name, IFF_TUN | IFF_NO_PI);  /* tun interface */

    if (tunFd < 0) {
        throwIoException(env, "Allocating interface!");
    }

    return intToJFileDescriptor(env, tunFd);
}