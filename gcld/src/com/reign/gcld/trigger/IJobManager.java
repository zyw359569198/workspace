package com.reign.gcld.trigger;

import java.util.*;
import org.quartz.*;
import java.text.*;

public interface IJobManager
{
    void initJobTrigger(final List<MyJob> p0) throws SchedulerException, ParseException;
}
