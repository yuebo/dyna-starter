/*
 *
 *  * Copyright 2002-2017 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.github.yuebo.dyna.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: yuebo
 * Date: 11/24/14
 * Time: 1:24 PM
 */
public class ConvertUtils {

    private static final String ARRAY_PATTERN = "[_a-zA-Z0-9\\$]+\\[[0-9]+\\]";

    public static void setProperty(Map<String, Object> srcMap, String property, Object value) {
        assert srcMap != null && property != null;
        String[] properties = property.trim().split("\\.");
        setProperty(srcMap, properties, value);
    }

    public static void setProperty(Map<String, Object> srcMap, String[] prop, Object value) {
        String property = prop[0];
        int index = 0;
        boolean array = false;
        if (property.matches(ARRAY_PATTERN)) {
            index = Integer.valueOf(property.replaceAll("[_a-zA-Z0-9\\$]+\\[", "").replaceAll("\\]", ""));
            property = property.replaceAll("\\[[0-9]+\\]", "");
            array = true;
        } else {

        }
        if (prop.length > 1) {
            Object o = srcMap.get(property);
            if (o == null) {
                if (array) {
                    List list = new ArrayList();
                    srcMap.put(property, list);
                    HashMap<String, Object> newMap = new HashMap<String, Object>();
                    ensureIndexValid(list, index);
                    list.set(index, newMap);
                    String[] prop2 = new String[prop.length - 1];
                    for (int i = 1; i < prop.length; i++) {
                        prop2[i - 1] = prop[i];
                    }
                    setProperty(newMap, prop2, value);

                } else {

                    HashMap<String, Object> newMap = new HashMap<String, Object>();
                    srcMap.put(property, newMap);

                    String[] prop2 = new String[prop.length - 1];
                    for (int i = 1; i < prop.length; i++) {
                        prop2[i - 1] = prop[i];
                    }
                    setProperty(newMap, prop2, value);
                }
            } else {
                if (array) {
                    List list = (List) srcMap.get(property);
                    ensureIndexValid(list, index);
                    Object o1 = list.get(index);
                    HashMap<String, Object> newMap = new HashMap<String, Object>();
                    if (o1 == null) {
                        if (prop.length > 1) {
                            newMap = new HashMap();
                            ensureIndexValid(list, index);
                            list.set(index, newMap);
                        } else {
                            ensureIndexValid(list, index);
                            list.set(index, o1);
                        }

                    } else {
                        newMap = (HashMap) o1;
                    }

                    String[] prop2 = new String[prop.length - 1];
                    for (int i = 1; i < prop.length; i++) {
                        prop2[i - 1] = prop[i];
                    }
                    setProperty(newMap, prop2, value);

                } else {
                    HashMap<String, Object> newMap = (HashMap<String, Object>) o;
                    String[] prop2 = new String[prop.length - 1];
                    for (int i = 1; i < prop.length; i++) {
                        prop2[i - 1] = prop[i];
                    }
                    setProperty(newMap, prop2, value);
                }
            }
        } else {
            if (array) {
                List list = (List) srcMap.get(property);
                if (list == null) {
                    list = new ArrayList();
                    srcMap.put(property, list);
                }
                ensureIndexValid(list, index);
                list.set(index, value);
            } else {
                srcMap.put(property, value);
            }


        }
    }


    public static Object getProperty(Map<String, Object> srcMap, String property) {
        assert srcMap != null && property != null;
        String[] properties = property.trim().split("\\.");
        return getProperty(srcMap, properties);
    }

    public static Object getProperty(Map<String, Object> srcMap, String[] prop) {
        String property = prop[0];
        int index = 0;
        boolean array = false;
        if (property.matches(ARRAY_PATTERN)) {
            index = Integer.valueOf(property.replaceAll("[_a-zA-Z0-9\\$]+\\[", "").replaceAll("\\]", ""));
            property = property.replaceAll("\\[[0-9]+\\]", "");
            array = true;
        } else {

        }
        if (prop.length > 1) {
            Object o = srcMap.get(property);
            if (o == null) {
                return null;
            } else {
                if (array) {
                    List list = (List) srcMap.get(property);
                    if (list.size() - 1 > index) {
                        return null;
                    }
                    Object o1 = list.get(index);
                    HashMap<String, Object> newMap = new HashMap<String, Object>();
                    if (o1 == null) {
                        return null;
                    } else {
                        newMap = (HashMap) o1;
                    }

                    String[] prop2 = new String[prop.length - 1];
                    for (int i = 1; i < prop.length; i++) {
                        prop2[i - 1] = prop[i];
                    }
                    return getProperty(newMap, prop2);

                } else {
                    HashMap<String, Object> newMap = (HashMap<String, Object>) o;
                    String[] prop2 = new String[prop.length - 1];
                    for (int i = 1; i < prop.length; i++) {
                        prop2[i - 1] = prop[i];
                    }
                    return getProperty(newMap, prop2);
                }
            }
        } else {
            if (array) {
                List list = (List) srcMap.get(property);
                if (list == null) {
                    list = new ArrayList();
                    srcMap.put(property, list);
                }
                ensureIndexValid(list, index);
                return list.get(index);
            } else {
                return srcMap.get(property);
            }


        }
    }


    public static void removeProperty(Map<String, Object> srcMap, String key) {
        if (key.indexOf(".") > 0) {
            String obj = key.substring(0, key.lastIndexOf("."));
            String property = key.substring(key.lastIndexOf(".") + 1);
            Object o = getProperty(srcMap, obj);
            if (o instanceof Map) {
                Map val = (Map) o;
                val.remove(property);
            } else {
                throw new RuntimeException("Cannot remove the property");
            }

        } else {
            srcMap.remove(key);
        }
    }


    private static void ensureIndexValid(List list, int index) {
        for (int i = list.size(); i <= index; i++) {
            list.add(null);
        }
    }


    public static Map<String, Object> json2Map(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            HashMap<String, Object> map = mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {
            });
            return map;
        } catch (IOException e) {
            return new HashMap();
        }

    }


}
