package priv.suki.process.service;

import lombok.Data;


/**
 * @author ��С��
 * @version 1.0.5
 */


@Data
public class SendUnit {
    /**
     * ����
     */
    private int num;
    /**
     * ��λ
     */
    private String timeType;

    public void parseUnit(String num, String timeType) {
        this.num = Integer.parseInt(num);
        this.timeType = timeType;
    }

}

