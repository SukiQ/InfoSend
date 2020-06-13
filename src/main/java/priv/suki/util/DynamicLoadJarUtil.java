package priv.suki.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * @author 花小琪
 */
public class DynamicLoadJarUtil {

    /**
     * classLoader: 类加载器
     */
    private DynamicClassLoader classLoader = null;

    /**
     * urls: 类加载路径
     */
    private final List<URL> urls = new ArrayList<>();

    public void addPath(String path) {
        try {

            classLoader.addURL(new File(path).toURI().toURL());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    public void addurl(URL urls) {
        classLoader.addURL(urls);
    }

    public Class<?> getClass(String dir, String className, ClassLoader parent) {

        this.loadClass(dir, parent);
        return this.getClassByName(className);
    }


    public Class<?> getClassByName(String className) {
        if (className == null) {
            return null;
        }
        Class<?> c = null;
        try {
            c = classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
//				log.error("getClassByName:"+e.getMessage());
        }
        return c;

    }


    private void loadClass(String dirPath, ClassLoader parent) {
        File dir = new File(dirPath);

        // 过滤返回jar包文件

        File[] files = dir.listFiles((dir1, name) -> name.endsWith(".jar"));
        if (files == null || files.length == 0) {
            try {// 无jar包，根目录作为class目录
                URL url = dir.toURI().toURL();
                urls.add(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            // 目录中的jar包增加到URL列表中
            for (File file : files) {
                try {
                    URL url = file.toURI().toURL();
                    urls.add(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }

        if (parent == null) {
            classLoader = new DynamicClassLoader(urls.toArray(new URL[0]));
        } else {
            classLoader = new DynamicClassLoader(urls.toArray(new URL[0]), parent);
        }

    }
}
