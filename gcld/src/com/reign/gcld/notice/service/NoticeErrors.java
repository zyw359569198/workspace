package com.reign.gcld.notice.service;

import com.reign.gcld.common.*;

public final class NoticeErrors
{
    public static final int SUCCESS = 0;
    public static final int NOTICE_EXISTED = 1;
    public static final int NOTICE_NOT_EXIST = 2;
    public static final int NOTICE_DIFF_TYPE = 3;
    public static final int PARAM_ERROR = 4;
    public static final int EXPIRE_TIME_LESS_THAN_NOW = 5;
    public static final int START_TIME_GREAT_THAN_EXPIRE_TIME = 6;
    public static final int NOTICE_NOT_BELONG_YX = 7;
    
    public static String getErrorMsg(final int errorCode) {
        switch (errorCode) {
            case 1: {
                return LocalMessages.T_NOTICE_EXISTED;
            }
            case 2: {
                return LocalMessages.T_NOTICE_NOT_EXIST;
            }
            case 3: {
                return LocalMessages.T_NOTICE_DIFF_TYPE;
            }
            case 4: {
                return LocalMessages.T_NOTICE_PARAM_ERROR;
            }
            case 5: {
                return LocalMessages.T_NOTICE_EXPIRE_TIME_LESS_THAN_NOW;
            }
            case 6: {
                return LocalMessages.T_NOTICE_START_TIME_GREAT_THAN_EXPIRE_TIME;
            }
            case 7: {
                return LocalMessages.T_NOTICE_NOT_BELONG_YX;
            }
            default: {
                return "";
            }
        }
    }
}
