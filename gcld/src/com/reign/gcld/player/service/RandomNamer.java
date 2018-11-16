package com.reign.gcld.player.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.common.*;
import org.dom4j.io.*;
import org.dom4j.*;
import com.reign.gcld.common.util.*;
import org.apache.commons.lang.math.*;
import java.util.*;

@Component("randomNamer")
public class RandomNamer implements InitializingBean
{
    @Autowired
    private IPlayerService playerService;
    public static final String SERVER_NAMEDATA_FILE = "NameData.xml";
    private Map<String, Integer> maleFirstNameMap;
    private Map<String, Integer> femaleFirstNameMap;
    private Map<String, Integer> lastNameMap;
    private String[] maleFirstNameList;
    private String[] femaleFirstNameList;
    private String[] lastNameList;
    private String[] incommonLastNameList;
    private List<String> sampleList;
    
    @Override
	public void afterPropertiesSet() throws Exception {
        final String path = String.valueOf(ListenerConstants.WEB_PATH) + "NameData.xml";
        final SAXReader saxReader = new SAXReader();
        try {
            final Document doc = saxReader.read(path);
            final Element config = doc.getRootElement();
            String maleFirstName = new String();
            String femaleFirstName = new String();
            String lastName = new String();
            String sample = new String();
            String incommonLastName = new String();
            if (config.element("lastname") != null) {
                lastName = config.element("lastname").attributeValue("data");
                lastName = lastName.replaceAll(" ", "");
            }
            if (config.element("male") != null) {
                maleFirstName = config.element("male").attributeValue("data");
                maleFirstName = maleFirstName.replaceAll(" ", "");
            }
            if (config.element("female") != null) {
                femaleFirstName = config.element("female").attributeValue("data");
                femaleFirstName = femaleFirstName.replaceAll(" ", "");
            }
            if (config.element("sample") != null) {
                sample = config.element("sample").attributeValue("data");
                sample = sample.replaceAll(" ", "");
            }
            if (config.element("incommon") != null) {
                incommonLastName = config.element("incommon").attributeValue("data");
                incommonLastName = incommonLastName.replaceAll(" ", "");
            }
            this.maleFirstNameMap = this.getWordAndIntonationMap(maleFirstName);
            this.femaleFirstNameMap = this.getWordAndIntonationMap(femaleFirstName);
            this.lastNameMap = this.getWordAndIntonationMap(lastName);
            final Map<String, Integer> sampleMap = this.getWordAndIntonationMap(sample);
            this.maleFirstNameList = this.maleFirstNameMap.keySet().toArray(new String[0]);
            this.femaleFirstNameList = this.femaleFirstNameMap.keySet().toArray(new String[0]);
            this.lastNameList = this.lastNameMap.keySet().toArray(new String[0]);
            this.incommonLastNameList = incommonLastName.split("\\|");
            this.sampleList = new ArrayList<String>();
            Integer[] array;
            for (int length = (array = sampleMap.values().toArray(new Integer[0])).length, i = 0; i < length; ++i) {
                final Integer s = array[i];
                this.sampleList.add(s.toString());
            }
            Collections.sort(this.sampleList);
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                throw new Exception(e);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
    private Map<String, Integer> getWordAndIntonationMap(final String string) {
        final Map<String, Integer> map = new HashMap<String, Integer>();
        final String[] tmps = string.trim().split("\\|");
        String[] array;
        for (int length2 = (array = tmps).length, i = 0; i < length2; ++i) {
            final String tmp = array[i];
            final int length = tmp.length();
            if (length == 2 || length == 4 || length == 6 || length == 8) {
                final String word = tmp.substring(0, length / 2);
                map.put(word, new Integer(tmp.substring(length / 2, length)));
            }
        }
        return map;
    }
    
    public List<String> generateRandomNames(final boolean male, int needCount) {
        final List<String> nameList = new ArrayList<String>();
        for (int maxRound = 1000; maxRound > 0 && needCount > 0; --maxRound) {
            final String name = this.getRandomName(male);
            if (this.playerService.checkName(name) && !this.playerService.validatePlayerName(name) && !nameList.contains(name)) {
                nameList.add(name);
                --needCount;
            }
        }
        return nameList;
    }
    
    public String getRandomName() {
        String name = null;
        for (int maxRound = 1000; maxRound > 0; --maxRound) {
            name = this.getRandomName(WebUtil.nextBoolean());
            if (this.playerService.checkName(name)) {
                return name;
            }
        }
        return "MM";
    }
    
    private String getRandomName(final boolean male) {
        final Map<String, Integer> firstNameMap = male ? this.maleFirstNameMap : this.femaleFirstNameMap;
        final String[] firstNameList = male ? this.maleFirstNameList : this.femaleFirstNameList;
        final int firstNameSize = firstNameMap.size();
        String fullName = new String();
        final double commonRatio = RandomUtils.nextDouble();
        if (commonRatio >= 0.4) {
            final int lastNameSize = this.lastNameMap.size();
            final double random = RandomUtils.nextDouble();
            for (int j = 0; j < 10; ++j) {
                fullName = new String();
                int randomIndex = RandomUtils.nextInt(lastNameSize);
                String code = new String();
                fullName = String.valueOf(fullName) + this.lastNameList[randomIndex];
                code = String.valueOf(code) + this.lastNameMap.get(this.lastNameList[randomIndex]);
                randomIndex = RandomUtils.nextInt(firstNameSize);
                final int firstNameLength = firstNameList[randomIndex].length();
                fullName = String.valueOf(fullName) + firstNameList[randomIndex];
                code = String.valueOf(code) + firstNameMap.get(firstNameList[randomIndex]);
                if (random > 0.75 && firstNameLength != 2) {
                    randomIndex = RandomUtils.nextInt(firstNameSize);
                    if (firstNameList[randomIndex].length() == 1) {
                        fullName = String.valueOf(fullName) + firstNameList[randomIndex];
                        code = String.valueOf(code) + firstNameMap.get(firstNameList[randomIndex]);
                    }
                }
                boolean ok = false;
                Collections.shuffle(this.sampleList);
                for (final String s : this.sampleList) {
                    if (s.equals(code)) {
                        ok = true;
                        break;
                    }
                }
                if (ok) {
                    break;
                }
            }
        }
        else {
            final int lastNameSize = this.incommonLastNameList.length;
            int randomIndex2 = RandomUtils.nextInt(lastNameSize);
            fullName = String.valueOf(fullName) + this.incommonLastNameList[randomIndex2];
            randomIndex2 = RandomUtils.nextInt(firstNameSize);
            fullName = String.valueOf(fullName) + firstNameList[randomIndex2];
        }
        return fullName;
    }
}
