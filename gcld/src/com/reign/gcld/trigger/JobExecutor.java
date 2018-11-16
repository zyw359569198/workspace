package com.reign.gcld.trigger;

import com.reign.gcld.common.log.*;
import com.reign.gcld.log.*;
import java.util.concurrent.*;
import org.springframework.context.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import java.lang.reflect.*;
import java.util.*;
import org.quartz.*;

public class JobExecutor implements Job
{
    private static final Logger log;
    private static final Logger dayReportLogger;
    private static Map<String, Method> cacheMap;
    
    static {
        log = CommonLog.getLog(JobExecutor.class);
        dayReportLogger = new DayReportLogger();
        JobExecutor.cacheMap = new ConcurrentHashMap<String, Method>();
    }
    
    @Override
	public void execute(final JobExecutionContext je) throws JobExecutionException {
        try {
            final ApplicationContext ctx = (ApplicationContext)je.getScheduler().getContext().get("application_context");
            final MyJob job = (MyJob)je.getTrigger().getJobDataMap().get("JOB_INFO");
            final Object obj = ctx.getBean(job.className);
            final String key = this.getKey(job.className, job.methodName);
            Method method = JobExecutor.cacheMap.get(key);
            if (method == null) {
                synchronized (JobExecutor.cacheMap) {
                    if (JobExecutor.cacheMap.get(key) == null) {
                        method = obj.getClass().getMethod(job.methodName, Constants.EMPTY_CLASS_ARRAY_0);
                        JobExecutor.cacheMap.put(key, method);
                    }
                }
                // monitorexit(JobExecutor.cacheMap)
            }
            method.invoke(obj, new Object[0]);
            for (final String log : ThreadLocalFactory.getTreadLocalLogs()) {
                JobExecutor.dayReportLogger.info(log);
            }
        }
        catch (SecurityException e) {
            JobExecutor.log.error("", e);
        }
        catch (NoSuchMethodException e2) {
            JobExecutor.log.error("", e2);
        }
        catch (IllegalArgumentException e3) {
            JobExecutor.log.error("", e3);
        }
        catch (IllegalAccessException e4) {
            JobExecutor.log.error("", e4);
        }
        catch (InvocationTargetException e5) {
            JobExecutor.log.error("", e5.getTargetException());
        }
        catch (SchedulerException e6) {
            JobExecutor.log.error("", e6);
        }
        catch (Exception e7) {
            JobExecutor.log.error("", e7);
        }
        finally {
            ThreadLocalFactory.clearTreadLocalLog();
            ThreadLocalFactory.getTreadLocalLog();
        }
        ThreadLocalFactory.clearTreadLocalLog();
        ThreadLocalFactory.getTreadLocalLog();
    }
    
    private String getKey(final String className, final String methodName) {
        return String.valueOf(className) + "_" + methodName;
    }
}
