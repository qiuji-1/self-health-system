/**
 * 题目：JDK和JRE有什么区别
 * 
 * 演示JDK、JRE、JVM的关系和区别
 */
public class Question05_JDKJRE {
    public static void main(String[] args) {
        System.out.println("=== JDK、JRE、JVM的关系 ===\n");
        System.out.println("JDK = JRE + 开发工具");
        System.out.println("JRE = JVM + 核心类库");
        System.out.println();
        
        System.out.println("=== JRE包含的内容 ===\n");
        System.out.println("1. JVM（Java虚拟机）");
        System.out.println("   - 执行字节码");
        System.out.println("   - 内存管理");
        System.out.println("   - 垃圾回收");
        System.out.println();
        
        System.out.println("2. 核心类库");
        System.out.println("   - java.lang（String、Object等）");
        System.out.println("   - java.util（集合框架）");
        System.out.println("   - java.io（输入输出）");
        System.out.println();
        
        System.out.println("=== JDK比JRE多出的工具 ===\n");
        System.out.println("开发工具：");
        System.out.println("  - javac    编译器：.java → .class");
        System.out.println("  - java     运行器：执行.class");
        System.out.println("  - jdb      调试器：排查问题");
        System.out.println("  - jar      打包工具：打包class文件");
        System.out.println("  - javadoc  文档生成器");
        System.out.println();
        
        System.out.println("监控诊断工具：");
        System.out.println("  - jps      查看Java进程");
        System.out.println("  - jstack   查看线程栈");
        System.out.println("  - jmap     查看内存映射");
        System.out.println("  - jconsole 可视化监控");
        System.out.println();
        
        System.out.println("=== 演示核心类库的使用 ===\n");
        
        // java.lang包的使用
        String str = "Hello Java";
        System.out.println("String类（java.lang）: " + str);
        System.out.println("长度: " + str.length());
        
        // java.util包的使用
        java.util.List<String> list = new java.util.ArrayList<>();
        list.add("元素1");
        list.add("元素2");
        System.out.println("ArrayList（java.util）: " + list);
        
        System.out.println("\n=== 应用场景 ===\n");
        System.out.println("运行Java程序    → JRE即可");
        System.out.println("开发Java程序    → 必须JDK");
        System.out.println("服务器部署      → JRE即可");
        System.out.println("学习Java       → 建议JDK");
    }
}
