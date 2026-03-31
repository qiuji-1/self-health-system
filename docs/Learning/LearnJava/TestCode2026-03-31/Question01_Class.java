/**
 * 题目：什么是Java中的类
 * 
 * 演示类的基本结构和使用
 */
public class Question01_Class {
    public static void main(String[] args) {
        System.out.println("=== 演示类的基本概念 ===\n");
        
        // 类是模板，对象是根据模板创建的具体实例
        Student s1 = new Student();
        s1.name = "张三";
        s1.age = 20;
        s1.study();
        
        Student s2 = new Student();
        s2.name = "李四";
        s2.age = 22;
        s2.study();
        
        System.out.println("\n=== 类的三要素 ===");
        System.out.println("1. 字段（属性）：name, age");
        System.out.println("2. 方法（行为）：study()");
        System.out.println("3. 构造器：Student()");
    }
}

/**
 * 学生类 - 演示类的基本结构
 */
class Student {
    // 字段（属性）
    String name;
    int age;
    
    // 方法（行为）
    public void study() {
        System.out.println(name + "正在学习，年龄：" + age);
    }
    
    // 构造器
    public Student() {
        System.out.println("创建了一个学生对象");
    }
}
