package com.gitee.starblues.integration.operator;

import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.listener.PluginInitializerListenerFactory;
import com.gitee.starblues.integration.listener.PluginListenerFactory;
import com.gitee.starblues.integration.operator.module.PluginInfo;
import com.gitee.starblues.integration.operator.verify.PluginLegalVerify;
import com.gitee.starblues.integration.operator.verify.PluginUploadVerify;
import com.gitee.starblues.factory.DefaultPluginFactory;
import com.gitee.starblues.factory.PluginFactory;
import com.gitee.starblues.utils.GlobalRegistryInfo;
import com.gitee.starblues.utils.PluginOperatorInfo;
import com.gitee.starblues.utils.PluginFileUtils;
import org.apache.commons.io.FileUtils;
import org.pf4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 默认的插件操作者
 * @author zhangzhuo
 * @version 2.1.0
 */
public class DefaultPluginOperator implements PluginOperator {

    private boolean isInit = false;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");


    private final IntegrationConfiguration integrationConfiguration;
    private final PluginManager pluginManager;
    private final PluginFactory pluginFactory;
    private final PluginInitializerListenerFactory pluginInitializerListenerFactory;

    private final PluginDescriptorFinder pluginDescriptorFinder;
    private final PluginLegalVerify uploadPluginVerify;


    public DefaultPluginOperator(ApplicationContext applicationContext,
                                 IntegrationConfiguration integrationConfiguration,
                                 PluginManager pluginManager,
                                 PluginListenerFactory pluginListenerFactory) {
        Objects.requireNonNull(integrationConfiguration);
        Objects.requireNonNull(pluginManager);
        this.integrationConfiguration = integrationConfiguration;
        this.pluginManager = pluginManager;
        this.pluginFactory = new DefaultPluginFactory(applicationContext, pluginListenerFactory);
        this.pluginInitializerListenerFactory = new PluginInitializerListenerFactory(applicationContext);

        this.pluginDescriptorFinder = new ManifestPluginDescriptorFinder();
        this.uploadPluginVerify = new PluginUploadVerify(this.pluginDescriptorFinder, pluginManager);
    }


    @Override
    public synchronized boolean initPlugins(PluginInitializerListener pluginInitializerListener) throws Exception {
        if(isInit){
            throw new RuntimeException("Plugins Already initialized. Cannot be initialized again");
        }
        try {
            pluginInitializerListenerFactory.addPluginInitializerListeners(pluginInitializerListener);
            log.info("Start initialize plugins of root path '{}'", pluginManager.getPluginsRoot().toString());
            pluginInitializerListenerFactory.before();
            // 启动前, 清除空文件
            PluginFileUtils.cleanEmptyFile(pluginManager.getPluginsRoot());
            pluginManager.loadPlugins();
            pluginManager.startPlugins();
            List<PluginWrapper> pluginWrappers = pluginManager.getStartedPlugins();
            if(pluginWrappers == null || pluginWrappers.isEmpty()){
                log.warn("Not found plugin!");
                return false;
            }
            for (PluginWrapper pluginWrapper : pluginWrappers) {
                GlobalRegistryInfo.addOperatorPluginInfo(pluginWrapper.getPluginId(),
                        PluginOperatorInfo.OperatorType.INSTALL, false);
                pluginFactory.registry(pluginWrapper);
            }
            pluginFactory.build();
            log.info("Initialize plugins success");
            pluginInitializerListenerFactory.complete();
            isInit = true;
            return true;
        }  catch (Exception e){
            pluginInitializerListenerFactory.failure(e);
            throw e;
        }
    }


    @Override
    public boolean install(Path path) throws Exception {
        if(path == null){
            throw new IllegalArgumentException("Method:install param 'pluginId' can not be empty");
        }
        String pluginId = null;
        try {
            pluginId = pluginManager.loadPlugin(path);
            if(StringUtils.isEmpty(pluginId)){
                log.error("Install plugin '{}' failure, this plugin id is empty.", pluginId);
                return false;
            }
            GlobalRegistryInfo.addOperatorPluginInfo(pluginId, PluginOperatorInfo.OperatorType.INSTALL, true);
            if(start(pluginId)){
                log.info("Install plugin '{}' success. {}", pluginId);
                return true;
            } else {
                log.error("Install plugin '{}' failure", pluginId);
                return false;
            }
        } catch (Exception e){
            // 说明load成功, 但是没有启动成功, 则卸载该插件
            log.error("Install plugin '{}' failure. {}", pluginId, e.getMessage());
            log.info("Start uninstall plugin '{}' failure", pluginId);
            try {
                uninstall(pluginId);
            } catch (Exception uninstallException){
                log.error("Uninstall plugin '{}' failure. {}", pluginId, e.getMessage());
            }
            throw e;
        } finally {
            if(pluginId != null){
                GlobalRegistryInfo.setOperatorPluginInfo(pluginId, false);
            }
        }
    }

    @Override
    public boolean uninstall(String pluginId) throws Exception {
        PluginWrapper pluginWrapper = pluginManager.getPlugin(pluginId);
        if(pluginWrapper == null){
            log.error("Uninstall plugin failure, Not found plugin '{}'", pluginId);
            return false;
        }
        Exception exception = null;
        try {
            pluginFactory.unRegistry(pluginId);
            pluginFactory.build();
        } catch (Exception e){
            log.error("Uninstall plugin '{}' failure, {}", pluginId, e.getMessage());
            exception = e;
        }
        try {
            if (pluginManager.unloadPlugin(pluginId)) {
                // 卸载完后，将插件文件移到备份文件中
                backup(pluginWrapper.getPluginPath(), "uninstallPlugin", 1);
                log.info("Uninstall plugin '{}' success", pluginId);
                return true;
            } else {
                log.error("Uninstall plugin '{}' failure", pluginId);
                return false;
            }
        } catch (Exception e){
            if(exception != null){
                exception.printStackTrace();
            }
            log.error("Uninstall plugin '{}' failure. {}", pluginId, e.getMessage());
            throw e;
        }
    }



    @Override
    public boolean start(String pluginId) throws Exception {
        if(StringUtils.isEmpty(pluginId)){
            throw new IllegalArgumentException("Method:start param 'pluginId' can not be empty");
        }
        PluginWrapper pluginWrapper = getPluginWrapper(pluginId, "Start");
        if(pluginWrapper.getPluginState() == PluginState.STARTED){
            throw new Exception("This plugin '" + pluginId + "' have already started");
        }
        try {
            PluginState pluginState = pluginManager.startPlugin(pluginId);
            if(pluginState == PluginState.STARTED){
                GlobalRegistryInfo.addOperatorPluginInfo(pluginId, PluginOperatorInfo.OperatorType.START, false);
                pluginFactory.registry(pluginWrapper);
                pluginFactory.build();
                log.info("Start plugin '{}' success", pluginId);
                return true;
            }
            log.error("Start plugin '{}' failure, plugin state is not start. Current plugin state is '{}'",
                    pluginId, pluginState.toString());
        } catch (Exception e){
            log.error("Start plugin '{}' failure. {}", pluginId, e.getMessage());
            log.info("Start stop plugin {}", pluginId);
            try {
                stop(pluginId);
            } catch (Exception stopException){
                log.error("Stop plugin '{}' failure. {}", pluginId, e.getMessage());
            }
            throw e;
        }

        return false;
    }

    @Override
    public boolean stop(String pluginId) throws Exception {
        if(StringUtils.isEmpty(pluginId)){
            throw new IllegalArgumentException("Method:stop param 'pluginId' can not be empty");
        }
        PluginWrapper pluginWrapper = getPluginWrapper(pluginId, "Stop");
        if(pluginWrapper.getPluginState() != PluginState.STARTED){
            throw new Exception("This plugin '" + pluginId + "' is not started");
        }
        try {
            pluginFactory.unRegistry(pluginId);
            pluginFactory.build();
        } catch (Exception e){
            log.error("Stop plugin '{}' failure. {}", pluginId, e.getMessage());
            e.printStackTrace();
        }
        try {
            pluginManager.stopPlugin(pluginId);
            log.info("Stop plugin '{}' success", pluginId);
            return true;
        } catch (Exception e){
            log.error("Stop plugin '{}' failure. {}", pluginId, e.getMessage());
            throw e;
        }
    }


    @Override
    public boolean uploadPluginAndStart(MultipartFile pluginFile) throws Exception {
        if(pluginFile == null){
            throw new IllegalArgumentException("Method:uploadPluginAndStart param 'pluginFile' can not be null");
        }
        Path path = uploadPlugin(pluginFile);
        if(this.install(path)){
            log.info("Upload And Start plugin Success");
            return true;
        } else {
            log.error("Upload And Start plugin failure");
            return false;
        }
    }

    @Override
    public boolean installConfigFile(Path path) throws Exception {
        if(!Files.exists(path)){
            throw new FileNotFoundException("path ' " + path + "'  does not exist!");
        }
        File sourceFile = path.toFile();
        String configPath = integrationConfiguration.pluginConfigFilePath() +
                File.separator + sourceFile.getName();
        Path targetPath = PluginFileUtils.getExistPath(Paths.get(configPath));
        FileUtils.copyFile(sourceFile, targetPath.toFile());
        return true;
    }

    @Override
    public boolean uploadConfigFile(MultipartFile configFile) throws Exception {
        if(configFile == null){
            throw new IllegalArgumentException("Method:uploadConfigFile param 'configFile' can not be null");
        }
        String fileName = configFile.getOriginalFilename();
        String configPath = integrationConfiguration.pluginConfigFilePath() +
                File.separator + fileName;
        Path srcPath = PluginFileUtils.getExistPath(Paths.get(configPath));
        Files.write(srcPath, configFile.getBytes());
        return true;
    }

    @Override
    public boolean backupPlugin(Path path, String appendName) throws Exception {
        Objects.requireNonNull(path);
        return backup(path, appendName, 2);
    }


    @Override
    public boolean backupPlugin(String pluginId, String appendName) throws Exception {
        PluginWrapper pluginManager = getPluginWrapper(pluginId, "BackupPlugin by pluginId");
        return backupPlugin(pluginManager.getPluginPath(), appendName);
    }

    @Override
    public List<PluginInfo> getPluginInfo() {
        List<PluginWrapper> startedPlugins = pluginManager.getPlugins();
        List<PluginInfo> pluginInfos = new ArrayList<>();
        if(startedPlugins == null){
            return pluginInfos;
        }
        return startedPlugins.stream()
                .filter(pluginWrapper -> pluginWrapper != null)
                .map(pw -> {
                    return new PluginInfo(pw.getDescriptor(), pw.getPluginState(),
                            pw.getPluginPath().toAbsolutePath().toString());
                })
                .collect(Collectors.toList());
    }


    @Override
    public Set<String> getPluginFilePaths() throws Exception {
        RuntimeMode environment = integrationConfiguration.environment();
        Set<String> paths = new HashSet<>();
        if(environment == RuntimeMode.DEVELOPMENT){
            paths.add(integrationConfiguration.pluginPath());
            return paths;
        }
        List<File> files = org.pf4j.util.FileUtils.getJars(Paths.get(integrationConfiguration.pluginPath()));
        return files.stream()
                .filter(file -> file != null)
                .map(file -> file.getAbsolutePath())
                .collect(Collectors.toSet());
    }

    @Override
    public List<PluginWrapper> getPluginWrapper() {
        return pluginManager.getPlugins();
    }

    /**
     * 上传插件
     * @param pluginFile 插件文件
     * @return 返回上传的插件路径
     * @throws Exception 异常信息
     */
    private Path uploadPlugin(MultipartFile pluginFile) throws Exception {
        if(pluginFile == null){
            throw new IllegalArgumentException("Method:uploadPlugin param 'pluginFile' can not be null");
        }
        // 获取文件的后缀名
        String fileName = pluginFile.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf(".") + 1);
        //检查文件格式是否合法
        if(StringUtils.isEmpty(suffixName)){
            throw new IllegalArgumentException("Invalid file type, please select .jar or .zip file");
        }
        if(!"jar".equalsIgnoreCase(suffixName) && !"zip".equalsIgnoreCase(suffixName)){
            throw new IllegalArgumentException("Invalid file type, please select .jar or .zip file");
        }
        String tempPathString = integrationConfiguration.uploadTempPath() + File.separator + fileName;
        Path tempPath = PluginFileUtils.getExistPath(Paths.get(tempPathString));
        File tempFile = tempPath.toFile();
        FileUtils.writeByteArrayToFile(tempFile, pluginFile.getBytes());
        try {
            Path verifyPath = uploadPluginVerify.verify(tempPath);
            if(verifyPath != null){
                String pluginFilePathString = pluginManager.getPluginsRoot().toString() +
                        File.separator + fileName;
                Path pluginFilePath = Paths.get(pluginFilePathString);
                File target = pluginFilePath.toFile();
                if(target.exists()){
                    // 存在则拷贝一份
                    backup(pluginFilePath, "uploadPlugin", 2);
                }
                FileUtils.copyFile(verifyPath.toFile(), target);
                // 删除临时文件
                tempFile.deleteOnExit();
                return pluginFilePath;
            } else {
                Exception exception =
                        new Exception(fileName + " verify failure, verifyPath is null");
                verifyFailureDelete(tempPath, exception);
                throw exception;
            }
        } catch (Exception e){
            // 出现异常, 删除刚才上传的临时文件
            verifyFailureDelete(tempPath, e);
            throw e;
        }
    }


    /**
     * 得到插件包装类
     * @param pluginId 插件id
     * @return PluginWrapper
     * @throws Exception 插件装配异常
     */
    private PluginWrapper getPluginWrapper(String pluginId, String errorMsg) throws Exception {
        PluginWrapper pluginWrapper = pluginManager.getPlugin(pluginId);
        if (pluginWrapper == null) {
            throw new Exception(errorMsg + " -> Not found plugin " + pluginId);
        }
        return pluginWrapper;
    }



    /**
     * 校验文件失败后, 删除临时文件
     * @param tempPluginFile 临时文件路径
     * @param e 异常信息
     * @throws Exception Exception
     */
    private void verifyFailureDelete(Path tempPluginFile, Exception e) throws Exception {
        try {
            Files.deleteIfExists(tempPluginFile);
        }catch (IOException e1){
            throw new Exception("Verify failure and delete temp file failure : " + e.getMessage(), e);
        }
    }

    /**
     * 备份
     * @param sourcePath 源文件的路径
     * @param appendName 追加的字符串
     * @param type 类型 1移动 2拷贝
     * @return 结果
     * @throws Exception Exception
     */
    private boolean backup(Path sourcePath, String appendName, int type) throws Exception {
        if(isDev()){
            // 如果是开发环境, 则不进行备份
            return true;
        }
        if(!Files.exists(sourcePath)){
            throw new FileNotFoundException("path ' " + sourcePath.toString() + "'  does not exist!");
        }
        try {
            String fileName = sourcePath.getFileName().toString();
            String targetName = integrationConfiguration.backupPath() + File.separator + getNowTimeByFormat();
            if(!StringUtils.isEmpty(appendName)){
                targetName = targetName + "_" + appendName;
            }
            Path target = Paths.get(targetName + "_" + fileName);
            if(!Files.exists(target.getParent())){
                Files.createDirectories(target.getParent());
            }
            File targetFile = target.toFile();
            File sourceFile = sourcePath.toFile();
            if(sourceFile.length() == 0){
                // 源文件字节为0, 说明为删除的插件。不需要备份
                return true;
            }
            FileUtils.writeByteArrayToFile(targetFile, FileUtils.readFileToByteArray(sourceFile));
            if(type == 1){
                FileUtils.writeByteArrayToFile(sourceFile, "".getBytes());
            }
            return true;
        } catch (IOException e) {
            throw new Exception("Backup plugin jar '" + sourcePath.toString() +  "' failure : " + e.getMessage(), e);
        }
    }

    /**
     * 获取现在的时间
     * @return String
     */
    private String getNowTimeByFormat(){
        LocalDateTime localDateTime = LocalDateTime.now();
        return dateTimeFormatter.format(localDateTime);
    }

    /**
     * 是否是开发环境
     * @return bolean
     */
    private boolean isDev(){
        return integrationConfiguration.environment() == RuntimeMode.DEVELOPMENT;
    }

}
