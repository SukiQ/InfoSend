package priv.suki.util;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CaseObject {
    private int caseNum;
    private List<String> caseMsg;

    public CaseObject() {
        caseNum = 0;
        caseMsg = new ArrayList<>();
    }

    public void updateCaseNum() {
        caseNum++;
    }

}
