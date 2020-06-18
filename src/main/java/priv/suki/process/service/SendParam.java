package priv.suki.process.service;


import lombok.Getter;
import lombok.Setter;

/**
 * @author 花小琪
 * @version 1.0.5
 */

@Getter
@Setter
public class SendParam {
    private SendUnit rate;
    private SendUnit duration;
    /**
     * 截取流水号的正则表达式
     */
    private String seqIdRex;

    private enum EnumSingleton {
        /**
         * 单例
         */
        SENDPARAM;

        private SendParam sendParam;

        EnumSingleton() {
            sendParam = new SendParam();
        }

        public SendParam getInstance() {
            return sendParam;
        }
    }

    /**
     * 获取单例
     *
     * @return SendParam
     */
    public static SendParam getInstance() {
        return EnumSingleton.SENDPARAM.getInstance();
    }

    public void setRate(String num, String timetype) {
        if (rate == null) {
            rate = new SendUnit();
        }
        rate.parseUnit(num, timetype);
    }

    public void setDuration(String num, String timetype) {
        if (duration == null) {
            duration = new SendUnit();
        }
        duration.parseUnit(num, timetype);
    }


}
