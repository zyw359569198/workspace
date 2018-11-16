package com.reign.plugin.yx.util.kingnet.udplog;

public class StatConstants
{
    public static short TABLE_ID_LOGIN;
    public static short TABLE_ID_PAY;
    public static short TABLE_ID_PROPS;
    public static short TABLE_ID_ACT;
    public static short TABLE_ID_REFER;
    public static short TABLE_ID_GUIDE;
    public static short TABLE_ID_INVENTORY;
    public static short TABLE_ID_CURRENCY;
    public static short TABLE_ID_SER;
    public static short TABLE_ID_LOGIN_TEST;
    public static short TABLE_ID_PAY_TEST;
    public static short TABLE_ID_PROPS_TEST;
    public static short TABLE_ID_ACT_TEST;
    public static short TABLE_ID_REFER_TEST;
    public static short TABLE_ID_GUIDE_TEST;
    public static short TABLE_ID_INVENTORY_TEST;
    public static short TABLE_ID_CURRENCY_TEST;
    public static short TABLE_ID_SER_TEST;
    public static int LOG_NULL;
    
    static {
        StatConstants.TABLE_ID_LOGIN = 8;
        StatConstants.TABLE_ID_PAY = 9;
        StatConstants.TABLE_ID_PROPS = 10;
        StatConstants.TABLE_ID_ACT = 11;
        StatConstants.TABLE_ID_REFER = 12;
        StatConstants.TABLE_ID_GUIDE = 31;
        StatConstants.TABLE_ID_INVENTORY = 32;
        StatConstants.TABLE_ID_CURRENCY = 33;
        StatConstants.TABLE_ID_SER = 34;
        StatConstants.TABLE_ID_LOGIN_TEST = 37;
        StatConstants.TABLE_ID_PAY_TEST = 38;
        StatConstants.TABLE_ID_PROPS_TEST = 39;
        StatConstants.TABLE_ID_ACT_TEST = 40;
        StatConstants.TABLE_ID_REFER_TEST = 41;
        StatConstants.TABLE_ID_GUIDE_TEST = 42;
        StatConstants.TABLE_ID_INVENTORY_TEST = 43;
        StatConstants.TABLE_ID_CURRENCY_TEST = 44;
        StatConstants.TABLE_ID_SER_TEST = 48;
        StatConstants.LOG_NULL = -1;
    }
    
    public enum CURRENCY_OPERATIONS
    {
        ADD("ADD", 0, "add"), 
        SUB("SUB", 1, "sub");
        
        private final String text;
        
        private CURRENCY_OPERATIONS(final String s, final int n, final String text) {
            this.text = text;
        }
        
        @Override
        public String toString() {
            return this.text;
        }
    }
    
    public enum LOG_TYPE
    {
        LOGIN("LOGIN", 0, StatConstants.TABLE_ID_LOGIN), 
        PAY("PAY", 1, StatConstants.TABLE_ID_PAY), 
        PROPS("PROPS", 2, StatConstants.TABLE_ID_PROPS), 
        ACT("ACT", 3, StatConstants.TABLE_ID_ACT), 
        REFER("REFER", 4, StatConstants.TABLE_ID_REFER), 
        GUIDE("GUIDE", 5, StatConstants.TABLE_ID_GUIDE), 
        INVENTORY("INVENTORY", 6, StatConstants.TABLE_ID_INVENTORY), 
        CURRENCY("CURRENCY", 7, StatConstants.TABLE_ID_CURRENCY), 
        SER("SER", 8, StatConstants.TABLE_ID_SER), 
        LOGIN_TEST("LOGIN_TEST", 9, StatConstants.TABLE_ID_LOGIN_TEST), 
        PAY_TEST("PAY_TEST", 10, StatConstants.TABLE_ID_PAY_TEST), 
        PROPS_TEST("PROPS_TEST", 11, StatConstants.TABLE_ID_PROPS_TEST), 
        ACT_TEST("ACT_TEST", 12, StatConstants.TABLE_ID_ACT_TEST), 
        REFER_TEST("REFER_TEST", 13, StatConstants.TABLE_ID_REFER_TEST), 
        GUIDE_TEST("GUIDE_TEST", 14, StatConstants.TABLE_ID_GUIDE_TEST), 
        INVENTORY_TEST("INVENTORY_TEST", 15, StatConstants.TABLE_ID_INVENTORY_TEST), 
        CURRENCY_TEST("CURRENCY_TEST", 16, StatConstants.TABLE_ID_CURRENCY_TEST), 
        SER_TEST("SER_TEST", 17, StatConstants.TABLE_ID_SER_TEST);
        
        private final short id;
        
        private LOG_TYPE(final String s, final int n, final short id) {
            this.id = id;
        }
        
        public short getValue() {
            return this.id;
        }
    }
    
    public enum PAY_TYPE
    {
        PAY("PAY", 0, "pay"), 
        PROPS("PROPS", 1, "props");
        
        private final String text;
        
        private PAY_TYPE(final String s, final int n, final String text) {
            this.text = text;
        }
        
        @Override
        public String toString() {
            return this.text;
        }
    }
    
    public enum RMB_ITEM_CATEGORY
    {
        ONCE("ONCE", 0, 1), 
        PERIOD("PERIOD", 1, 2), 
        FOREVER("FOREVER", 2, 3);
        
        private final int id;
        
        private RMB_ITEM_CATEGORY(final String s, final int n, final short id) {
            this.id = id;
        }
        
        private RMB_ITEM_CATEGORY(final String s, final int n, final int id) {
            this.id = id;
        }
        
        public int getValue() {
            return this.id;
        }
    }
    
    public enum RMB_ITEM_OPERATIONS
    {
        ADD("ADD", 0, "add"), 
        SUB("SUB", 1, "sub");
        
        private final String text;
        
        private RMB_ITEM_OPERATIONS(final String s, final int n, final String text) {
            this.text = text;
        }
        
        @Override
        public String toString() {
            return this.text;
        }
    }
}
