package com.gitee.starblues.utils;

import org.junit.Assert;
import org.junit.Test;
import org.pf4j.RuntimeMode;

/**
 * @author starBlues
 * @version 1.0
 */
public class PluginConfigUtilsTest {


     @Test
     public void testGetConfigFileName(){
         String fileName = "fileName.yml";
         String prod = "prod";
         String dev = "dev";
         PluginConfigUtils.FileNamePack configFileName = PluginConfigUtils.getConfigFileName(fileName,
                 prod, dev, RuntimeMode.DEPLOYMENT);
         Assert.assertNotNull(configFileName);
         Assert.assertEquals(fileName, configFileName.getSourceFileName());
         Assert.assertEquals(prod, configFileName.getFileSuffix());

         configFileName = PluginConfigUtils.getConfigFileName(fileName,
                 prod, dev, RuntimeMode.DEVELOPMENT);
         Assert.assertNotNull(configFileName);
         Assert.assertEquals(fileName, configFileName.getSourceFileName());
         Assert.assertEquals(dev, configFileName.getFileSuffix());
     }


     @Test
     public void testJoinConfigFileName_1(){
         String fileName = "fileName.yml";
         String suffix = "prod";
         String joinConfigFileName = PluginConfigUtils.joinConfigFileName(fileName, suffix);
         Assert.assertNotNull(joinConfigFileName);
         Assert.assertEquals("fileName-prod.yml", joinConfigFileName);
     }

     @Test
     public void testJoinConfigFileName_2(){
         String fileName = "fileName.yml";
         String suffix = "";
         String joinConfigFileName = PluginConfigUtils.joinConfigFileName(fileName, suffix);
         Assert.assertNotNull(joinConfigFileName);
         Assert.assertEquals("fileName.yml", joinConfigFileName);
     }

     @Test
     public void testJoinConfigFileName_3(){
         String fileName = "fileName.yml";
         String suffix = null;
         String joinConfigFileName = PluginConfigUtils.joinConfigFileName(fileName, suffix);
         Assert.assertNotNull(joinConfigFileName);
         Assert.assertEquals("fileName.yml", joinConfigFileName);
     }

     @Test
     public void testJoinConfigFileName_4(){
         String fileName = null;
         String suffix = null;
         String joinConfigFileName = PluginConfigUtils.joinConfigFileName(fileName, suffix);
         Assert.assertNull(joinConfigFileName);
     }

}
