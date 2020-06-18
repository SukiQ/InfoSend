package priv.suki.inf;

import priv.suki.util.ClassUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * �ӿڲ���������
 *
 * @author ��С��
 * @version 1.0.5
 */
public class InterfaceFactory {
    public static final String CNAME = "CNAME";
    public static final String INFO_CLASS = "INFO_CLASS";
    public static final String SEND_CLASS = "SEND_CLASS";
    public static final String INF_PACKAGE_PATH = "priv.suki.inf.info";
    private Map<String, InterfaceObject> interfaceMap;
    private Map<String, Map<String, Object>> paramMap;

    /**
     * ����
     *
     * @throws Exception �����쳣
     */
    public static void load() throws Exception {
        getInstance().loadInf();
    }

    /**
     * ���ط���
     *
     * @throws Exception �����쳣
     */
    public void loadInf() throws Exception {
        Set<Class<?>> classSet = ClassUtil.getClasses(INF_PACKAGE_PATH);
        String cname = null;
        String infoClass = null;
        String sendClass = null;
        interfaceMap = new HashMap<>(classSet.size());
        paramMap = new HashMap<>(classSet.size());
        for (Class<?> inf : classSet) {
            Field[] fields = inf.getDeclaredFields();
            Map<String, Object> paramTmpMap = new HashMap<>(fields.length - 3);
            for (Field field : fields) {
                if ((Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL) == field.getModifiers()) {
                    if (CNAME.equals(field.getName())) {
                        cname = (String) field.get(null);
                    }
                    if (INFO_CLASS.equals(field.getName())) {
                        infoClass = (String) field.get(null);
                    }
                    if (SEND_CLASS.equals(field.getName())) {
                        sendClass = (String) field.get(null);
                    }
                    continue;
                }
                field.setAccessible(true);
                paramTmpMap.put(field.getName(), field.get(null));
            }


            paramMap.put(inf.getSimpleName(), paramTmpMap);
            interfaceMap.put(inf.getSimpleName(), new InterfaceObject(cname, Class.forName(infoClass), Class.forName(sendClass)));
        }

    }

    /**
     * ��ȡ�ӿڶ���
     *
     * @param infName �ӿ���
     * @return �ӿڶ���
     */
    public static InterfaceObject getInfObject(String infName) {
        return getInstance().interfaceMap.get(infName);
    }

    /**
     * ��ȡ�ӿڲ���ֵ
     *
     * @param interfaceName �ӿ���
     * @param paramName     �ӿڲ�����
     * @return ����ֵ
     */
    public static Object getParam(String interfaceName, String paramName) {
        return getInstance().paramMap.get(interfaceName).get(paramName);
    }

    /**
     * ���ص���
     *
     * @return ����
     */
    public static InterfaceFactory getInstance() {
        return Singleton.INSTANCE.getInstance();
    }


    /**
     * ����ģʽ
     */
    private enum Singleton {
        /**
         * ����
         */
        INSTANCE;

        private final InterfaceFactory instance;

        Singleton() {
            instance = new InterfaceFactory();
        }

        public InterfaceFactory getInstance() {
            return instance;
        }
    }


}
