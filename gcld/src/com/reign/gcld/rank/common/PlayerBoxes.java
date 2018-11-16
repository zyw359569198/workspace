package com.reign.gcld.rank.common;

import org.apache.commons.lang.*;
import java.util.*;

public class PlayerBoxes
{
    private Map<Integer, PlayerSingleBoxInfo> map;
    
    public PlayerBoxes(final String string) {
        this.map = new HashMap<Integer, PlayerSingleBoxInfo>();
        if (!StringUtils.isBlank(string)) {
            final String[] boxes = string.split(";");
            String[] array;
            for (int length = (array = boxes).length, i = 0; i < length; ++i) {
                final String s = array[i];
                final PlayerSingleBoxInfo info = new PlayerSingleBoxInfo(s);
                this.map.put(info.getBoxId(), info);
            }
        }
    }
    
    @Override
    public String toString() {
        if (this.map.size() == 0) {
            return "";
        }
        final StringBuffer sbBuffer = new StringBuffer();
        for (final Integer key : this.map.keySet()) {
            sbBuffer.append(this.map.get(key)).append(";");
        }
        if (sbBuffer.length() > 0) {
            sbBuffer.deleteCharAt(sbBuffer.length() - 1);
        }
        return sbBuffer.toString();
    }
    
    public String changeNum(final int boxId, final int num) {
        this.map.get(boxId).setBoxNum(num);
        return this.toString();
    }
    
    public void add(final int i, final int addNum, final int maxNum) {
        if (this.map.get(i) != null) {
            final PlayerSingleBoxInfo boxInfo = this.map.get(i);
            int num = boxInfo.getBoxNum();
            num += addNum;
            num = ((num > maxNum) ? maxNum : num);
            boxInfo.setBoxNum(num);
        }
        else {
            final PlayerSingleBoxInfo boxInfo = new PlayerSingleBoxInfo();
            boxInfo.setBoxId(i);
            boxInfo.setBoxNum(addNum);
            this.map.put(i, boxInfo);
        }
    }
    
    public void minus(final int i) {
        if (this.map.get(i) != null) {
            final PlayerSingleBoxInfo boxInfo = this.map.get(i);
            final int num = (boxInfo.getBoxNum() == 0) ? 0 : (boxInfo.getBoxNum() - 1);
            boxInfo.setBoxNum(num);
        }
    }
    
    public int getNum(final int i) {
        final PlayerSingleBoxInfo singleBoxInfo = this.map.get(i + 1);
        return (singleBoxInfo == null) ? 0 : singleBoxInfo.getBoxNum();
    }
}
