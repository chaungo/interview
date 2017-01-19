package util.gadget;

import handle.executors.CycleTestCallable;
import handle.executors.ExecutorManagement;
import manament.log.LoggerWapper;
import models.ExecutionIssueResultWapper;
import models.SessionInfo;
import models.exception.APIException;
import models.gadget.CycleVsTestExecution;
import models.main.GadgetData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

public class CycleUtility {
    final static LoggerWapper logger = LoggerWapper.getLogger(CycleUtility.class);
    private static CycleUtility INSTANCE = new CycleUtility();

    private CycleUtility() {
    }

    public static CycleUtility getInstance() {
        return INSTANCE;
    }

    public List<GadgetData> getDataCycle(CycleVsTestExecution cycleGadget, SessionInfo sessionInfo) throws APIException {
        List<GadgetData> returnData = new ArrayList<>();
        Set<String> cycles = cycleGadget.getCycles();
        String project = cycleGadget.getProjectName();
        if (cycleGadget.isSelectAllCycle()) {
            cycles = AssigneeUtility.getInstance().getListCycleName(project, cycleGadget.getRelease(), cycleGadget.getProducts(), sessionInfo);
        }
        List<CycleTestCallable> tasks = new ArrayList<>();
        if (cycles != null && !cycles.isEmpty()) {
            for (String cycle : cycles) {
                tasks.add(new CycleTestCallable(cycle, project, sessionInfo.getCookies()));
            }
            List<Future<ExecutionIssueResultWapper>> taskResult = ExecutorManagement.getInstance().invokeTask(tasks);
            List<ExecutionIssueResultWapper> results = ExecutorManagement.getInstance().getResult(taskResult);
            for (ExecutionIssueResultWapper wapper : results) {
                if (wapper != null && wapper.getExecutionsVO() != null) {
                    GadgetData gadgetData = GadgetUtility.getInstance().convertToGadgetData(wapper.getExecutionsVO());
                    gadgetData.setKey(wapper.getIssue());
                    returnData.add(gadgetData);
                }
            }
        } else {
            logger.fastDebug("No Test Cycle in gadget %s", cycleGadget.getId());
        }
        GadgetUtility.getInstance().sortData(returnData);
        return returnData;
    }
}
