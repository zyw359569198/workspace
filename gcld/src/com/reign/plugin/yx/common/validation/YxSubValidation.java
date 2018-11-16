package com.reign.plugin.yx.common.validation;

import com.reign.plugin.yx.common.*;
import org.apache.commons.lang.*;

public class YxSubValidation implements Comparable<YxSubValidation>
{
    String name;
    String value;
    String errorString;
    String errorCode;
    
    public MultiResult validate() {
        final MultiResult result = new MultiResult();
        result.result1 = StringUtils.isBlank(this.value);
        result.result2 = this.errorCode;
        result.result3 = this.errorString;
        return result;
    }
    
    @Override
    public int compareTo(final YxSubValidation sub) {
        return this.name.compareTo(sub.name);
    }
    
    public MultiResult validate(final MultiResult check) {
        final MultiResult result = new MultiResult();
        boolean isValid = !StringUtils.isBlank(this.value);
        isValid = (isValid && this.doValidateWithCheck(check));
        result.result1 = isValid;
        result.result2 = this.errorCode;
        result.result3 = this.errorString;
        return result;
    }
    
    private boolean doValidateWithCheck(final MultiResult check) {
        return check == null || check.result2 == null || this.value.equalsIgnoreCase((String)check.result2);
    }
}
