package com.gitee.starblues.realize;

/**
 * 卸载校验器
 * @author starBlues
 * @version 2.4.0
 */
public interface UnRegistryValidator {


    /**
     * 校验是否可卸载
     * @return 校验结果
     * @throws Exception 校验异常. 校验异常后, 该插件默认不可卸载
     */
    Result verify() throws Exception;


    class Result{
        /**
         * 是否可卸载
         */
        private boolean verify;

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
