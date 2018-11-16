package com.reign.gcld.task.request;

import java.util.*;

public class TaskRequestProcessViewerComposite extends TaskRequestProcessViewer
{
    public static final int AND = 1;
    public static final int OR = 2;
    
    public TaskRequestProcessViewerComposite(final List<TaskRequestProcessViewer> taskRequestProcessViewerList, final int type, final boolean isShowProcess) {
        if (type == 1) {
            this.checkCompleteAnd(taskRequestProcessViewerList, isShowProcess);
        }
        else {
            this.checkCompleteOr(taskRequestProcessViewerList, isShowProcess);
        }
    }
    
    private void checkCompleteAnd(final List<TaskRequestProcessViewer> taskRequestProcessViewerList, final boolean isShowProcess) {
        this.processStr = "";
        this.completed = true;
        for (final TaskRequestProcessViewer viewer : taskRequestProcessViewerList) {
            if (!viewer.isCompleted()) {
                this.completed = false;
            }
        }
        if (!this.completed && isShowProcess) {
            this.processStr = "0/1";
        }
    }
    
    private void checkCompleteOr(final List<TaskRequestProcessViewer> taskRequestProcessViewerList, final boolean isShowProcess) {
        this.processStr = "";
        this.completed = false;
        for (final TaskRequestProcessViewer viewer : taskRequestProcessViewerList) {
            if (viewer.isCompleted()) {
                this.completed = true;
            }
        }
        if (!this.completed && isShowProcess) {
            this.processStr = "0/1";
        }
    }
    
    @Override
    public void buildProcessStr() {
    }
}
