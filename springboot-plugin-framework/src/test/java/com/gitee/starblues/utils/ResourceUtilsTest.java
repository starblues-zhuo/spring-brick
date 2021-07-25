package com.gitee.starblues.utils;


import org.junit.Assert;
import org.junit.Test;

/**
 * @author starBlues
 * @version 2.3.4
 * @since 2021-06-01
 */
public class ResourceUtilsTest {

    @Test
    public void test(){
        String locationMatch = "file:C:\\Users\\Desktop\\plugin1-log.xml";
        String matchLocation = ResourceUtils.getMatchLocation(locationMatch);
        Assert.assertEquals("C:\\Users\\Desktop\\plugin1-log.xml",
                matchLocation);

        locationMatch = "classpath:C:\\Users\\Desktop\\plugin1-log.xml";
        matchLocation = ResourceUtils.getMatchLocation(locationMatch);
        Assert.assertEquals("C:\\Users\\Desktop\\plugin1-log.xml",
                matchLocation);

        locationMatch = "package:com.test.aa";
        matchLocation = ResourceUtils.getMatchLocation(locationMatch);
        Assert.assertEquals("com/test/aa",
                matchLocation);


    }

}