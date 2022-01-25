package com.gitee.starblues.bootstrap.realize;

/**
 * 插件停止校验器. 自主实现判断是否可卸载
 * @author starBlues
 * @version 3.0.0
 */
public interface StopValidator {


    /**
     * 校验是否可卸载。如果校验器抛出异常. 默认插件不可停止
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
