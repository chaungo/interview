package models.main;

import models.APIIssueVO;

public class GadgetData {
    private APIIssueVO key;
    private ElementGadGetData unexecuted = new ElementGadGetData();
    private ElementGadGetData failed = new ElementGadGetData();
    private ElementGadGetData wip = new ElementGadGetData();
    private ElementGadGetData blocked = new ElementGadGetData();
    private ElementGadGetData passed = new ElementGadGetData();
    private ElementGadGetData planned = new ElementGadGetData();
    private ElementGadGetData unplanned = new ElementGadGetData();

    public void increaseUnexecuted(int number) {
        unexecuted.increase(number);
    }

    public void increaseFailed(int number) {
        failed.increase(number);
    }

    public void increaseWip(int number) {
        wip.increase(number);
    }

    public void increaseBlocked(int number) {
        blocked.increase(number);
    }

    public void increasePassed(int number) {
        passed.increase(number);
    }

    public void increasePlanned(int number) {
        planned.increase(number);
    }

    public void increaseUnplanned(int number) {
        unplanned.increase(number);
    }

    public APIIssueVO getKey() {
        return key;
    }

    public void setKey(APIIssueVO key) {
        this.key = key;
    }

    public ElementGadGetData getUnexecuted() {
        return unexecuted;
    }

    public void setUnexecuted(ElementGadGetData unexecuted) {
        this.unexecuted = unexecuted;
    }

    public ElementGadGetData getFailed() {
        return failed;
    }

    public void setFailed(ElementGadGetData failed) {
        this.failed = failed;
    }

    public ElementGadGetData getWip() {
        return wip;
    }

    public void setWip(ElementGadGetData wip) {
        this.wip = wip;
    }

    public ElementGadGetData getBlocked() {
        return blocked;
    }

    public void setBlocked(ElementGadGetData blocked) {
        this.blocked = blocked;
    }

    public ElementGadGetData getPassed() {
        return passed;
    }

    public void setPassed(ElementGadGetData passed) {
        this.passed = passed;
    }

    public ElementGadGetData getPlanned() {
        return planned;
    }

    public void setPlanned(ElementGadGetData planned) {
        this.planned = planned;
    }

    public ElementGadGetData getUnplanned() {
        return unplanned;
    }

    public void setUnplanned(ElementGadGetData unplanned) {
        this.unplanned = unplanned;
    }

}
