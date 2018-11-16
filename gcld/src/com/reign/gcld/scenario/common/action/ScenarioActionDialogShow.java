package com.reign.gcld.scenario.common.action;

import com.reign.gcld.common.*;
import com.reign.gcld.scenario.common.*;

public class ScenarioActionDialogShow extends ScenarioAction implements Cloneable
{
    private String dialog;
    
    public String getDialog() {
        return this.dialog;
    }
    
    public void setDialog(final String dialog) {
        this.dialog = dialog;
    }
    
    public ScenarioActionDialogShow(final String[] operationSingle) {
        this.dialog = operationSingle[1];
    }
    
    @Override
    public void doWork(final IDataGetter getter, final int playerId, final ScenarioEvent scenarioEvent) {
    }
    
    @Override
    public ScenarioActionDialogShow clone() throws CloneNotSupportedException {
        return (ScenarioActionDialogShow)super.clone();
    }
}
