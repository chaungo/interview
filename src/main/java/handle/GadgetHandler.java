package handle;

import java.util.List;

import models.SessionInfo;
import models.exception.APIException;
import ninja.Context;
import ninja.Result;

public abstract class GadgetHandler extends Handler {

    public abstract Result insertOrUpdateGadget(String type, String data, Context context) throws APIException;

    public abstract Result getGadgets(String id) throws APIException;

    public abstract Result getDataGadget(String id, SessionInfo sessionInfo) throws APIException;

    public abstract Result getStoryInEpic(List<String> epic, SessionInfo sessionInfo) throws APIException;

    public abstract Result getProjectList(SessionInfo sessionInfo) throws APIException;

    public abstract Result deleteGadget(String id) throws APIException;
}
