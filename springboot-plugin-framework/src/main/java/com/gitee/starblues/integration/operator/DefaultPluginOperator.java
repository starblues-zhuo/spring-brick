package com.gitee.starblues.integration.operator;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.listener.PluginInitializerListenerFactory;
import com.gitee.starblues.integration.listener.PluginListenerFactory;
import com.gitee.starblues.integration.operator.module.PluginInfo;
import com.gitee.starblues.integration.operator.verify.PluginLegalVerify;
import com.gitee.starblues.integration.operator.verify.DefaultPluginVerify;
import com.gitee.starblues.factory.DefaultPluginFactory;
import com.gitee.starblues.factory.PluginFactory;
import com.gitee.starblues.utils.GlobalRegistryInfo;
import com.gitee.starblues.utils.PluginOperatorInfo;
import com.gitee.starblues.utils.PluginFileUtils;
import org.pf4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 默认的插件操作者
 * @author starBlues
 * @version 2.4.0
 */
public class DefaultPluginOperator implements PluginOperator {

    private boolean isInit = false;
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    protected final GenericApplicationContext applicationContext;
    protected final IntegrationConfiguration integrationConfiguration;
    protected final PluginManager pluginManager;
    protected final PluginFactory pluginFactory;
    protected final PluginInitializerListenerFactory pluginInitializerListenerFactory;

    protected PluginLegalVerify pluginLegalVerify;


    public DefaultPluginOperator(ApplicationContext applicationContext,
                                 IntegrationConfiguration integrationConfiguration,
                                 PluginManager pluginManager,
                                 PluginListenerFactory pluginListenerFactory) {
        Objects.requireNonNull(integrationConfiguration, "applicationContext can't be null");
        Objects.requireNonNull(integrationConfiguration, "IntegrationConfiguration can't be null");
        Objects.requireNonNull(pluginManager, "PluginManager can't be null");
        this.applicationContext = (GenericApplicationContext) applicationContext;
        this.integrationConfiguration = integrationConfiguration;
        this.pluginManager = pluginManager;
        this.pluginFactory = new DefaultPluginFactory(applicationContext, pluginListenerFactory);
        this.pluginInitializerListenerFactory = new PluginInitializerListenerFactory(applicationContext);

        this.pluginLegalVerify = new DefaultPluginVerify(pluginManager);
    }

    /**
     * 设置插件校验器
     * @param uploadPluginVerify uploadPluginVerify
     */
    public void setUploadPluginVerify(PluginLegalVerify uploadPluginVerify) {
        if(uploadPluginVerify != null){
            this.pluginLegalVerify = uploadPluginVerify;
        }
    }

    @Override
    public synchronized boolean initPlugins(PluginInitializerListener pluginInitializerListener) throws Exception {
        if(isInit){
            throw new RuntimeException("Plugins Already initialized. Cannot be initialized again");
        }
        try {
            pluginInitializerListenerFactory.addPluginInitializerListeners(pluginInitializerListener);
            log.info("Plugins start initialize of root path '{}'", pluginManager.getPluginsRoot().toString());
            // 触发插件初始化监听器
            pluginInitializerListenerFactory.before();
            if(!integrationConfiguration.enable()){
                // 如果禁用的话, 直接返回
                pluginInitializerListenerFactory.complete();
                return false;
            }

            // 启动前, 清除空文件
            PluginFileUtils.cleanEmptyFile(pluginManager.getPluginsRoot());

            // 开始初始化插件工厂
            pluginFactory.initialize();
            // 开始加载插件
            pluginManager.loadPlugins();
            pluginManager.startPlugins();
            List<PluginWrapper> pluginWrappers = pluginManager.getStartedPlugins();
            if(pluginWrappers == null || pluginWrappers.isEmpty()){
                log.warn("Not found plugin!");
                return false;
            }
            boolean isFoundException = false;
            for (PluginWrapper pluginWrapper : pluginWrappers) {
                String pluginId = pluginWrapper.getPluginId();
                GlobalRegistryInfo.addOperatorPluginInfo(pluginId,
                        PluginOperatorInfo.OperatorType.INSTALL, false);
                try {
                    // 依次注册插件信息到Spring boot
                    pluginFactory.registry(PluginRegistryInfo.build(pluginWrapper, pluginManager,
                            applicationContext, true));
                } catch (Exception e){
                    log.error("Plugin '{}' registry failure. Reason : {}", pluginId, e.getMessage(), e);
                    isFoundException = true;
                }
            }
            pluginFactory.build();
            isInit = true;
            if(isFoundException){
                log.error("Plugins initialize failure");
                return false;
            } else {
                log.info("Plugins initialize success");
                pluginInitializerListenerFactory.complete();
                return true;
            }
        }  catch (Exception e){
            pluginInitializerListenerFactory.failure(e);
            throw e;
        }
    }

    @Override
    public boolean verify(Path jarPath) throws Exception {
        pluginLegalVerify.verify(jarPath);
        return true;
    }


    @Override
    public synchronized boolean install(Path jarPath) throws Exception {
        if(isDev()){
            throw new RuntimeException("Plugin cannot be installed in 'dev' environment");
        }
        if(jarPath == null){
            throw new IllegalArgumentException("Method:install param 'pluginId' can not be empty");
        }
        String pluginId = null;
        try {
            if(!Files.exists(jarPath)){
                throw new FileNotFoundException("Not found this path " + jarPath);
            }
            // 校验插件文件
            pluginLegalVerify.verify(jarPath);
            Path pluginsRoot = pluginManager.getPluginsRoot();
            if(jarPath.getParent().compareTo(pluginsRoot) == 0){
                // 说明该插件文件存在于插件root目录下。直接加载该插件
                pluginId = pluginManager.loadPlugin(jarPath);
            } else {
                File sourceFile = jarPath.toFile();
                String targetPathString = pluginsRoot.toString() + File.separator +
                        sourceFile.getName();
                Path targetPath = Paths.get(targetPathString);
                if(Files.exists(targetPath)){
                    // 如果存在该文件, 则移动备份
                    backup(targetPath, "install-backup", 1);
                }
                PluginFileUtils.createExistFile(targetPath);
                Files.copy(jarPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                pluginId = pluginManager.loadPlugin(targetPath);
            }
            if(StringUtils.isEmpty(pluginId)){
                log.error("Plugin '{}' install failure, this plugin id is empty.", pluginId);
                return false;
            }
            GlobalRegistryInfo.addOperatorPluginInfo(pluginId, PluginOperatorInfo.OperatorType.INSTALL, true);
            if(start(pluginId)){
                log.info("Plugin '{}' install success", pluginId);
                return true;
            } else {
                try {
                    uninstall(pluginId, false);
                } catch (Exception uninstallException){
                    log.error("Plugin '{}' uninstall failure. {}", pluginId, uninstallException.getMessage());
                }
                return false;
            }
        } catch (Exception e){
            // 说明load成功, 但是没有启动成功, 则卸载该插件
            log.error("Plugin '{}' install failure. {}", pluginId, e.getMessage());
            if(!StringUtils.isEmpty(pluginId)){
                try {
                    uninstall(pluginId, false);
                } catch (Exception uninstallException){
                    log.error("Plugin '{}' uninstall failure. {}", pluginId, uninstallException.getMessage());
                }
            }
            throw e;
        } finally {
            if(!StringUtils.isEmpty(pluginId)){
                GlobalRegistryInfo.setOperatorPluginInfo(pluginId, false);
            }
        }
    }


    @Override
    public synchronized boolean uninstall(String pluginId, boolean isBackup) throws Exception {
        if(isDev()){
            throw new RuntimeException("Plugin cannot be uninstalled in 'dev' environment");
        }
        if(StringUtils.isEmpty(pluginId)){
            throw new IllegalArgumentException("Method:uninstall param 'pluginId' can not be empty");
        }
        PluginWrapper pluginWrapper = pluginManager.getPlugin(pluginId);
        if(pluginWrapper == null){
            throw new Exception("Plugin uninstall failure, Not found plugin '" + pluginId + "'");
        }

        Exception exception = null;
        if(pluginWrapper.getPluginState() == PluginState.STARTED){
            try {
                pluginFactory.unRegistry(pluginId);
                pluginFactory.build();
            } catch (Exception e){
                log.error("Plugin '{}' uninstall failure, {}", pluginId, e.getMessage());
                exception = e;
            }
        }

        try {
            if (pluginManager.unloadPlugin(pluginId)) {
                Path pluginPath = pluginWrapper.getPluginPath();
                boolean opPluginFile;
                if(isBackup){
                    // 将插件文件移到备份文件中
                    opPluginFile = backup(pluginPath, "uninstall", 1);
                } else {
                    // 不备份的话。直接删除该文件
                    opPluginFile = Files.deleteIfExists(pluginPath);
                }
                if(opPluginFile){
                    log.info("Plugin '{}' uninstall success", pluginId);
                } else {
                    log.error("Plugin '{}' uninstall failure. process plugin file failure", pluginId);
                }
                return opPluginFile;
            } else {
                log.error("Plugin '{}' uninstall failure", pluginId);
                return false;
            }
        } catch (Exception e){
            if(exception != null){
                exception.printStackTrace();
            }
            log.error("Plugin '{}' uninstall failure. {}", pluginId, e.getMessage());
            throw e;
        }
    }

    @Override
    public synchronized boolean start(String pluginId) throws Exception {
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
                pluginFactory.registry(PluginRegistryInfo.build(pluginWrapper, pluginManager,
                        applicationContext,false));
                pluginFactory.build();
                log.info("Plugin '{}' start success", pluginId);
                return true;
            }
            log.error("Plugin '{}' start failure, plugin state is not start. Current plugin state is '{}'",
                    pluginId, pluginState.toString());

        } catch (Exception e){
            log.error("Plugin '{}' start failure. {}", pluginId, e.getMessage(), e);
            log.info("Start stop plugin '{}'", pluginId);
            try {
                stop(pluginId);
            } catch (Exception stopException){
                log.error("Plugin '{}' stop failure. {}", pluginId, e.getMessage());
            }
        }
        return false;
    }

    @Override
    public synchronized boolean stop(String pluginId) throws Exception {
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
            log.info("Plugin '{}' unRegistry success", pluginId);
        } catch (Exception e){
            log.error("Plugin '{}' stop failure. {}", pluginId, e.getMessage(), e);
        }
        try {
            pluginManager.stopPlugin(pluginId);
            log.info("Plugin '{}' stop success", pluginId);
            return true;
        } catch (Exception e){
            log.error("Plugin '{}' stop failure. {}", pluginId, e.getMessage());
            throw e;
        }
    }


    @Override
    public synchronized boolean uploadPluginAndStart(MultipartFile pluginFile) throws Exception {
        if(isDev()){
            throw new RuntimeException("Plugin cannot uploadPluginAndStart in the 'dev' environment");
        }
        if(pluginFile == null){
            throw new IllegalArgumentException("Method:uploadPluginAndStart param 'pluginFile' can not be null");
        }
        Path path = uploadPlugin(pluginFile);
        if(this.install(path)){
            log.info("Plugin upload and start success");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public synchronized boolean installConfigFile(Path configFilePath) throws Exception {
        if(isDev()){
            throw new RuntimeException("Plugin config file cannot be installed in the 'dev' environment");
        }
        if(!Files.exists(configFilePath)){
            throw new FileNotFoundException("configFilePath '" + configFilePath + "'  does not exist!");
        }
        File sourceFile = configFilePath.toFile();
        String configPath = integrationConfiguration.pluginConfigFilePath() +
                File.separator + sourceFile.getName();
        Path targetPath = PluginFileUtils.createExistFile(Paths.get(configPath));
        if(Files.exists(targetPath)){
            // 如果文件存在, 则移动备份
            backup(targetPath, "install-config-backup",1);
        }
        Files.copy(configFilePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        return true;
    }

    @Override
    public synchronized boolean uploadConfigFile(MultipartFile configFile) throws Exception {
        if(isDev()){
            throw new RuntimeException("Plugin config file cannot be uploaded in the 'dev' environment");
        }
        if(configFile == null){
            throw new IllegalArgumentException("Method:uploadConfigFile param 'configFile' can not be null");
        }
        String fileName = configFile.getOriginalFilename();
        String configPath = integrationConfiguration.pluginConfigFilePath() +
                File.separator + fileName;
        Path targetPath = PluginFileUtils.createExistFile(Paths.get(configPath));
        if(Files.exists(targetPath)){
            // 如果文件存在, 则拷贝备份
            backup(targetPath, "upload-config-backup",2);
        }
        // 然后写入数据到该文件
        Files.write(targetPath, configFile.getBytes());
        return true;
    }

    @Override
    public synchronized boolean backupPlugin(Path backDirPath, String sign) throws Exception {
        if(isDev()){
            throw new RuntimeException("Plugin cannot backup in the 'dev' environment");
        }
        Objects.requireNonNull(backDirPath);
        return backup(backDirPath, sign, 2);
    }


    @Override
    public synchronized boolean backupPlugin(String pluginId, String sign) throws Exception {
        if(isDev()){
            throw new RuntimeException("Plugin cannot backup in the 'dev' environment");
        }
        PluginWrapper pluginManager = getPluginWrapper(pluginId, "BackupPlugin by pluginId");
        return backupPlugin(pluginManager.getPluginPath(), sign);
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
                    return getPluginInfo(pw);
                })
                .collect(Collectors.toList());
    }

    @Override
    public PluginInfo getPluginInfo(String pluginId) {
        PluginWrapper pluginWrapper = pluginManager.getPlugin(pluginId);
        if(pluginWrapper == null){
            log.warn("Not found plugin '{}'", pluginId);
            return null;
        }
        return getPluginInfo(pluginWrapper);
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

    @Override
    public PluginWrapper getPluginWrapper(String pluginId) {
        return pluginManager.getPlugin(pluginId);
    }

    /**
     * 上传插件
     * @param pluginFile 插件文件
     * @return 返回上传的插件路径
     * @throws Exception 异常信息
     */
    protected Path uploadPlugin(MultipartFile pluginFile) throws Exception {
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
        Path tempPath = PluginFileUtils.createExistFile(Paths.get(tempPathString));
        Files.write(tempPath, pluginFile.getBytes());
        try {
            Path verifyPath = pluginLegalVerify.verify(tempPath);
            if(verifyPath != null){
                String targetPathString = pluginManager.getPluginsRoot().toString() +
                        File.separator + fileName;
                Path targetPluginPath = Paths.get(targetPathString);
                if(Files.exists(targetPluginPath)){
                    // 存在则拷贝一份
                    backup(targetPluginPath, "upload", 2);
                }
                // 拷贝校验的路径到插件路径下
                Files.copy(verifyPath, targetPluginPath, StandardCopyOption.REPLACE_EXISTING);
                // 删除临时文件
                Files.deleteIfExists(tempPath);
                return targetPluginPath;
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
     * @param errorMsg 错误信息
     * @return PluginWrapper
     * @throws Exception 插件装配异常
     */
    protected PluginWrapper getPluginWrapper(String pluginId, String errorMsg) throws Exception {
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
    protected void verifyFailureDelete(Path tempPluginFile, Exception e) throws Exception {
        try {
            Files.deleteIfExists(tempPluginFile);
        }catch (IOException e1){
            throw new Exception("Verify failure and delete temp file failure : " + e.getMessage(), e);
        }
    }

    /**
     * 备份
     * @param sourcePath 源文件的路径
     * @param sign 文件标志
     * @param type 类型 1移动 2拷贝
     * @return 结果
     */
    protected boolean backup(Path sourcePath, String sign, int type) {
        try {
            if(isDev()){
                // 如果是开发环境, 则不进行备份
                return false;
            }
            if(sourcePath == null){
                return false;
            }
            if(!Files.exists(sourcePath)){
                log.error("Path '{}' does not exist", sourcePath.toString());
                return false;
            }
            String fileName = sourcePath.getFileName().toString();
            String targetName = integrationConfiguration.backupPath() + File.separator;
            if(!StringUtils.isEmpty(sign)){
                targetName = targetName + "[" + sign + "]";
            }
            targetName = targetName + "[" + getNowTimeByFormat() + "]";
            Path target = Paths.get(targetName + "_" + fileName);
            if(!Files.exists(target.getParent())){
                Files.createDirectories(target.getParent());
            }
            File sourceFile = sourcePath.toFile();
            if(sourceFile.length() == 0){
                // 源文件字节为0, 说明为删除的插件。不需要备份
                return true;
            }
            if(type == 1){
                // 是移动的话, 则删除源文件
                Files.move(sourcePath, target, StandardCopyOption.REPLACE_EXISTING);
            } else {
                // 拷贝
                Files.copy(sourcePath, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException e) {
            log.error("Backup plugin jar '{}' failure. {}", sourcePath.toString(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取现在的时间
     * @return String
     */
    protected String getNowTimeByFormat(){
        LocalDateTime localDateTime = LocalDateTime.now();
        return FORMAT.format(localDateTime);
    }

    /**
     * 是否是开发环境
     * @return bolean
     */
    protected boolean isDev(){
        return integrationConfiguration.environment() == RuntimeMode.DEVELOPMENT;
    }

    /**
     * 通过PluginWrapper得到插件信息
     * @param pluginWrapper pluginWrapper
     * @return PluginInfo
     */
    private PluginInfo getPluginInfo(PluginWrapper pluginWrapper) {
        return new PluginInfo(pluginWrapper.getDescriptor(), pluginWrapper.getPluginState(),
                pluginWrapper.getPluginPath().toAbsolutePath().toString(),
                pluginManager.getRuntimeMode().toString());
    }



}
