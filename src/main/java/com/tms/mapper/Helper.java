package com.tms.mapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

public class Helper {

    private Helper(){};


    static Function createGetter(final Method method, boolean isRelated)  {
        return bean -> {
            try {
                Object result = method.invoke(bean);
                if (result != null && isRelated && ((Long)result).intValue() == 0)
                    return null;
                return result;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

    static Setter createSetter(final Method method)  {
        if (method == null)
            return null;
        return (bean, value) -> {
            try {
                method.invoke(bean, bean);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

}
