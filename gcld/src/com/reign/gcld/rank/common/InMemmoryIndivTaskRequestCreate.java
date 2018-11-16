package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestCreate
{
    public static final String REQ_TUJIN = "tujin";
    public static final String REQ_DANTIAO = "dantiao";
    public static final String REQ_SHADI = "kill";
    public static final String REQ_WEIJIAO = "killnpc";
    public static final String REQ_ZHENGBING = "mubing";
    public static final String REQ_JIEBING = "hy";
    public static final String REQ_XIANGYING = "jinling";
    public static final String REQ_ZHANCHENG = "zhancheng";
    public static final String REQ_WEIJIAOHUANGJIN = "killhj";
    public static final String REQ_ASS_OCCUPY = "zhugong";
    public static final String REQ_KILL_BAR = "killmz";
    public static final String REQ_INCENE = "jisiyb";
    public static final String REQ_ROB_COPPER = "killyb";
    public static final String REQ_CRAZY_INVEST = "tznum";
    public static final String REQ_INVEST_COPPER = "tzyb";
    public static final String REQ_GOT_COUPON = "fanbeiquan";
    public static final String REQ_DEFEATN = "win";
    public static final String REQ_QIECHUOWUYI = "challenge";
    public static final String REQ_JIFEN = "score";
    public static final String REQ_ZHUZHAO = "build";
    public static final String REQ_XILIAN = "wash";
    
    public static InMemmoryIndivTaskRequest creatRequest(final String req) {
        final String[] reqs = req.split(",");
        if (reqs[0].equalsIgnoreCase("tujin")) {
            return new InMemmoryIndivTaskRequestTujin(reqs);
        }
        if (reqs[0].equalsIgnoreCase("dantiao")) {
            return new InMemmoryIndivTaskRequestDantiao(reqs);
        }
        if (reqs[0].equalsIgnoreCase("kill")) {
            return new InMemmoryIndivTaskRequestShadi(reqs);
        }
        if (reqs[0].equalsIgnoreCase("mubing")) {
            return new InMemmoryIndivTaskRequestZhengbing(reqs);
        }
        if (reqs[0].equalsIgnoreCase("killnpc")) {
            return new InMemmoryIndivTaskRequestWeijiao(reqs);
        }
        if (reqs[0].equalsIgnoreCase("hy")) {
            return new InMemmoryIndivTaskRequestJiebing(reqs);
        }
        if (reqs[0].equalsIgnoreCase("jinling")) {
            return new InMemmoryIndivTaskRequestXiangying(reqs);
        }
        if (reqs[0].equalsIgnoreCase("zhancheng")) {
            return new InMemmoryIndivTaskRequestZhancheng(reqs);
        }
        if (reqs[0].equalsIgnoreCase("killhj")) {
            return new InMemmoryIndivTaskRequestWeijiaoHuangjin(reqs);
        }
        if (reqs[0].equalsIgnoreCase("zhugong")) {
            return new InMemmoryIndivTaskRequestAssOccupy(reqs);
        }
        if (reqs[0].equalsIgnoreCase("killmz")) {
            return new InMemmoryIndivTaskRequestKillBar(reqs);
        }
        if (reqs[0].equalsIgnoreCase("jisiyb")) {
            return new InMemmoryIndivTaskRequestIncene(reqs);
        }
        if (reqs[0].equalsIgnoreCase("killyb")) {
            return new InMemmoryIndivTaskRequestRobCopper(reqs);
        }
        if (reqs[0].equalsIgnoreCase("tznum")) {
            return new InMemmoryIndivTaskRequestCrazyInvest(reqs);
        }
        if (reqs[0].equalsIgnoreCase("tzyb")) {
            return new InMemmoryIndivTaskRequestInvestCopper(reqs);
        }
        if (reqs[0].equalsIgnoreCase("fanbeiquan")) {
            return new InMemmoryIndivTaskRequestGotCoupon(reqs);
        }
        if (reqs[0].equalsIgnoreCase("win")) {
            return new InMemmoryIndivTaskRequestDefeatN(reqs);
        }
        if (reqs[0].equalsIgnoreCase("challenge")) {
            return new InMemmoryIndivTaskRequestQiechuo(reqs);
        }
        if (reqs[0].equalsIgnoreCase("score")) {
            return new InMemmoryIndivTaskRequestJifen(reqs);
        }
        if (reqs[0].equalsIgnoreCase("build")) {
            return new InMemmoryIndivTaskRequestZhuZhao(reqs);
        }
        if (reqs[0].equalsIgnoreCase("wash")) {
            return new InMemmoryIndivTaskRequestXilian(reqs);
        }
        return null;
    }
}
