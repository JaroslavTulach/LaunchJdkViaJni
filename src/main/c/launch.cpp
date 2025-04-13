#include <jni.h>       /* where everything is defined */
#include <cstdlib>

int main(int argc, char** argv) {
    if (argc != 2) {
       printf("Usage: %s <name_of_class_to_load>\n", argv[0]);
       exit(1);
    }

    JavaVM *jvm;       /* denotes a Java VM */
    JNIEnv *env;       /* pointer to native method interface */
    JavaVMInitArgs vm_args; /* JDK/JRE 19 VM initialization arguments */
    vm_args.version = JNI_VERSION_1_1;
    vm_args.nOptions = 333;
    printf("vesion before %d\n", vm_args.version);
    int res = JNI_GetDefaultJavaVMInitArgs(&vm_args);
    printf("result %d\n", res);
    printf("vesion after %d\n", vm_args.version);
    printf("  opts after %d\n", vm_args.nOptions);

    JavaVMOption opt;
    JavaVMOption* options = &opt;
    options[0].optionString = "-Djava.class.path=/home/devel/NetBeansProjects/enso/LaunchJdkViaJni/";
    vm_args.nOptions = 1;
    vm_args.options = options;
    vm_args.ignoreUnrecognized = false;
    /* load and initialize a Java VM, return a JNI interface
     * pointer in env */
    printf("before create\n");
    JNI_CreateJavaVM(&jvm, (void**)&env, &vm_args);
    printf("after create\n");
    /* invoke the Main.test method using the JNI */
    printf("Loading %s\n", argv[1]);
    jclass cls = env->FindClass(argv[1]);
    printf("load class: %ld\n", (long)cls);
    if (!cls) {
        printf("Class not found\n");
        exit(1);
    }
    jmethodID mid = env->GetStaticMethodID(cls, "test", "(I)V");
    printf("found method test %ld\n", (long)mid);
    if (mid != 0) {
        env->CallStaticVoidMethod(cls, mid, 100);
    }
   /* We are done. */
    jvm->DestroyJavaVM();
    printf("destroyed");

    return 0;
}
