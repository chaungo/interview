package handle;

import models.SessionInfo;
import models.exception.APIException;
import ninja.Result;

import java.util.List;

public abstract class EpicHandler extends Handler {
    public abstract Result getEpicLinks(String project, String release, List<String> products, SessionInfo sessionInfo) throws APIException;
}
