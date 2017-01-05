package handle;

import java.util.Set;

import models.SessionInfo;
import models.exception.APIException;
import models.main.Release;
import ninja.Result;

public interface AssigneeHandler {
    public Result getListCycleName(String projectName, Release release,  Set<String> products, SessionInfo sessionInfo) throws APIException;
	public Result getAssigneeList(String projectName, Release release, SessionInfo sessionInfo) throws APIException;
	
}
