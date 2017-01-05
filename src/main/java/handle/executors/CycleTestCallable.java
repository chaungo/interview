package handle.executors;

import java.util.Map;
import java.util.concurrent.Callable;

import models.APIIssueVO;
import models.ExecutionIssueResultWapper;
import models.main.ExecutionsVO;
import util.gadget.AssigneeUtility;

public class CycleTestCallable implements Callable<ExecutionIssueResultWapper> {
    private String cycle;
    private String project;
    private Map<String, String> cookies;
    public CycleTestCallable(String cycle, String project, Map<String, String> cookies) {
        this.cycle = cycle;
        this.project = project;
        this.cookies = cookies;
    }

    @Override
    public ExecutionIssueResultWapper call() throws Exception {
        ExecutionIssueResultWapper wapper = new ExecutionIssueResultWapper();
        ExecutionsVO executionsVO = AssigneeUtility.getInstance().findExecution(project, cycle, null, cookies);
        if(executionsVO != null){
            wapper.setExecutionsVO(executionsVO.getExecutions());
        }
        wapper.setIssue(new APIIssueVO(cycle, null));
        return wapper;
        
    }

}
