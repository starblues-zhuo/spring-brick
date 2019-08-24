package com.gitee.starblues.integration.operator;

import com.gitee.starblues.exception.PluginPlugException;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.listener.PluginInitializerListenerFactory;
import com.gitee.starblues.integration.listener.PluginListenerFactory;
import com.gitee.starblues.integration.operator.module.PluginInfo;
import com.gitee.starblues.integration.operator.verify.PluginLegalVerify;
import com.gitee.starblues.integration.operator.verify.PluginUploadVerify;
import com.gitee.starblues.register.DefaultPluginFactory;
import com.gitee.starblues.register.PluginFactory;
import org.pf4j.*;
import org.pf4j.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
 * @version 1.0
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
    public synchronized boolean initPlugins(PluginInitializerListener pluginInitializerListener) throws PluginPlugException {
        if(isInit){
            throw new PluginPlugException("Plugins Already initialized. Cannot be initialized again");
        }
        try {
            pluginInitializerListenerFactory.addPluginInitializerListeners(pluginInitializerListener);
            log.info("Start initialize plugins");
            pluginInitializerListenerFactory.before();
            pluginManager.loadPlugins();
            pluginManager.startPlugins();
            List<PluginWrapper> pluginWrappers = pluginManager.getStartedPlugins();
            if(pluginWrappers == null){
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
            throw new PluginPlugException(e);
        }
    }

    @Override
    public String loadPlugin(Path path) throws PluginPlugException {
        try {
            return pluginManager.loadPlugin(path);
        } catch (Exception e){
            throw new PluginPlugException(e);
        }
    }

    @Override
    public boolean install(Path path) throws PluginPlugException {
        if(path == null){
            throw new IllegalArgumentException("Method:install param <pluginId> can not be empty");
        }
        String pluginId = null;
        try {
            pluginId = pluginManager.loadPlugin(path);
            return start(pluginId);
        } catch (Exception e){
            if(pluginId != null){
                // 说明load成功, 但是没有启动成功, 则卸载该插件
                pluginManager.unloadPlugin(pluginId);
                try {
                    backup(path, "installErrorPlugin", 2);
                } catch (Exception e1) {
                    throw new PluginPlugException(e1);
                }
            }
            throw new PluginPlugException(e);
        }
    }

    @Override
    public boolean uninstall(String pluginId) throws PluginPlugException {
        try {
            stop(pluginId);
            if(pluginManager.unloadPlugin(pluginId)){
                log.info("Uninstall Plugin {} Success", pluginId);
                return true;
            } else {
                log.info("Uninstall Plugin {} failure", pluginId);
                return false;
            }
        } catch (Exception e){
            throw new PluginPlugException(e);
        }
    }

    @Override
    public boolean delete(String pluginId) throws PluginPlugException {
        PluginWrapper pluginWrapper = pluginManager.getPlugin(pluginId);
        if(pluginWrapper == null){
            log.error("Delete -> Not Found plugin {}", pluginId);
            return false;
        }
        if(pluginWrapper.getPluginState() == PluginState.STARTED){
            uninstall(pluginId);
        }
        backup(pluginWrapper.getPluginPath(), "deleteByPluginId", 2);
        log.info("Delete plugin {} Success", pluginId);
        return true;
    }

    @Override
    public boolean delete(Path path) throws PluginPlugException {
        try {
            if(!Files.exists(path)){
                throw new PluginPlugException(path.toString() + "  does not exist!");
            }
            PluginDescriptor pluginDescriptor = pluginDescriptorFinder.find(path);
            PluginWrapper pluginWrapper = pluginManager.getPlugin(pluginDescriptor.getPluginId());
            if(pluginWrapper != null){
                return delete(pluginWrapper.getPluginId());
            } else {
                log.error("Not found Plugin {} of path {}", pluginDescriptor.getPluginId(), path.toString());
                return false;
            }
        } catch (PluginException e) {
            throw new PluginPlugException(e);
        } finally {
            backup(path, "deleteByPath", 2);
        }
    }


    @Override
    public boolean start(String pluginId) throws PluginPlugException {
        if(StringUtils.isEmpty(pluginId)){
            throw new IllegalArgumentException("Method:start param <pluginId> can not be empty");
        }
        PluginWrapper pluginWrapper = getPluginWrapper(pluginId, "Start");
        if(pluginWrapper.getPluginState() == PluginState.STARTED){
            throw new PluginPlugException("This plugin <" + pluginId + "> is not stopped");
        }
        try {
            PluginState pluginState = pluginManager.startPlugin(pluginId);
            if(pluginState == PluginState.STARTED){
                pluginFactory.registry(pluginWrapper);
                pluginFactory.build();
                log.info("Start Plugin {} Success", pluginId);
                return true;
            }
            log.error("Start Plugin {} Failure, plugin state is not start <{}>", pluginId, pluginState.toString());
            return false;
        } catch (Exception e){
            throw new PluginPlugException("Start plugin <" + pluginId + "> failure. " + e.getMessage() ,e);
        }
    }

    @Override
    public boolean stop(String pluginId) throws PluginPlugException {
        if(StringUtils.isEmpty(pluginId)){
            throw new IllegalArgumentException("Method:stop param <pluginId> can not be empty");
        }
        PluginWrapper pluginWrapper = getPluginWrapper(pluginId, "Stop");
        if(pluginWrapper.getPluginState() != PluginState.STARTED){
            throw new PluginPlugException("This plugin <" + pluginId + "> is not running");
        }
        try {
            pluginFactory.unRegistry(pluginId);
            log.info("Stop Plugin {} Success", pluginId);
            return true;
        } catch (Exception e){
            throw new PluginPlugException("Stop plugin <" + pluginId + "> failure. " + e.getMessage() ,e);
        } finally {
            pluginManager.stopPlugin(pluginId);
        }
    }




    @Override
    public Path uploadPlugin(MultipartFile pluginFile) throws PluginPlugException {
        if(pluginFile == null){
            throw new IllegalArgumentException("Method:uploadPlugin param <pluginFile> can not be null");
        }
        try {
            // 获取文件的后缀名
            String fileName = pluginFile.getOriginalFilename();
            String suffixName = fileName.substring(fileName.lastIndexOf(".") + 1);
            //检查文件格式是否合法
            if(StringUtils.isEmpty(suffixName)){
                throw new IllegalArgumentException("Invalid file type,please select .jar or .zip file");
            }
            if(!"jar".equalsIgnoreCase(suffixName) && !"zip".equalsIgnoreCase(suffixName)){
                throw new IllegalArgumentException("Invalid file type,please select .jar or .zip file");
            }
            String tempPath = integrationConfiguration.uploadTempPath() + File.separator + fileName;
            Path tempPluginFile = Files.write(getExistFile(Paths.get(tempPath)), pluginFile.getBytes());
            try {
                Path verifyPath = uploadPluginVerify.verify(tempPluginFile);
                if(verifyPath != null){
                    String pluginFilePathString = pluginManager.getPluginsRoot().toString() +
                            File.separator + fileName;
                    Path pluginFilePath = Paths.get(pluginFilePathString);
                    if(Files.exists(pluginFilePath)){
                        // 如果存在同名插件的化, 先备份它
                        backup(pluginFilePath, "uploadPluginFile", 1);
                    }
                    return Files.move(verifyPath, pluginFilePath);
                } else {
                    PluginPlugException pluginPlugException =
                            new PluginPlugException(fileName + " verify failure, verifyPath is null");
                    verifyFailureDelete(tempPluginFile, pluginPlugException);
                    throw pluginPlugException;
                }
            } catch (Exception e){
                // 出现异常, 删除刚才上传的临时文件
                verifyFailureDelete(tempPluginFile, e);
                throw new PluginPlugException("Verify failure : " + e.getMessage(), e);
            }
        } catch (Exception e){
            throw new PluginPlugException(e);
        }
    }


    @Override
    public boolean uploadPluginAndStart(MultipartFile pluginFile) throws PluginPlugException {
        if(pluginFile == null){
            throw new PluginPlugException("Method:uploadPluginAndStart param <pluginFile> can not be null");
        }
        try {
            Path path = uploadPlugin(pluginFile);
            this.install(path);
            log.info("Upload And Start Plugin {} Success. ",  path.toString());
            return true;
        } catch (Exception e){
            throw new PluginPlugException(e);
        }
    }

    @Override
    public boolean uploadConfigFile(MultipartFile configFile) throws PluginPlugException {
        if(configFile == null){
            throw new IllegalArgumentException("Method:uploadConfigFile param<configFile> can not be null");
        }
        try {
            String fileName = configFile.getOriginalFilename();
            String configPath = integrationConfiguration.pluginConfigFilePath() +
                    File.separator + fileName;
            Files.write(getExistFile(Paths.get(configPath)), configFile.getBytes());
            return true;
        } catch (Exception e){
            throw new PluginPlugException(e);
        }
    }

    @Override
    public boolean backupPlugin(Path path, String appendName) throws PluginPlugException {
        Objects.requireNonNull(path);
        return backup(path, appendName, 2);
    }



    @Override
    public boolean backupPlugin(String pluginId, String appendName) throws PluginPlugException {
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
    public Set<String> getPluginFilePaths() throws PluginPlugException {
        try {
            RuntimeMode environment = integrationConfiguration.environment();
            Set<String> paths = new HashSet<>();
            if(environment == RuntimeMode.DEVELOPMENT){
                paths.add(integrationConfiguration.pluginPath());
                return paths;
            }
            List<File> files = FileUtils.getJars(Paths.get(integrationConfiguration.pluginPath()));
            return files.stream()
                    .filter(file -> file != null)
                    .map(file -> file.getAbsolutePath())
                    .collect(Collectors.toSet());
        } catch (Exception e){
            throw new PluginPlugException(e);
        }
    }



    /**
     * 得到插件包装类
     * @param pluginId 插件id
     * @return PluginWrapper
     * @throws PluginPlugException 插件装配异常
     */
    private PluginWrapper getPluginWrapper(String pluginId, String errorMsg) throws PluginPlugException {
        PluginWrapper pluginWrapper = pluginManager.getPlugin(pluginId);
        if (pluginWrapper == null) {
            throw new PluginPlugException(errorMsg + " -> Not found plugin " + pluginId);
        }
        return pluginWrapper;
    }


    /**
     * 得到存在的文件
     * @param path 插件路径
     * @return 插件路径
     * @throws IOException 没有发现文件异常
     */
    private Path getExistFile(Path path) throws IOException {
        Path parent = path.getParent();
        if(!Files.exists(parent)){
            Files.createDirectories(parent);
        }
        if(!Files.exists(path)){
            Files.createFile(path);
        }
        return path;
    }

    /**
     * 校验文件失败后, 删除临时文件
     * @param tempPluginFile 临时文件路径
     * @param e 异常信息
     * @throws PluginPlugException PluginPlugException
     */
    private void verifyFailureDelete(Path tempPluginFile, Exception e) throws PluginPlugException {
        try {
            Files.deleteIfExists(tempPluginFile);
        }catch (IOException e1){
            throw new PluginPlugException("Verify failure and delete temp file failure : " + e.getMessage(), e);
        }
    }

    /**
     * 备份
     * @param path 文件的路径
     * @param appendName 追加的字符串
     * @param type 类型 1移动 2拷贝
     * @return 结果
     * @throws PluginPlugException PluginPlugException
     */
    private boolean backup(Path path, String appendName, int type) throws PluginPlugException {
        if(!Files.exists(path)){
            throw new PluginPlugException(path.toString() + "  does not exist!");
        }
        try {
            String fileName = path.getFileName().toString();
            String targetName = integrationConfiguration.backupPath() + File.separator + getNowTimeByFormat();
            if(!StringUtils.isEmpty(appendName)){
                targetName = targetName + "_" + appendName;
            }
            Path target = Paths.get(targetName + "_" + fileName);
            if(!Files.exists(target.getParent())){
                Files.createDirectories(target.getParent());
            }
            if(type == 1){
                Files.move(path, target);
            } else {
                Files.copy(path, target);
            }
            return true;
        } catch (IOException e) {
            throw new PluginPlugException("BackupPlugin " + path.toString() +  " failure : " + e.getMessage(), e);
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
