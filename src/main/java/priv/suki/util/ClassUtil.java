package priv.suki.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author 花小琪
 * @version 1.0.5
 * Class工具类
 */
public class ClassUtil {

    /**
     * 从包package中获取所有的Class
     *
     * @param pack 包路径
     * @return 类名集合
     */
    public static Set<Class<?>> getClasses(String pack) throws ClassNotFoundException, IOException {

        Set<Class<?>> classes = new LinkedHashSet<>();
        boolean recursive = true;
        String packageName = pack;
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        dirs = Thread.currentThread().getContextClassLoader().getResources(
                packageDirName);
        // 循环迭代下去
        while (dirs.hasMoreElements()) {

            URL url = dirs.nextElement();
            // 得到协议的名称
            String protocol = url.getProtocol();
            // 如果是以文件的形式保存在服务器上
            if ("file".equals(protocol)) {
                // 获取包的物理路径
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                // 以文件的方式扫描整个包下的文件 并添加到集合中
                findAndAddClassesInPackageByFile(packageName, filePath, classes);
            } else if ("jar".equals(protocol)) {

                JarFile jar;

                jar = ((JarURLConnection) url.openConnection())
                        .getJarFile();

                Enumeration<JarEntry> entries = jar.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    // 如果是以/开头的
                    if (name.charAt(0) == '/') {
                        // 获取后面的字符串
                        name = name.substring(1);
                    }
                    // 如果前半部分和定义的包名相同
                    if (name.startsWith(packageDirName)) {
                        int idx = name.lastIndexOf('/');
                        // 如果以"/"结尾 是一个包
                        if (idx != -1) {
                            // 获取包名 把"/"替换成"."
                            packageName = name.substring(0, idx)
                                    .replace('/', '.');
                        }

                        // 如果是一个.class文件 而且不是目录
                        if (name.endsWith(".class")
                                && !entry.isDirectory()) {
                            // 去掉后面的".class" 获取真正的类名
                            String className = name.substring(
                                    packageName.length() + 1, name
                                            .length() - 6);

                            // 添加到classes
                            classes.add(Class
                                    .forName(packageName + '.'
                                            + className));

                        }
                    }
                }

            }
        }


        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName 包名
     * @param packagePath 包路径
     * @param classes     类命
     */
    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, Set<Class<?>> classes) throws ClassNotFoundException {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        File[] dirfiles = dir.listFiles(file -> (file.isDirectory())
                || (file.getName().endsWith(".class")));
        // 循环所有文件
        if (dirfiles == null) {
            return;
        }
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "."
                        + file.getName(), file.getAbsolutePath(), classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0,
                        file.getName().length() - 6);

                // 添加到集合中去
                classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));

            }
        }
    }


}
