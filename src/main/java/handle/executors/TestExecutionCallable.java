package handle.executors;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import manament.log.LoggerWapper;
import models.APIIssueVO;
import models.ExecutionIssueResultWapper;
import models.ExecutionIssueVO;
import models.JQLIssueVO;
import models.JQLIssuetypeVO.Type;
import util.gadget.EpicUtility;
import util.gadget.StoryUtility;

public class TestExecutionCallable implements Callable<ExecutionIssueResultWapper> {
    final static LoggerWapper logger = LoggerWapper.getLogger(TestExecutionCallable.class);
    private JQLIssueVO issue;
    private Type type;
    private Map<String, String> cookies;
    public TestExecutionCallable(JQLIssueVO issue, Type type, Map<String, String> cookies) {
        super();
        this.issue = issue;
        this.type = type;
        this.cookies = cookies; 
    }

    @Override
    public ExecutionIssueResultWapper call() throws Exception {
        ExecutionIssueResultWapper resultWapper = new ExecutionIssueResultWapper();
        APIIssueVO apiIssue = new APIIssueVO();
        apiIssue.setKey(issue.getKey());
        apiIssue.setSelf(issue.getSelf());
        apiIssue.setSummary(issue.getFields().getSummary());
        apiIssue.setPriority(issue.getFields().getPriority());
        apiIssue.setType(type);
        
        resultWapper.setIssue(apiIssue);
        if(Type.TEST.equals(type)){
            List<ExecutionIssueVO> executionIssues = EpicUtility.getInstance().findTestExecutionInIsuee(issue.getKey(), cookies);
            if(executionIssues != null && !executionIssues.isEmpty()){
                resultWapper.getExecutionsVO().addAll(executionIssues);
            }
        } else if(Type.STORY.equals(type)){
            List<ExecutionIssueVO> executionIssues = StoryUtility.getInstance().findAllTestExecutionInStory(issue, cookies);
            resultWapper.getPlanned().increase(issue.getFields().getCustomfield_14809());
            resultWapper.getPlanned().getIssues().add(issue.getKey());
            if(executionIssues != null && !executionIssues.isEmpty()){
                resultWapper.getExecutionsVO().addAll(executionIssues);
            }
        }
        logger.fasttrace("%s:%s:%d Test execution", issue.getKey(), type , resultWapper.getExecutionsVO().size());
        return resultWapper;
    }

}
