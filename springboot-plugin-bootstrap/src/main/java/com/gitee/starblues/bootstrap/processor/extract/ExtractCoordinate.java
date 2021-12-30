package com.gitee.starblues.bootstrap.processor.extract;

import com.gitee.starblues.annotation.Extract;
import org.pf4j.util.StringUtils;

import java.util.Objects;

/**
 * 执行器坐标
 * @author starBlues
 * @version 2.4.4
 */
public class ExtractCoordinate {

    private final String bus;
    private final String scene;
    private final String useCase;
    private final Class<?> extractClass;

    ExtractCoordinate(String bus, String scene, String useCase, Class<?> extractClass) {
        this.bus = bus;
        this.scene = scene;
        this.useCase = useCase;
        this.extractClass = extractClass;
    }


    ExtractCoordinate(Extract extract, Class<?> extractClass) {
        this.bus = extract.bus();
        this.scene = extract.scene();
        this.useCase = extract.useCase();
        this.extractClass = extractClass;
    }

    public static ExtractCoordinate build(String bus) {
        return new ExtractCoordinate(bus, null, null, null);
    }

    public static ExtractCoordinate build(String bus, String scene) {
        return new ExtractCoordinate(bus, scene, null, null);
    }

    public static ExtractCoordinate build(String bus, String scene, String useCase) {
        return new ExtractCoordinate(bus, scene, useCase, null);
    }

    public String getBus() {
        return bus;
    }

    public String getScene() {
        return scene;
    }

    public String getUseCase() {
        return useCase;
    }

    public Class<?> getExtractClass() {
        return extractClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExtractCoordinate)) {
            return false;
        }
        ExtractCoordinate that = (ExtractCoordinate) o;
        if(StringUtils.isNotNullOrEmpty(bus) &&
                StringUtils.isNotNullOrEmpty(scene) &&
                StringUtils.isNotNullOrEmpty(useCase)){
            return Objects.equals(getBus(), that.getBus()) &&
                    Objects.equals(getScene(), that.getScene()) &&
                    Objects.equals(getUseCase(), that.getUseCase());
        }

        if(StringUtils.isNotNullOrEmpty(bus) && StringUtils.isNotNullOrEmpty(scene)){
            return Objects.equals(getBus(), that.getBus()) &&
                    Objects.equals(getScene(), that.getScene());
        }

        if(StringUtils.isNotNullOrEmpty(bus)){
            return Objects.equals(getBus(), that.getBus());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBus(), getScene(), getUseCase());
    }

    @Override
    public String toString() {
        return "ExtractCoordinate{" +
                "bus='" + bus + '\'' +
                ", scene='" + scene + '\'' +
                ", useCase='" + useCase + '\'' +
                '}';
    }

}
