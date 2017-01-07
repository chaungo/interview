package handle.executors;

import models.JQLIssueVO;
import util.gadget.GadgetUtility;

import java.util.Map;
import java.util.concurrent.Callable;

public class FindIssueCallable implements Callable<JQLIssueVO> {
    private String issueKey;
    private Map<String, String> cookies;

    public FindIssueCallable(String key, Map<String, String> cookies) {
        issueKey = key;
        this.cookies = cookies;
    }

    @Override
    public JQLIssueVO call() throws Exception {
        return GadgetUtility.getInstance().findIssue(issueKey, cookies);
    }

}
