package models;

import java.util.Set;

public class JQLIssueWapper {
    private JQLIssueVO issue;
    private Set<JQLIssueVO> child;

    
    public JQLIssueWapper() {
    }

    public JQLIssueWapper(JQLIssueVO issue, Set<JQLIssueVO> child) {
        this.issue = issue;
        this.child = child;
    }

    public Set<JQLIssueVO> getChild() {
        return child;
    }

    public void setChild(Set<JQLIssueVO> child) {
        this.child = child;
    }

    public JQLIssueVO getIssue() {
        return issue;
    }

    public void setIssue(JQLIssueVO issue) {
        this.issue = issue;
    }

}
