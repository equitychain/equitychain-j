package com.passport.utils;

import com.passport.annotations.EntityClaz;
import org.reflections.Reflections;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClassUtil {

    /**
     * 从包package中获取所有的Class
     *
     */
    public static List<Class<?>> getClasses(String packageName) {

        // 第一个class类的集合
        List<Class<?>> c = new ArrayList<Class<?>>();
        //反射工具包，指明扫描路径
        Reflections reflections = new Reflections(packageName);
        //获取带Handler注解的类
        Set<Class<?>> classList = reflections.getTypesAnnotatedWith(EntityClaz.class);
        for (Class classes : classList) {
            EntityClaz t = (EntityClaz) classes.getAnnotation(EntityClaz.class);
            c.add(classes);
        }
        return c;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("列族文件不存在");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        System.out.println("dirfiles:"+dirfiles);
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                continue;
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    Class c = Class.forName(packageName + '.' + className);
                    if(c.isAnnotationPresent(EntityClaz.class)) {
                        classes.add(c);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
