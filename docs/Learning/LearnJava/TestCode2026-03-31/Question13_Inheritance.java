/**
 * 题目：什么是Java中的继承机制
 * 
 * 演示Java继承的基本概念和使用
 */
public class Question13_Inheritance {
    public static void main(String[] args) {
        System.out.println("=== 继承的基本概念 ===\n");
        
        // 创建子类对象
        Dog dog = new Dog("旺财");
        dog.eat();   // 调用继承的方法
        dog.bark();  // 调用自己的方法
        
        System.out.println("\n=== 构造方法的执行顺序 ===\n");
        
        Cat cat = new Cat("咪咪", 3);
        System.out.println("猫的名字：" + cat.getName());
        System.out.println("猫的年龄：" + cat.getAge());
        
        System.out.println("\n=== 访问权限演示 ===\n");
        
        Son son = new Son();
        son.accessFields();
        
        System.out.println("\n=== 继承链 ===\n");
        
        System.out.println("Java只支持单继承，但可以多层继承：");
        System.out.println("  Animal → Dog → GoldenRetriever");
        
        GoldenRetriever gr = new GoldenRetriever("大黄");
        gr.eat();      // Animal的方法
        gr.bark();     // Dog的方法
        gr.fetch();    // GoldenRetriever的方法
        
        System.out.println("\n=== 方法重写 ===\n");
        
        Animal animal1 = new Animal("动物");
        Animal animal2 = new Dog("小狗");
        
        animal1.sound();  // Animal的声音
        animal2.sound();  // Dog重写后的声音（多态）
        
        System.out.println("\n=== 继承 vs 组合 ===\n");
        
        System.out.println("继承：is-a关系（Dog is a Animal）");
        System.out.println("组合：has-a关系（Car has a Engine）");
        System.out.println();
        System.out.println("推荐：优先使用组合而非继承");
        
        System.out.println("\n=== 常见陷阱 ===\n");
        
        // 陷阱1：静态方法不参与多态
        System.out.println("陷阱1：静态方法不参与多态");
        Father f = new Son();
        f.staticMethod();  // 输出Father的静态方法
        
        // 陷阱2：字段没有多态
        System.out.println("\n陷阱2：字段没有多态");
        Father f2 = new Son();
        System.out.println("  Father引用指向Son对象");
        System.out.println("  f2.name = " + f2.name);  // Father的字段
        
        Son s = new Son();
        System.out.println("  Son对象");
        System.out.println("  s.name = " + s.name);  // Son的字段
        
        System.out.println("\n=== 最佳实践 ===\n");
        
        System.out.println("1. 继承关系要符合is-a原则");
        System.out.println("2. 优先使用组合而非继承");
        System.out.println("3. 父类设计要考虑扩展性");
        System.out.println("4. 父类构造方法要合理设计");
        System.out.println("5. 避免过深的继承层次（建议不超过3层）");
    }
}

// ========== 父类 ==========
class Animal {
    protected String name;
    
    // 无参构造
    public Animal() {
        System.out.println("  Animal无参构造");
    }
    
    // 带参构造
    public Animal(String name) {
        System.out.println("  Animal带参构造：" + name);
        this.name = name;
    }
    
    public void eat() {
        System.out.println(name + " is eating");
    }
    
    public void sound() {
        System.out.println(name + " makes a sound");
    }
    
    public String getName() {
        return name;
    }
}

// ========== 子类1 ==========
class Dog extends Animal {
    public Dog(String name) {
        super(name);  // 调用父类构造方法
        System.out.println("  Dog构造：" + name);
    }
    
    public void bark() {
        System.out.println(name + " is barking");
    }
    
    @Override
    public void sound() {
        System.out.println(name + " 汪汪叫");
    }
}

// ========== 子类2（演示构造顺序）==========
class Cat extends Animal {
    private int age;
    
    public Cat(String name, int age) {
        super(name);  // 必须第一行
        this.age = age;
        System.out.println("  Cat构造：" + name + ", " + age + "岁");
    }
    
    public int getAge() {
        return age;
    }
}

// ========== 多层继承 ==========
class GoldenRetriever extends Dog {
    public GoldenRetriever(String name) {
        super(name);
        System.out.println("  GoldenRetriever构造：" + name);
    }
    
    public void fetch() {
        System.out.println(name + " is fetching the ball");
    }
}

// ========== 访问权限演示 ==========
class Father {
    public int publicField = 1;
    protected int protectedField = 2;
    int defaultField = 3;
    private int privateField = 4;
    
    public static void staticMethod() {
        System.out.println("  Father静态方法");
    }
    
    public String name = "Father";
}

class Son extends Father {
    public String name = "Son";
    
    public void accessFields() {
        System.out.println("  publicField: " + publicField);
        System.out.println("  protectedField: " + protectedField);
        // System.out.println(defaultField);  // 不同包无法访问
        // System.out.println(privateField);  // private无法访问
        System.out.println("  （private字段只能通过父类方法访问）");
    }
    
    public static void staticMethod() {
        System.out.println("  Son静态方法");
    }
}
