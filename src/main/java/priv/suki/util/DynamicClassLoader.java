package priv.suki.util;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * This is a basic URL classloader for loading new resources dynamically.
 * <p>
 * It allows public access to the addURL() method.
 * <p>
 * It also adds a convenience method to update the current thread classloader
 */
public class DynamicClassLoader extends URLClassLoader {

    /**
     * @param urls - list of URLs to add to the thread's classloader
     */
    public static void updateLoader(URL[] urls) {
        DynamicClassLoader loader = (DynamicClassLoader) Thread.currentThread()
                .getContextClassLoader();
        for (int i = 0; i < urls.length; i++) {
            loader.addURL(urls[i]);
        }
    }

    public DynamicClassLoader(URL[] urls) {

        super(urls);

    }

    public DynamicClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public DynamicClassLoader(URL[] urls, ClassLoader parent,
                              URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    // Make the addURL method visible
    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

}
