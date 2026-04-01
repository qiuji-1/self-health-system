/**
 * 题目：static关键字的作用
 * 
 * 演示静态变量、静态方法的使用
 */
public class Question03_Static {
    public static void main(String[] args) {
        System.out.println("=== 静态变量 vs 实例变量 ===\n");
        
        // 创建两个学生
        Student03 s1 = new Student03("张三");
        Student03 s2 = new Student03("李四");
        
        System.out.println("s1.name: " + s1.name);
        System.out.println("s2.name: " + s2.name);
        System.out.println("Student03.count: " + Student03.count);  // 静态变量用类名访问
        
        System.out.println("\n=== 静态方法调用 ===\n");
        Student03.showCount();  // 静态方法用类名调用
        
        System.out.println("\n=== 静态方法限制 ===\n");
        System.out.println("静态方法：");
        System.out.println("✅ 可以访问静态变量和静态方法");
        System.out.println("❌ 不能访问实例变量和实例方法");
        System.out.println("❌ 不能使用 this 关键字");
        
        System.out.println("\n=== 静态代码块 ===\n");
        System.out.println("静态代码块在类加载时执行一次");
        System.out.println("常用于初始化静态资源");
    }
}

class Student03 {
    String name;           // 实例变量：每个对象独有
    static int count = 0;  // 静态变量：所有对象共享
    
    // 静态代码块：类加载时执行一次
    static {
        System.out.println("静态代码块执行：类被加载了");
    }
    
    public Student03(String name) {
        this.name = name;
        count++;  // 每创建一个学生，count+1
        System.out.println("创建学生：" + name);
    }
    
    // 静态方法
    public static void showCount() {
        System.out.println("学生总数：" + count);
        // System.out.println(name);  // 错误！不能访问实例变量
    }
    
    // 实例方法
    public void study() {
        System.out.println(name + "正在学习");
        showCount();  // 可以访问静态方法
    }
}
