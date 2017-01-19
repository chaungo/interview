package handle;

import models.SessionInfo;
import models.exception.APIException;
import ninja.Result;

import java.util.Set;

public interface AssigneeHandler {
    public Result getListCycleName(String projectName, String release, Set<String> products, SessionInfo sessionInfo) throws APIException;

    public Result getAssigneeList(String projectName, String release, SessionInfo sessionInfo) throws APIException;

}
