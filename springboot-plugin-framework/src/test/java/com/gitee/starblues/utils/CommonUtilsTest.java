package com.gitee.starblues.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author starBlues
 * @version 1.0
 */
public class CommonUtilsTest {

    @Test
    public void testJoin(){
        Assert.assertEquals(CommonUtils.joiningPath("/p1", "p2", "p3"), "/p1/p2/p3");
        Assert.assertEquals(CommonUtils.joiningPath("/p1", "p2", "p3/"), "/p1/p2/p3/");
        Assert.assertEquals(CommonUtils.joiningPath("p1", "p2", "p3/"), "/p1/p2/p3/");
    }



}
