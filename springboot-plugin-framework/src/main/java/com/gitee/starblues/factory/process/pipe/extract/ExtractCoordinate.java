package com.gitee.starblues.factory.process.pipe.extract;

import com.gitee.starblues.annotation.Extract;
import org.pf4j.util.StringUtils;

import java.util.Objects;

/**
 * 执行器坐标
 * @author starBlues
 * @version 1.0
 */
public class ExtractCoordinate {

    private String bus;
    private String useCase;
    private String scene;

    ExtractCoordinate(String bus, String useCase, String scene) {
        this.bus = bus;
        this.useCase = useCase;
        this.scene = scene;
    }


    ExtractCoordinate(Extract extract) {
        this.bus = extract.bus();
        this.useCase = extract.useCase();
        this.scene = extract.scene();
    }

    public static ExtractCoordinate build(String bus) {
        return new ExtractCoordinate(bus, null, null);
    }

    public static ExtractCoordinate build(String bus, String useCase) {
        return new ExtractCoordinate(bus, useCase, null);
    }

    public static ExtractCoordinate build(String bus, String useCase, String scene) {
        return new ExtractCoordinate(bus, useCase, scene);
    }

    public String getBus() {
        return bus;
    }

    public String getUseCase() {
        return useCase;
    }

    public String getScene() {
        return scene;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExtractCoordinate)) return false;
        ExtractCoordinate that = (ExtractCoordinate) o;
        if(StringUtils.isNotNullOrEmpty(bus) &&
                StringUtils.isNotNullOrEmpty(useCase) &&
                StringUtils.isNotNullOrEmpty(scene)){
            return Objects.equals(getBus(), that.getBus()) &&
                    Objects.equals(getUseCase(), that.getUseCase()) &&
                    Objects.equals(getScene(), that.getScene());
        }

        if(StringUtils.isNotNullOrEmpty(bus) && StringUtils.isNotNullOrEmpty(useCase)){
            return Objects.equals(getBus(), that.getBus()) &&
                    Objects.equals(getUseCase(), that.getUseCase());
        }

        if(StringUtils.isNotNullOrEmpty(bus)){
            return Objects.equals(getBus(), that.getBus());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBus(), getUseCase(), getScene());
    }

    @Override
    public String toString() {
        return "ExtractCoordinate{" +
                "bus='" + bus + '\'' +
                ", useCase='" + useCase + '\'' +
                ", scene='" + scene + '\'' +
                '}';
    }
}
