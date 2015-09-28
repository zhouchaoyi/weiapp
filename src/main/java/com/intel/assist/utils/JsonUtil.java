package com.intel.assist.utils;

import org.codehaus.jettison.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kunpeng on 2015/6/25.
 */
public class JsonUtil {

    public static <T> JSONObject objectToJson(Class<T> c,T object) throws Exception{
        
        Method[] methods = object.getClass().getDeclaredMethods();
        Map<String,Object> objMap = new HashMap<String,Object>();
        for (int i = 0; i < methods.length; i++) {
            if(methods[i].getName().startsWith("get")){
                String name=methods[i].getName().substring(3);

                System.out.println("name-> " +name);

                char[] names = name.toCharArray();
                names[0] += 32;
                String lowName = String.valueOf(names);
                objMap.put(lowName,methods[i].invoke(object));
            }
        }
        return new JSONObject(objMap);
    }

}
