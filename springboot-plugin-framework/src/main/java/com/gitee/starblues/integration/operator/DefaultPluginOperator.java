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
import java.nio.file.StandardCopyOption;
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
            log.info("Start initialize plugins");
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
    public String loadPlugin(Path path) throws Exception {
        return pluginManager.loadPlugin(path);
    }

    @Override
    public boolean install(Path path) throws Exception {
        if(path == null){
            throw new IllegalArgumentException("Method:install param [pluginId] can not be empty");
        }
        String pluginId = null;
        try {
            pluginId = pluginManager.loadPlugin(path);
            return start(pluginId);
        } catch (Exception e){
            // 说明load成功, 但是没有启动成功, 则卸载该插件
            uninstall(pluginId);
            throw e;
        }
    }

    @Override
    public boolean uninstall(String pluginId) throws Exception {
        PluginWrapper pluginWrapper = pluginManager.getPlugin(pluginId);
        if(pluginWrapper == null){
            log.error("Uninstall Plugin failure, Not found plugin {}", pluginId);
            return false;
        }
        try {
            pluginFactory.unRegistry(pluginId);
            pluginFactory.build();
            return true;
        } catch (Exception e){
            throw new Exception("Stop plugin [" + pluginId + "] failure. " + e.getMessage() ,e);
        } finally {
            if (pluginManager.unloadPlugin(pluginId)) {
                // 卸载完后，将插件文件移到备份文件中
                backup(pluginWrapper.getPluginPath(), "uninstallPlugin", 1);
                log.info("Unload Plugin [{}] success", pluginId);
                log.info("Uninstall Plugin [{}] success", pluginId);
            } else {
                log.info("Unload Plugin [{}] failure", pluginId);
                log.info("Uninstall Plugin [{}] failure", pluginId);
            }
        }
    }

    @Override
    public boolean delete(String pluginId) throws Exception {
        PluginWrapper pluginWrapper = pluginManager.getPlugin(pluginId);
        if(pluginWrapper == null){
            log.error("Delete -> Not Found plugin [{}]", pluginId);
            return false;
        }
        if(pluginWrapper.getPluginState() == PluginState.STARTED){
            uninstall(pluginId);
        }
        backup(pluginWrapper.getPluginPath(), "deleteByPluginId", 1);
        log.info("Delete plugin [{}] Success", pluginId);
        return true;
    }

    @Override
    public boolean delete(Path path) throws Exception {
        try {
            if(!Files.exists(path)){
                throw new FileNotFoundException(path.toString() + "  does not exist!");
            }
            PluginDescriptor pluginDescriptor = pluginDescriptorFinder.find(path);
            PluginWrapper pluginWrapper = pluginManager.getPlugin(pluginDescriptor.getPluginId());
            if(pluginWrapper != null){
                return delete(pluginWrapper.getPluginId());
            } else {
                log.error("Not found Plugin [{}] of path {}", pluginDescriptor.getPluginId(), path.toString());
                return false;
            }
        } finally {
            backup(path, "deleteByPath", 2);
        }
    }


    @Override
    public boolean start(String pluginId) throws Exception {
        if(StringUtils.isEmpty(pluginId)){
            throw new IllegalArgumentException("Method:start param [pluginId] can not be empty");
        }
        PluginWrapper pluginWrapper = getPluginWrapper(pluginId, "Start");
        if(pluginWrapper.getPluginState() == PluginState.STARTED){
            throw new Exception("This plugin [" + pluginId + "] have already started");
        }
        PluginState pluginState = pluginManager.startPlugin(pluginId);
        if(pluginState == PluginState.STARTED){
            pluginFactory.registry(pluginWrapper);
            pluginFactory.build();
            log.info("Start Plugin [{}] success", pluginId);
            return true;
        }
        log.error("Start Plugin [{}] failure, plugin state is not start. State[{}]", pluginId, pluginState.toString());
        return false;
    }

    @Override
    public boolean stop(String pluginId) throws Exception {
        if(StringUtils.isEmpty(pluginId)){
            throw new IllegalArgumentException("Method:stop param [pluginId] can not be empty");
        }
        PluginWrapper pluginWrapper = getPluginWrapper(pluginId, "Stop");
        if(pluginWrapper.getPluginState() != PluginState.STARTED){
            throw new Exception("This plugin [" + pluginId + "] is not started");
        }
        try {
            pluginFactory.unRegistry(pluginId);
            pluginFactory.build();
            return true;
        } catch (Exception e){
            throw new Exception("Stop plugin [" + pluginId + "] failure. " + e.getMessage() ,e);
        } finally {
            pluginManager.stopPlugin(pluginId);
        }
    }




    @Override
    public Path uploadPlugin(MultipartFile pluginFile) throws Exception {
        if(pluginFile == null){
            throw new IllegalArgumentException("Method:uploadPlugin param [pluginFile] can not be null");
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
        String tempPath = integrationConfiguration.uploadTempPath() + File.separator + fileName;
        Path srcPath = PluginFileUtils.getExistPath(Paths.get(tempPath));
        Path tempPluginFile = Files.write(srcPath, pluginFile.getBytes());

        try {
            Path verifyPath = uploadPluginVerify.verify(tempPluginFile);
            if(verifyPath != null){
                String pluginFilePathString = pluginManager.getPluginsRoot().toString() +
                        File.separator + fileName;
                Path pluginFilePath = Paths.get(pluginFilePathString);
                File target = pluginFilePath.toFile();
                if(target.exists()){
                    // 存在则拷贝一份
                    backup(pluginFilePath, "uploadPlugin", 2);
                }
                FileUtils.writeByteArrayToFile(target, FileUtils.readFileToByteArray(verifyPath.toFile()));
                return pluginFilePath;
            } else {
                Exception exception =
                        new Exception(fileName + " verify failure, verifyPath is null");
                verifyFailureDelete(tempPluginFile, exception);
                throw exception;
            }
        } catch (Exception e){
            // 出现异常, 删除刚才上传的临时文件
            verifyFailureDelete(tempPluginFile, e);
            throw new Exception("Verify failure : " + e.getMessage(), e);
        }
    }


    @Override
    public boolean uploadPluginAndStart(MultipartFile pluginFile) throws Exception {
        if(pluginFile == null){
            throw new Exception("Method:uploadPluginAndStart param [pluginFile] can not be null");
        }
        Path path = uploadPlugin(pluginFile);
        this.install(path);
        log.info("Upload And Start Plugin Success. [{}]",  path.toString());
        return true;
    }

    @Override
    public boolean uploadConfigFile(MultipartFile configFile) throws Exception {
        if(configFile == null){
            throw new IllegalArgumentException("Method:uploadConfigFile param [configFile] can not be null");
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
        try {
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
        } catch (Exception e){
            throw new Exception(e);
        }
    }

    @Override
    public List<PluginWrapper> getPluginWrapper() {
        return pluginManager.getPlugins();
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
        if(!Files.exists(sourcePath)){
            throw new FileNotFoundException(sourcePath.toString() + "  does not exist!");
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
            FileUtils.writeByteArrayToFile(targetFile, FileUtils.readFileToByteArray(sourceFile));
            if(type == 1){
                FileUtils.writeByteArrayToFile(sourceFile, "".getBytes());
            }
            return true;
        } catch (IOException e) {
            throw new Exception("BackupPlugin " + sourcePath.toString() +  " failure : " + e.getMessage(), e);
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



}
