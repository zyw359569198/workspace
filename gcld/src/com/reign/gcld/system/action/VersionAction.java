package com.reign.gcld.system.action;

import com.reign.gcld.common.web.*;
import com.reign.framework.netty.mvc.view.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.component.*;
import java.util.*;
import com.reign.framework.netty.mvc.annotation.*;

@Action
@Views({ @View(name = "byte", type = ByteView.class, compress = "false") })
public class VersionAction extends BaseAction
{
    private static final long serialVersionUID = 1L;
    
    @Command("version")
    public ByteResult version(final Request request) {
        final List<ComponentMessage> componentList = ComponentManager.getInstance().getAllComponent();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        for (final ComponentMessage cm : componentList) {
            doc.createElement(cm.getComponentName(), cm.getVersion());
        }
        doc.endObject();
        return new ByteResult(doc.toByte());
    }
}
