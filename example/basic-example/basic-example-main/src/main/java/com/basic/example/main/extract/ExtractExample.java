package com.basic.example.main.extract;

/**
 * @author starBlues
 * @version 1.0
 */
public interface ExtractExample {

    void exe();

    void exe(String name);

    void exe(Info info);

    Info exeInfo(Info info);


    class Info{
        private String name;
        private Integer age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "Info{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }




}
