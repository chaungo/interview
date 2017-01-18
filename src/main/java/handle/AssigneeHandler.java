package handle;

import java.util.Set;

import models.SessionInfo;
import models.exception.APIException;
import ninja.Result;

public interface AssigneeHandler {
    public Result getListCycleName(String projectName, String release, Set<String> products, SessionInfo sessionInfo) throws APIException;

    public Result getAssigneeList(String projectName, String release, SessionInfo sessionInfo) throws APIException;

}
