package util.gadget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import handle.executors.CycleTestCallable;
import handle.executors.ExecutorManagement;
import manament.log.LoggerWapper;
import models.ExecutionIssueResultWapper;
import models.exception.APIException;
import models.gadget.CycleVsTestExecution;
import models.main.GadgetData;
import util.AdminUtility;

public class CycleUtility {
    final static LoggerWapper logger = LoggerWapper.getLogger(CycleUtility.class);
    private static CycleUtility INSTANCE = new CycleUtility();

    private CycleUtility() {
    }

    public static CycleUtility getInstance() {
        return INSTANCE;
    }

    public List<GadgetData> getDataCycle(CycleVsTestExecution cycleGadget,  Map<String, String> cookies) throws APIException {
        List<GadgetData> returnData = new ArrayList<>();
        Set<String> cycles = cycleGadget.getCycles();
        String project = cycleGadget.getProjectName();
        if(cycleGadget.isSelectAllCycle()){
            cycles = AdminUtility.getInstance().getAllCycle();
        }
        List<CycleTestCallable> tasks = new ArrayList<>();
        if(cycles != null && !cycles.isEmpty()){
            for (String cycle : cycles){
                tasks.add(new CycleTestCallable(cycle, project, cookies));
            }
            List<Future<ExecutionIssueResultWapper>> taskResult = ExecutorManagement.getInstance().invokeTask(tasks);
            List<ExecutionIssueResultWapper> results = ExecutorManagement.getInstance().getResult(taskResult);
            for (ExecutionIssueResultWapper wapper : results){
                if(wapper != null && wapper.getExecutionsVO() != null){
                    GadgetData gadgetData = GadgetUtility.getInstance().convertToGadgetData(wapper.getExecutionsVO());
                    gadgetData.setKey(wapper.getIssue());
                    returnData.add(gadgetData);
                }
            }
        } else{
            logger.fastDebug("No Test Cycle in gadget %s", cycleGadget.getId());
        }
        GadgetUtility.getInstance().sortData(returnData);
        return returnData;
    }
}
