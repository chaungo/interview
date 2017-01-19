package handle;

import manament.log.LoggerWapper;
import models.AssigneeVO;
import models.SessionInfo;
import models.exception.APIException;
import ninja.Result;
import ninja.Results;
import util.gadget.AssigneeUtility;

import java.util.Set;

public class AssigneeHandlerImpl implements AssigneeHandler {
    final static LoggerWapper logger = LoggerWapper.getLogger(AssigneeHandlerImpl.class);

    public AssigneeHandlerImpl() {
    }

    @Override
    public Result getListCycleName(String projectName, String release, Set<String> products, SessionInfo sessionInfo) throws APIException {
        return Results.json().render(AssigneeUtility.getInstance().getListCycleName(projectName, release, products, sessionInfo));
    }

    @Override
    public Result getAssigneeList(String projectName, String release, SessionInfo sessionInfo) throws APIException {
        Set<AssigneeVO> assignees = AssigneeUtility.getInstance().findAssigneeList(projectName, release, sessionInfo);
        return Results.json().render(assignees);
    }

}
