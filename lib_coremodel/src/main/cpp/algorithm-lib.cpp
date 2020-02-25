#include <jni.h>
#include <string>
#include <map>
#include <vector>
#include<android/log.h>

#define TAG "ndk_study" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__) // 定义LOGF类型

// 获取数组的大小
# define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))

using namespace std;

//除法位运算
jfloat divson(JNIEnv *env, jobject instance,jlong a,jlong b){
//    jfloat cnt = 0;

}

// 注册native方法到java中
static int registerNativeMethods(JNIEnv *env, const char *className,
                                 JNINativeMethod *gMethods, int numMethods) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

static const char *mClassName = "com/masterxing/lib_coremodel/utils/JNIUtils";

static JNINativeMethod method[] = {
        {"divson", "(JJ)F", (jfloat *) divson}
};

int register_ndk_load(JNIEnv *env) {
    // 调用注册方法
    return registerNativeMethods(env, mClassName,
                                 method, NELEM(method));
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGD("jni_OnLoad");
    JNIEnv *env = NULL;
    jint result = -1;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return result;
    }

    jint bool1 = register_ndk_load(env);
    // 返回jni的版本
    return JNI_VERSION_1_6;
}