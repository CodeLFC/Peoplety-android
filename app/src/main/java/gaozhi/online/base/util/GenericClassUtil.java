package gaozhi.online.base.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericClassUtil {
    /**
     * 获取泛型类class对象
     * @param klass
     * @return
     */
    public static Class<?> getClass(Class<?>klass){
        Class<?> actualClass = null;
        Type type = klass.getGenericSuperclass();
        if(type instanceof ParameterizedType){
            Type[]actualTypeArgument = ((ParameterizedType)type).getActualTypeArguments();
            if(actualTypeArgument.length>0){
                actualClass = (Class<?>) actualTypeArgument[0];
            }
        }
        return actualClass;
    }
}
