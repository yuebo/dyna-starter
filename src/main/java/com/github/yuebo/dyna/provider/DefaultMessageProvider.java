package com.github.yuebo.dyna.provider;

import com.github.yuebo.dyna.AppConstants;
import com.github.yuebo.dyna.utils.SpringUtils;
import com.github.yuebo.dyna.service.JDBCService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: yuebo
 * Date: 12/8/14
 * Time: 5:00 PM
 */
@Component("defaultMessageProvider")
public class DefaultMessageProvider implements AppConstants {
    @Autowired
    private JDBCService jdbcService;
    @Autowired
    private SpringUtils springUtils;

    public String get(String key) {
        Map condition = new HashMap();
        condition.put("_data", TBL_MESSAGE);
        condition.put("key", key);
        Map result = jdbcService.findData(condition);
        if (result == null || result.isEmpty()) {
            return key;
        } else return (String) result.get("value");
    }

    public boolean hasError(Map messages, String name) {
        if (messages == null) {
            return false;
        } else {
            List<Map<String, String>> error = (List<Map<String, String>>) messages.get("error");
            if (error != null) {
                for (Map<String, String> e : error) {
                    if (name.equals(e.get("name"))) {
                        return true;
                    }
                }
                return false;
            } else {
                return false;
            }
        }
    }

    public String label(String label) {
        return label == null ? "" : label.replaceAll("\\n", "<br>");
    }

    public boolean isNull(Object o) {
        return o == null;
    }

    public List<String> split(String org, String c) {
        ArrayList<String> ret = new ArrayList<>();
        if (org == null) {
            return ret;
        }
        String[] result = org.split(c);
        for (String r : result) {
            if (StringUtils.isNotEmpty(r)) {
                ret.add(r);
            }
        }
        return ret;
    }

    public String convert(Object o) {
        if (o instanceof List) {
            StringBuffer buffer = new StringBuffer();
            List<String> l = (List) o;
            int i = 0;
            for (String item : l) {
                if (i > 0) {
                    buffer.append(",");
                }
                buffer.append(item);
                i++;
            }
            return buffer.toString();
        }
        return o == null ? "" : o.toString();

    }

    public String escapeName(String name){
        if(name.contains($)){
            return StringUtils.replace(name,$,"\\$");
        }else {
            return name;
        }
    }

}
