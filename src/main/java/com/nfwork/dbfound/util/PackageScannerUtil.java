package com.nfwork.dbfound.util;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PackageScannerUtil {

    private static void findClassesByFile(String packageName,String pkgPath, Set<String> classes) {
        File dir = new File(pkgPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File packageDir = new File(pkgPath);
        File[] files = packageDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findClassesByFile(packageName + "." + file.getName(),pkgPath + "/" + file.getName(), classes);
                } else if (file.isFile()) {
                    String name = packageName + "." + file.getName();
                    if(name.endsWith(".class")) {
                        classes.add(name.substring(0, name.length() - 6));
                    }
                }
            }
        }
    }

    private static void findClassesByJar(String pkgName, JarFile jar, Set<String> classes) {
        String pkgDir = pkgName.replace(".", "/");

        Enumeration<JarEntry> entry = jar.entries();

        JarEntry jarEntry;
        String name, className;
        while (entry.hasMoreElements()) {
            jarEntry = entry.nextElement();
            name = jarEntry.getName();
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }
            if (jarEntry.isDirectory() || !name.startsWith(pkgDir) || !name.endsWith(".class")) {
                continue;
            }
            className = name.substring(0, name.length() - 6);
            classes.add(className);
        }
    }

    public static Set<String> getClassPaths(String pkg) {
        try {
            Set<String> classes = new LinkedHashSet<>();
            if(DataUtil.isNull(pkg)){
                return classes;
            }
            String pkgDirName = pkg.replace('.', '/');

            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(pkgDirName);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {// 如果是以文件的形式保存在服务器上
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");// 获取包的物理路径
                    findClassesByFile(pkg, filePath, classes);
                } else if ("jar".equals(protocol)) {// 如果是jar包文件
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    findClassesByJar(pkg, jar, classes);
                }
            }
            return classes;
        }catch (IOException exception){
            throw new DBFoundRuntimeException(exception);
        }
    }
}
