package com.reign.gcld;

import com.reign.framework.*;

public class Main
{
    public static void main(final String[] args) {
        final ServletBootstrap bootstrap = new ServletBootstrap();
        try {
            bootstrap.startup();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
