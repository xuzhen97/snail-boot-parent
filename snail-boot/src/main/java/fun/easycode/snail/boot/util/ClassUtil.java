package fun.easycode.snail.boot.util;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Class工具类
 * https://keepli.gitee.io/2020/12/28/Java%E5%8F%8D%E5%B0%84%E8%8E%B7%E5%8F%96%E6%9F%90%E4%B8%AA%E6%8E%A5%E5%8F%A3%E7%9A%84%E6%89%80%E6%9C%89%E5%AE%9E%E7%8E%B0%E7%B1%BB/
 * @author xuzhen97
 */
public class ClassUtil {

    /**
     * 获取一个接口的所有实现类
     *
     * @param target
     * @return
     */
    public static List<Class<?>> getInterfaceImpls(Class<?> target) {
        List<Class<?>> subclasses = new ArrayList<>();
        try {
            // 判断class对象是否是一个接口
            if (target.isInterface()) {
                String basePackage = target.getClassLoader().getResource("").getPath();
                File[] files = new File(basePackage).listFiles();
                // 存放class路径的list
                ArrayList<String> classpaths = new ArrayList<>();
                for (File file : files) {
                    // 扫描项目编译后的所有类
                    if (file.isDirectory()) {
                        listPackages(file.getName(), classpaths);
                    }
                }
                // 获取所有类,然后判断是否是 target 接口的实现类
                for (String classpath : classpaths) {
                    Class<?> classObject = Class.forName(classpath);
                    // 判断该对象的父类是否为null
                    if (classObject.getSuperclass() == null){
                        continue;
                    }
                    Set<Class<?>> interfaces = new HashSet<>(Arrays.asList(classObject.getInterfaces()));
                    if (interfaces.contains(target)) {
                        subclasses.add(Class.forName(classObject.getName()));
                    }
                }
            } else {
                throw new RuntimeException("Class对象不是一个interface");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return subclasses;
    }

    /**
     * 获取项目编译后的所有的.class的字节码文件
     * 这么做的目的是为了让 Class.forName() 可以加载类
     *
     * @param basePackage 默认包名
     * @param classes     存放字节码文件路径的集合
     * @return
     */
    public static void listPackages(String basePackage, List<String> classes) {
        URL url = ClassUtil.class.getClassLoader()
                .getResource("./" + basePackage.replaceAll("\\.", "/"));
        File directory = new File(url.getFile());
        for (File file : directory.listFiles()) {
            // 如果是一个目录就继续往下读取(递归调用)
            if (file.isDirectory()) {
                listPackages(basePackage + "." + file.getName(), classes);
            } else {
                // 如果不是一个目录,判断是不是以.class结尾的文件,如果不是则不作处理
                String classpath = file.getName();
                if (".class".equals(classpath.substring(classpath.length() - ".class".length()))) {
                    classes.add(basePackage + "." + classpath.replaceAll(".class", ""));
                }
            }
        }
    }
}
