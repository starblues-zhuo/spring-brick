/**
 * Copyright [2019-2022] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.spring.extract;

import com.gitee.starblues.annotation.Extract;
import com.gitee.starblues.utils.ObjectUtils;

import java.util.Objects;

/**
 * 执行器坐标
 * @author starBlues
 * @version 3.0.0
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
        if(!ObjectUtils.isEmpty(bus) && !ObjectUtils.isEmpty(scene) && !ObjectUtils.isEmpty(useCase)){
            return Objects.equals(getBus(), that.getBus()) &&
                    Objects.equals(getScene(), that.getScene()) &&
                    Objects.equals(getUseCase(), that.getUseCase());
        }

        if(!ObjectUtils.isEmpty(bus) && !ObjectUtils.isEmpty(scene)){
            return Objects.equals(getBus(), that.getBus()) &&
                    Objects.equals(getScene(), that.getScene());
        }

        if(!ObjectUtils.isEmpty(bus)){
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
