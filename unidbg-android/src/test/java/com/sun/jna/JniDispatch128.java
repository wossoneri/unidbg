package com.sun.jna;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.LibraryResolver;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidARMEmulator;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.github.unidbg.memory.Memory;

import java.io.File;
import java.io.IOException;

public class JniDispatch128 extends AbstractJni {

    private static LibraryResolver createLibraryResolver() {
        return new AndroidResolver(23);
    }

    private static AndroidEmulator createARMEmulator() {
        return new AndroidARMEmulator("com.sun.jna");
    }

    private final AndroidEmulator emulator;
    private final Module module;
    private final VM vm;

    private final DvmClass Native;

    private JniDispatch128() {
        emulator = createARMEmulator();
        final Memory memory = emulator.getMemory();
        memory.setLibraryResolver(createLibraryResolver());

        vm = emulator.createDalvikVM(null);
        vm.setJni(this);
        vm.setVerbose(true);

        // 自行修改文件路径
        DalvikModule dm = vm.loadLibrary(new File("/Users/admin/Gitlibs/unidbg/unidbg-android/src/test/resources/dylib/libcms.so"), false);
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();

        Native = vm.resolveClass("com/ss/sys/ces/a");
    }

    private void destroy() throws IOException {
        emulator.close();
        System.out.println("destroy");
    }

    public static void main(String[] args) throws Exception {

        JniDispatch128 test = new JniDispatch128();

        test.test();

        test.destroy();
    }

    public static String xuzi1(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        char[] charArray = "0123456789abcdef".toCharArray();
        char[] cArr = new char[(bArr.length * 2)];
        for (int i = 0; i < bArr.length; i++) {
            int b2 = bArr[i] & 255;
            int i2 = i * 2;
            cArr[i2] = charArray[b2 >>> 4];
            cArr[i2 + 1] = charArray[b2 & 15];
        }
        return new String(cArr);
    }


    private void test() {
        String methodSign = "leviathan(II[B)[B";


        byte[] data = "7fd7711322876f5c25fa07e58500dbf300000000000000000000000000000000f2587820ae9015f93552d6ffa986dc32d3bb753f20bef8cb5c8c0ca5f131da85".getBytes();
        int time = (int) (System.currentTimeMillis() / 1000);

        Native.callStaticJniMethod(emulator, methodSign, -1, time, new ByteArray(vm, data));

        Object ret = Native.callStaticJniMethodObject(emulator, methodSign, -1, time, new ByteArray(vm, data));

        System.out.println("callObject执行结果:" + ((DvmObject) ret).getValue());

        byte[] tt = (byte[]) ((DvmObject) ret).getValue();
        System.out.println(new String(tt));
        String s = xuzi1(tt);
        System.out.println(s);
    }
}