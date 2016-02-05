package com.shenit.commons.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.shenit.commons.utils.DataUtils;
import com.shenit.commons.utils.HttpUtils;
import com.shenit.commons.utils.ValidationUtils;

public class QueryString {
    public Map<String, List<String>> params;
    public String query;

    public QueryString(String query) {
        this.query = query;
        params = parseQueryInternal(query);
    }

    public Integer getInteger(String param) {
        return getInteger(param, null);
    }

    public Integer getInteger(String param, Integer defaultVal) {
        return hasParam(param) ? DataUtils.toInt(params.get(param).get(0),
                        defaultVal) : defaultVal;
    }

    public String get(String param) {
        return get(param, null);
    }

    public String get(String param, String defaultVal) {
        return hasParam(param) ? params.get(param).get(0) : defaultVal;
    }

    public Long getLong(String param) {
        return getLong(param, null);
    }

    public Long getLong(String param, Long defaultVal) {
        return hasParam(param) ? DataUtils.toLong(params.get(param).get(0),
                        defaultVal) : defaultVal;
    }

    public String[] getArray(String param) {
        return hasParam(param) ? params.get(param).toArray(new String[0])
                        : null;
    }

    /**
     * @param param
     * @return
     */
    public boolean hasParam(String param) {
        List<String> vals = params.get(param);
        return !ValidationUtils.isEmpty(vals);
    }

    private Map<String, List<String>> parseQueryInternal(String query) {
        params = new HashMap<String, List<String>>();
        if (StringUtils.isEmpty(query)) return params;
        String[] pairs = query.split(HttpUtils.AMP);
        String[] pairValue;
        for (String pair : pairs) {
            pairValue = pair.split(HttpUtils.EQ);
            if (!params.containsKey(pairValue[0])) {
                params.put(pairValue[0], new ArrayList<String>());
            }
            params.get(pairValue[0]).add(pairValue[1]);
        }
        return params;
    }
}
