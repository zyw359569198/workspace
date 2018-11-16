package com.reign.plugin.yx.common.validation;

public class YxPingAnValidation extends YxValidation
{
    private String url;
    private final String pattern = "http://(\\w+.)+.(com)|(cn)/";
    
    public YxPingAnValidation(final String value, final String url) {
        super(value);
        this.url = url;
    }
    
    @Override
    protected StringBuffer appendKey(final StringBuffer sb, final String key) {
        final StringBuffer result = new StringBuffer();
        result.append(key).append(sb);
        return result;
    }
    
    @Override
    protected StringBuffer getComposedSrcSB() {
        final StringBuffer result = new StringBuffer();
        String value = this.url.replace("/", "");
        value = value.replace("?", "");
        value = value.replace(".", "");
        result.append(value).append(super.getComposedSrcSB());
        return result;
    }
    
    public static void main(final String[] args) {
        String value = "http://game-gateway-wlt-sanbox.stg2.24money.com/GameGateway/v0_1/requestCreateFlow.do";
        final String pattern = "http://(\\w+.)+.(com)|(cn)/";
        value = value.replaceAll(pattern, "");
        value = value.replace("/", "");
        value = value.replace("?", "");
        value = value.replace(".", "");
        System.out.println(value);
        final ValidationEnum ve = ValidationEnum.valueOf("SIGN");
        System.out.println(ve.getName());
        String values = "2012-01-22 22:00:00";
        values = values.replaceAll("\\s", "");
        System.out.println(values);
    }
}
