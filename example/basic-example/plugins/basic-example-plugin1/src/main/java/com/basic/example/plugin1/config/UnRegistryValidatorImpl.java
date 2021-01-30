package com.basic.example.plugin1.config;

import com.gitee.starblues.realize.UnRegistryValidator;
import org.springframework.stereotype.Component;

/**
 * @author starBlues
 * @version 1.0
 */
@Component
public class UnRegistryValidatorImpl implements UnRegistryValidator {
    @Override
    public Result verify() throws Exception {
        return new Result(false, "不可卸载");
    }
}
