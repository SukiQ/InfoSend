package priv.suki.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 函数方法类，提供InfoSend可用的各种方法
 *
 * @author 花小琪
 * @version 1.o
 */
public class BuiltFunc {

    private int num = 0;
    private Map<String, CaseObject> caseMap;


    /**
     * 获取当前时间
     *
     * @return 当前时间，默认格式为yyyy-MM-dd HH:mm:ss
     */
    public String date() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

    /**
     * 获取消息流水号，递增
     *
     * @return 流水号id
     */
    public String id() {
        num++;
        return String.valueOf(num);
    }

    /**
     * 配置多种消息顺序显示
     *
     * @param msg 原文
     * @return 解析后消息
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
