package priv.suki.process.service;

import lombok.Data;


/**
 * @author 花小琪
 * @version 1.0.5
 */


@Data
public class SendUnit {
    /**
     * 数字
     */
    private int num;
    /**
     * 单位
     */
    private String timeType;

    public void parseUnit(String num, String timeType) {
        this.num = Integer.parseInt(num);
        this.timeType = timeType;
    }

}

