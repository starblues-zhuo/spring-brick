package com.gitee.starblues.extension.cloud.openfeign;

import org.springframework.cloud.context.named.NamedContextFactory;

import java.util.Arrays;
import java.util.Objects;

/**
 * copy org.springframework.cloud.openfeign.FeignClientSpecification
 * @author starBlues
 * @version 2.4.6
 */
public class FeignClientSpecification implements NamedContextFactory.Specification {

    private String name;

    private Class<?>[] configuration;

    FeignClientSpecification() {
    }

    FeignClientSpecification(String name, Class<?>[] configuration) {
        this.name = name;
        this.configuration = configuration;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Class<?>[] getConfiguration() {
        return this.configuration;
    }

    public void setConfiguration(Class<?>[] configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FeignClientSpecification that = (FeignClientSpecification) o;
        return Objects.equals(this.name, that.name)
                && Arrays.equals(this.configuration, that.configuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.configuration);
    }

    @Override
    public String toString() {
        return new StringBuilder("FeignClientSpecification{").append("name='")
                .append(this.name).append("', ").append("configuration=")
                .append(Arrays.toString(this.configuration)).append("}").toString();
    }

}
