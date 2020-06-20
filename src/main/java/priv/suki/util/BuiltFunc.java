package priv.suki.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * ���������࣬�ṩInfoSend���õĸ��ַ���
 *
 * @author ��С��
 * @version 1.o
 */
public class BuiltFunc {

    private int num = 0;
    private Map<String, CaseObject> caseMap;


    /**
     * ��ȡ��ǰʱ��
     *
     * @return ��ǰʱ�䣬Ĭ�ϸ�ʽΪyyyy-MM-dd HH:mm:ss
     */
    public String date() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

    /**
     * ��ȡ��Ϣ��ˮ�ţ�����
     *
     * @return ��ˮ��id
     */
    public String id() {
        num++;
        return String.valueOf(num);
    }

    /**
     * ���ö�����Ϣ˳����ʾ
     *
     * @param msg ԭ��
     * @return ��������Ϣ
     */
    public String caseOrder(String msg) {
        if (caseMap == null) {
            caseMap = new HashMap<>();
        }
        if (!caseMap.containsKey(msg)) {
            String caseValue = StringUtil.getSuiltString("^(?:\\$\\{CASE)(.*)(?:})$", msg, 1);
            if (StringUtil.isBlank(caseValue)) {
                return "";
            }
            String[] caseValues = caseValue.split("\\|");
            CaseObject caseObject = new CaseObject();
            caseObject.setCaseMsg(Arrays.asList(caseValues));
            caseMap.put(msg, caseObject);
        }
        CaseObject caseObject = caseMap.get(msg);
        String buildMsg = caseObject.getCaseMsg().get(caseObject.getCaseNum() % caseObject.getCaseMsg().size());
        caseObject.updateCaseNum();
        return buildMsg;
    }

    public void setId(int num) {
        this.num = num;
    }

}
