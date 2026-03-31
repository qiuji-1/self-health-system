/**
 * 题目：什么是实例化
 * 
 * 演示实例化的过程和对象与引用的关系
 */
public class Question02_Instantiation {
    public static void main(String[] args) {
        System.out.println("=== 实例化的完整过程 ===\n");
        
        // 1. 声明引用变量
        // 2. new 创建对象
        // 3. 调用构造器初始化
        // 4. 将对象地址赋给引用
        Student s1 = new Student("张三", 20);
        
        System.out.println("\n=== 对象与引用的区别 ===\n");
        
        Student s2 = new Student("李四", 22);
        Student s3 = s2;  // s3和s2指向同一个对象
        
        System.out.println("s2: " + s2.name);
        System.out.println("s3: " + s3.name);
        
        s3.name = "李四改了";
        System.out.println("修改s3.name后：");
        System.out.println("s2: " + s2.name);  // s2也被改了
        System.out.println("s3: " + s3.name);
        
        System.out.println("\n=== 实例初始化顺序 ===\n");
        Student s4 = new Student("王五", 25);
    }
}

class Student {
    String name = initName();      // 字段初始化
    int age;
    static int count = 0;          // 静态变量（类加载时初始化）
    
    // 静态代码块（类加载时执行）
    static {
        System.out.println("1. 静态代码块执行（类加载时）");
    }
    
    // 字段初始化方法
    private String initName() {
        System.out.println("2. 字段初始化");
        return "默认值";
    }
    
    // 构造器
    public Student(String name, int age) {
        System.out.println("3. 构造器执行");
        this.name = name;
        this.age = age;
    }
}
