/**
 * Copyright [2019-2022] [starBlues]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitee.starblues.bootstrap.realize;

/**
 * 插件停止校验器. 自主实现判断是否可卸载
 * @author starBlues
 * @version 3.0.0
 */
public interface StopValidator {


    /**
     * 校验是否可停止/卸载。如果校验器抛出异常. 默认插件不可停止/卸载
     * @return 校验结果
     */
    Result verify();


    class Result{
        /**
         * 是否可卸载
         */
        private final boolean verify;

        /**
         * 不可卸载时的提示内容
         */
        private String message;


        public Result(boolean verify) {
            this.verify = verify;
        }

        public Result(boolean verify, String message) {
            this.verify = verify;
            this.message = message;
        }

        public boolean isVerify() {
            return verify;
        }

        public String getMessage() {
            return message;
        }
    }

}
