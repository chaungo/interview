package models.main;

import java.util.HashSet;
import java.util.Set;

public class ElementGadGetData {
    private int total;
    private Set<String> issues = new HashSet<String>();
    
    public void increase(int number){
        total+=number;
    }
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Set<String> getIssues() {
        return issues;
    }

    public void setIssues(Set<String> issues) {
        this.issues = issues;
    }

}
