package com.gitee.starblues.integration.pf4j;

import org.pf4j.DependencyResolver;
import org.pf4j.PluginDescriptor;
import org.pf4j.VersionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author starBlues
 * @version 1.0
 */
public class SortDependencyResolver extends DependencyResolver {

    private final static Logger LOGGER = LoggerFactory.getLogger(SortDependencyResolver.class);

    private final List<String> sortInitPluginIds;

    public SortDependencyResolver(List<String> sortInitPluginIds, VersionManager versionManager) {
        super(versionManager);
        this.sortInitPluginIds = sortInitPluginIds;
    }

    @Override
    public Result resolve(List<PluginDescriptor> plugins) {
        Result resolve = super.resolve(plugins);
        if(sortInitPluginIds == null || sortInitPluginIds.isEmpty()){
            return resolve;
        }
        List<String> sortedPlugins = resolve.getSortedPlugins();
        List<String> newSortPluginIds = new ArrayList<>(sortedPlugins.size());
        for (String sortPluginId : sortInitPluginIds) {
            Iterator<String> iterator = sortedPlugins.iterator();
            while (iterator.hasNext()){
                String id = iterator.next();
                if(Objects.equals(id, sortPluginId)){
                    newSortPluginIds.add(id);
                    iterator.remove();
                }
            }
        }
        if(!sortedPlugins.isEmpty()){
            newSortPluginIds.addAll(sortedPlugins);
        }

        return getSortResult(resolve, newSortPluginIds);
    }

    @SuppressWarnings("unchecked")
    private Result getSortResult(Result resolve,  List<String> newSortPluginIds ){
        try {
            Field sortedPluginsField = ReflectionUtils.findField(Result.class, "sortedPlugins");
            List<String> sortedPlugins = null;
            if(sortedPluginsField != null){
                if (!sortedPluginsField.isAccessible()) {
                    sortedPluginsField.setAccessible(true);
                }
                sortedPlugins = (List<String>) sortedPluginsField.get(resolve);
            }
            if(sortedPlugins == null){
                return resolve;
            }
            sortedPlugins.clear();
            sortedPlugins.addAll(newSortPluginIds);
            return resolve;
        } catch (Exception e){
            LOGGER.error("Set plugin init sort failure. use default sort init plugin. " + e.getMessage());
            return resolve;
        }
    }



}
