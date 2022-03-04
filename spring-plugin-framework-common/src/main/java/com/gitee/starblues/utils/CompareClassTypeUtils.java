/**
 * Copyright [2019-2022] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.utils;

/**
 * 比较两个类类型
 *
 * @author starBlues
 * @version 3.0.0
 */
public abstract class CompareClassTypeUtils {

    private CompareClassTypeUtils(){}

    public static boolean compare(Class<?> class1, Class<?> class2){
        if(class1.isAssignableFrom(class2)){
            return true;
        }
        if(isBoolean(class1) && isBoolean(class2)){
            return true;
        }
        if(isChar(class1) && isChar(class2)){
            return true;
        }
        if(isByte(class1) && isByte(class2)){
            return true;
        }
        if(isShort(class1) && isShort(class2)){
            return true;
        }
        if(isInt(class1) && isInt(class2)){
            return true;
        }
        if(isLong(class1) && isLong(class2)){
            return true;
        }
        if(isFloat(class1) && isFloat(class2)){
            return true;
        }
        if(isDouble(class1) && isDouble(class2)){
            return true;
        }
        if(isVoid(class1) && isVoid(class2)){
            return true;
        }
        return false;
    }


    public static boolean isBoolean(Class<?> class1){
        return class1.isAssignableFrom(Boolean.class) || class1.isAssignableFrom(boolean.class);
    }

    public static boolean isChar(Class<?> class1){
        return class1.isAssignableFrom(Character.class) || class1.isAssignableFrom(char.class);
    }

    public static boolean isByte(Class<?> class1){
        return class1.isAssignableFrom(Byte.class) || class1.isAssignableFrom(byte.class);
    }

    public static boolean isShort(Class<?> class1){
        return class1.isAssignableFrom(Short.class) || class1.isAssignableFrom(short.class);
    }

    public static boolean isInt(Class<?> class1){
        return class1.isAssignableFrom(Integer.class) || class1.isAssignableFrom(int.class);
    }

    public static boolean isLong(Class<?> class1){
        return class1.isAssignableFrom(Long.class) || class1.isAssignableFrom(long.class);
    }

    public static boolean isFloat(Class<?> class1){
        return class1.isAssignableFrom(Float.class) || class1.isAssignableFrom(float.class);
    }

    public static boolean isDouble(Class<?> class1){
        return class1.isAssignableFrom(Double.class) || class1.isAssignableFrom(double.class);
    }

    public static boolean isVoid(Class<?> class1){
        return class1.isAssignableFrom(Void.class) || class1.isAssignableFrom(void.class);
    }

}
