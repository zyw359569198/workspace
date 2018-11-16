package com.reign.gcld.sdata.cache;

import com.reign.gcld.sdata.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.common.cache.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.common.util.*;

@Component("chatWordsCache")
public class ChatWordsCache extends AbstractCache<Integer, ChatWords>
{
    @Autowired
    private SDataLoader dataLoader;
    private Map<Integer, ArrayList<String>> lvToWordsMap;
    
    public ChatWordsCache() {
        this.lvToWordsMap = new HashMap<Integer, ArrayList<String>>();
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final List<ChatWords> result = this.dataLoader.getModels((Class)ChatWords.class);
        ArrayList<String> generalList = null;
        for (final ChatWords chatWords : result) {
            final String[] wordsArray = chatWords.getWords().split(";");
            chatWords.setWordsArray(wordsArray);
            final ArrayList<String> list = new ArrayList<String>();
            list.addAll(Arrays.asList(wordsArray));
            if ("0".equalsIgnoreCase(chatWords.getLv())) {
                generalList = list;
            }
            else {
                final String[] lvs = chatWords.getLv().split(",");
                String[] array;
                for (int length = (array = lvs).length, i = 0; i < length; ++i) {
                    final String lvsString = array[i];
                    final int lv = Integer.parseInt(lvsString);
                    this.lvToWordsMap.put(lv, list);
                }
            }
            super.put((Object)chatWords.getId(), (Object)chatWords);
        }
        for (final Integer lv2 : this.lvToWordsMap.keySet()) {
            this.lvToWordsMap.get(lv2).addAll(generalList);
        }
    }
    
    public String getRandomChatWords(final int playerLv) {
        final ArrayList<String> list = this.lvToWordsMap.get(playerLv);
        if (list == null || list.size() == 0) {
            ErrorSceneLog.getInstance().appendErrorMsg("playerCourtesyObj is null or empty").append("playerLv", playerLv).appendClassName("ChatWordsCache").appendMethodName("getRandomChatWords").flush();
            return null;
        }
        final int randIndex = WebUtil.nextInt(list.size());
        return list.get(randIndex);
    }
    
    @Override
	public void clear() {
        this.lvToWordsMap.clear();
        super.clear();
    }
}
