#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_koenidv_tbocrypto_data_Keys_coingeckoKey(JNIEnv *env, jobject thiz) {
    std::string api_key = "CG-wGcfxWiZGEcVM4qGtLHxetkS"; // commiting this to git for demonstration purposes
    return env->NewStringUTF(api_key.c_str());
}