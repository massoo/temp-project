package org.owasp.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.*;
import java.util.regex.Pattern;

/**
 * User: massoo
 */
public class CleanseUtility {

    private static final Logger LOG = LoggerFactory.getLogger(CleanseUtility.class);

    public static String cleanseString(String string, Pattern pattern) {
        // TODO: apply canonicalization
        //String normalizedString = Utility.normalize(string);

        LOG.debug("Using cleansing pattern: {}", pattern.pattern());
        String cleansedString = string.replaceAll(pattern.pattern(), "");

        return cleansedString;
    }

    // TODO: Returning a valid enumeration is not yet implemented
    public static Enumeration cleanseEnumeration(Enumeration<String> enumeration, Pattern pattern) {

        Assert.notNull(enumeration);
        List<String> list = Collections.list(enumeration);

        list = cleanseList(list, pattern);

        return Collections.enumeration(list);
    }

    public static List<String> cleanseList(List<String> stringList, Pattern pattern) {
        List<String> newList = new ArrayList<String>();

        for (String string : stringList) {
            newList.add(cleanseString(string, pattern));
        }

        return newList;
    }

    public static Map<String, String[]> cleanseMap(Map<String, String[]> map, Pattern pattern) {

        Map<String, String[]> newMap = new LinkedHashMap<String, String[]>();
        Iterator iterator = map.keySet().iterator();

        while (iterator.hasNext()) {
            String key = iterator.next().toString();

            String[] values = map.get(key);
            String[] newValues = new String[values.length];

            for (int i = 0; i < values.length; i++) {
                newValues[i] = cleanseString(values[i], pattern); // Apply validation logic to the value
            }

            key = cleanseString(key, pattern);
            newMap.put(key, newValues);
        }

        return newMap;
    }


}
