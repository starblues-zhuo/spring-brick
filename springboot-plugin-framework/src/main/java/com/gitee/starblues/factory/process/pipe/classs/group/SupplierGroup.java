package com.gitee.starblues.factory.process.pipe.classs.group;

import com.gitee.starblues.annotation.Supplier;
import com.gitee.starblues.factory.process.pipe.classs.PluginClassGroup;
import com.gitee.starblues.utils.AnnotationsUtils;


/**
 * 分组存在注解: @Supplier
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class SupplierGroup implements PluginClassGroup {

    /**
     * 自定义 @Supplier
     */
    public static final String SUPPLIER = "supplier";


    @Override
    public String groupId() {
        return SUPPLIER;
    }

    @Override
    public boolean filter(Class<?> aClass) {
        return AnnotationsUtils.haveAnnotations(aClass, false, Supplier.class);
    }
}
