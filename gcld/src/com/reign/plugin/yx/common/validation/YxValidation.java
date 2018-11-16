package com.reign.plugin.yx.common.validation;

import com.reign.plugin.yx.common.*;
import com.reign.util.log.*;
import org.apache.commons.lang.*;
import com.reign.framework.netty.util.*;
import com.reign.util.codec.*;
import com.reign.plugin.yx.action.*;
import java.util.*;

public class YxValidation
{
    private String streamValue;
    private String charset;
    private String signValue;
    private Map<String, MultiResult> needCheckMap;
    static ErrorLogger log;
    List<YxSubValidation> list;
    public static final int VALIDATION_TYPE_MD5 = 0;
    public static final int VALIDATION_TYPE_SHA1 = 1;
    public static final MultiResult sucResult;
    public static final MultiResult excResult;
    
    static {
        YxValidation.log = new ErrorLogger();
        sucResult = new MultiResult(true, "1", "success");
        excResult = new MultiResult(false, "6", "exception");
    }
    
    public YxValidation(final String value) {
        this.charset = "utf-8";
        this.needCheckMap = null;
        this.streamValue = value;
        this.list = new ArrayList<YxSubValidation>();
        this.formSubValidations(this.streamValue);
    }
    
    private void formSubValidations(final String content) {
        if (StringUtils.isBlank(content)) {
            return;
        }
        final String str = content.trim();
        final String[] strs = str.split("&");
        YxSubValidation sub = null;
        String[] array;
        for (int length = (array = strs).length, i = 0; i < length; ++i) {
            final String value = array[i];
            try {
                final String[] values = value.split("=", 2);
                final String k = Utils.decode(values[0], this.charset);
                sub = new YxSubValidation();
                if (values.length == 1) {
                    sub.name = k;
                    sub.value = null;
                    final ValidationEnum vEnum = ValidationEnum.getEnumByName(k);
                    if (vEnum != null) {
                        sub.errorCode = vEnum.getErrorCode();
                        sub.errorString = vEnum.getErrorString();
                    }
                }
                else {
                    String v = Utils.decode(values[1], this.charset);
                    v = v.trim();
                    sub.name = k;
                    sub.value = v;
                    final ValidationEnum vEnum2 = ValidationEnum.getEnumByName(k);
                    if (vEnum2 != null) {
                        sub.errorCode = vEnum2.getErrorCode();
                        sub.errorString = vEnum2.getErrorString();
                    }
                }
                this.list.add(sub);
            }
            catch (Exception e) {
                YxValidation.log.error(this, e);
            }
        }
        Collections.sort(this.list);
    }
    
    public MultiResult validateParameter() {
        MultiResult result = null;
        MultiResult check = null;
        for (final YxSubValidation validate : this.list) {
            if (this.needCheckMap != null && !this.needCheckMap.isEmpty()) {
                if (!this.needCheckMap.containsKey(validate.name)) {
                    continue;
                }
                check = this.needCheckMap.get(validate.name);
                this.needCheckMap.remove(validate.name);
            }
            result = validate.validate(check);
            final Boolean flag = (Boolean)result.result1;
            if (!flag) {
                return result;
            }
        }
        if (this.needCheckMap != null && !this.needCheckMap.isEmpty()) {
            final Iterator<String> iterator2 = this.needCheckMap.keySet().iterator();
            if (iterator2.hasNext()) {
                final String key = iterator2.next();
                final ValidationEnum ve = ValidationEnum.getEnumByName(key);
                final MultiResult tmp = new MultiResult();
                tmp.result1 = false;
                tmp.result2 = ve.getErrorCode();
                tmp.result3 = ve.getErrorString();
                return tmp;
            }
        }
        return YxValidation.sucResult;
    }
    
    public boolean validateSign(final int validateType, final String key) {
        StringBuffer sb = this.getComposedSrcSB();
        sb = this.appendKey(sb, key);
        String sign = "";
        if (validateType == 0) {
            sign = CodecUtil.md5(sb.toString());
        }
        else {
            sign = CodecUtil.sha1(sb.toString());
        }
        final String checkSign = this.getValueByName(ValidationEnum.SIGN.getName());
        YxPingAnOperationAction.opReport.error("checkSign:" + checkSign + " sign:" + sign + " sb:" + sb.toString());
        return checkSign.equalsIgnoreCase(sign);
    }
    
    protected StringBuffer appendKey(final StringBuffer sb, final String key) {
        sb.append(key);
        return sb;
    }
    
    protected StringBuffer getComposedSrcSB() {
        final StringBuffer sb = new StringBuffer();
        String value = "";
        for (final YxSubValidation sub : this.list) {
            if (sub.name.equalsIgnoreCase(ValidationEnum.SIGN.getName())) {
                continue;
            }
            sb.append(sub.name);
            if (StringUtils.isBlank(sub.name)) {
                continue;
            }
            value = sub.value.replaceAll("\\s", "");
            sb.append(value);
        }
        return sb;
    }
    
    public String getValueByName(final String name) {
        for (final YxSubValidation sub : this.list) {
            if (sub.name.equalsIgnoreCase(name)) {
                return sub.value;
            }
        }
        return null;
    }
    
    public void addCheck(final MultiResult check) {
        if (this.needCheckMap == null) {
            this.needCheckMap = new HashMap<String, MultiResult>();
        }
        this.needCheckMap.put((String)check.result1, check);
    }
}
