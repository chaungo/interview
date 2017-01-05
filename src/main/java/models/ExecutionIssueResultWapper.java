package models;

import java.util.ArrayList;
import java.util.List;

import models.main.ElementGadGetData;

public class ExecutionIssueResultWapper {
    private volatile List<ExecutionIssueVO> executionsVO = new ArrayList<>();
    private ElementGadGetData planned = new ElementGadGetData();
    private APIIssueVO issue;

    public void increasePland(int number) {
        planned.increase(number);
    }

    public APIIssueVO getIssue() {
        return issue;
    }

    public void setIssue(APIIssueVO issue) {
        this.issue = issue;
    }

    public List<ExecutionIssueVO> getExecutionsVO() {
        return executionsVO;
    }

    public void setExecutionsVO(List<ExecutionIssueVO> executionsVO) {
        this.executionsVO = executionsVO;
    }

    public ElementGadGetData getPlanned() {
        return planned;
    }

    public void setPlanned(ElementGadGetData planned) {
        this.planned = planned;
    }

}
