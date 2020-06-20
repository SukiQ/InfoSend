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
 * @author ��С��
 * @version 1.0.5
 * Class������
 */
public class ClassUtil {

    /**
     * �Ӱ�package�л�ȡ���е�Class
     *
     * @param pack ��·��
     * @return ��������
     */
    public static Set<Class<?>> getClasses(String pack) throws ClassNotFoundException, IOException {

        Set<Class<?>> classes = new LinkedHashSet<>();
        boolean recursive = true;
        String packageName = pack;
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        dirs = Thread.currentThread().getContextClassLoader().getResources(
                packageDirName);
        // ѭ��������ȥ
        while (dirs.hasMoreElements()) {

            URL url = dirs.nextElement();
            // �õ�Э�������
            String protocol = url.getProtocol();
            // ��������ļ�����ʽ�����ڷ�������
            if ("file".equals(protocol)) {
                // ��ȡ��������·��
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                // ���ļ��ķ�ʽɨ���������µ��ļ� ����ӵ�������
                findAndAddClassesInPackageByFile(packageName, filePath, classes);
            } else if ("jar".equals(protocol)) {

                JarFile jar;

                jar = ((JarURLConnection) url.openConnection())
                        .getJarFile();

                Enumeration<JarEntry> entries = jar.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    // �������/��ͷ��
                    if (name.charAt(0) == '/') {
                        // ��ȡ������ַ���
                        name = name.substring(1);
                    }
                    // ���ǰ�벿�ֺͶ���İ�����ͬ
                    if (name.startsWith(packageDirName)) {
                        int idx = name.lastIndexOf('/');
                        // �����"/"��β ��һ����
                        if (idx != -1) {
                            // ��ȡ���� ��"/"�滻��"."
                            packageName = name.substring(0, idx)
                                    .replace('/', '.');
                        }

                        // �����һ��.class�ļ� ���Ҳ���Ŀ¼
                        if (name.endsWith(".class")
                                && !entry.isDirectory()) {
                            // ȥ�������".class" ��ȡ����������
                            String className = name.substring(
                                    packageName.length() + 1, name
                                            .length() - 6);

                            // ��ӵ�classes
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
     * ���ļ�����ʽ����ȡ���µ�����Class
     *
     * @param packageName ����
     * @param packagePath ��·��
     * @param classes     ����
     */
    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, Set<Class<?>> classes) throws ClassNotFoundException {
        // ��ȡ�˰���Ŀ¼ ����һ��File
        File dir = new File(packagePath);
        // ��������ڻ��� Ҳ����Ŀ¼��ֱ�ӷ���
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        // �Զ�����˹��� �������ѭ��(������Ŀ¼) ��������.class��β���ļ�(����õ�java���ļ�)
        File[] dirfiles = dir.listFiles(file -> (file.isDirectory())
                || (file.getName().endsWith(".class")));
        // ѭ�������ļ�
        if (dirfiles == null) {
            return;
        }
        for (File file : dirfiles) {
            // �����Ŀ¼ �����ɨ��
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "."
                        + file.getName(), file.getAbsolutePath(), classes);
            } else {
                // �����java���ļ� ȥ�������.class ֻ��������
                String className = file.getName().substring(0,
                        file.getName().length() - 6);

                // ��ӵ�������ȥ
                classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));

            }
        }
    }


}
