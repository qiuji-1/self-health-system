import java.util.Optional;

/**
 * 题目：Java的Optional类是什么，它有什么用
 * 
 * 演示Optional的使用和与传统null处理的对比
 */
public class Question09_Optional {
    public static void main(String[] args) {
        System.out.println("=== Optional的创建方式 ===\n");
        
        // 创建空的Optional
        Optional<String> empty = Optional.empty();
        System.out.println("empty: " + empty);
        System.out.println("是否有值: " + empty.isPresent());
        
        // 创建非空Optional（确定不为null时）
        Optional<String> opt1 = Optional.of("Hello");
        System.out.println("opt1: " + opt1.get());
        
        // 创建可能为空的Optional（推荐）
        String value = null;
        Optional<String> opt2 = Optional.ofNullable(value);
        System.out.println("opt2是否有值: " + opt2.isPresent());
        
        System.out.println("\n=== 传统方式 vs Optional方式 ===\n");
        
        // 传统方式
        System.out.println("传统方式：");
        User user1 = findUserById("123");
        if (user1 != null) {
            System.out.println("用户名: " + user1.getName());
        } else {
            System.out.println("用户不存在");
        }
        
        // Optional方式
        System.out.println("\nOptional方式：");
        Optional<User> userOpt = findUserByIdOptional("123");
        String name = userOpt
            .map(User::getName)
            .orElse("用户不存在");
        System.out.println("用户名: " + name);
        
        System.out.println("\n=== Optional常用API ===\n");
        
        Optional<String> opt = Optional.ofNullable(getValue());
        
        // orElse：没值返回默认值
        String value1 = opt.orElse("默认值");
        System.out.println("orElse结果: " + value1);
        
        // orElseGet：懒加载默认值（推荐）
        String value2 = opt.orElseGet(() -> {
            System.out.println("执行懒加载");
            return "懒加载默认值";
        });
        System.out.println("orElseGet结果: " + value2);
        
        // orElseThrow：没值抛异常
        try {
            Optional.empty().orElseThrow(() -> new RuntimeException("值不存在"));
        } catch (RuntimeException e) {
            System.out.println("捕获异常: " + e.getMessage());
        }
        
        // ifPresent：有值才执行
        opt.ifPresent(val -> System.out.println("ifPresent: " + val));
        
        System.out.println("\n=== 链式调用示例 ===\n");
        
        // 链式查询：获取用户所在城市
        String city = findUserByIdOptional("123")
            .map(User::getAddress)
            .map(Address::getCity)
            .orElse("未知城市");
        System.out.println("城市: " + city);
        
        // map + filter + orElse
        String result = Optional.of("Hello World")
            .map(String::toUpperCase)
            .filter(s -> s.length() > 5)
            .orElse("太短");
        System.out.println("链式处理结果: " + result);
        
        System.out.println("\n=== 处理可能为null的值 ===\n");
        
        // 模拟从数据库查询
        User dbUser = new User("张三", null);
        
        // 传统方式：多层null判断
        String city1;
        if (dbUser != null) {
            Address addr = dbUser.getAddress();
            if (addr != null) {
                city1 = addr.getCity();
            } else {
                city1 = "未知城市";
            }
        } else {
            city1 = "未知城市";
        }
        System.out.println("传统方式获取城市: " + city1);
        
        // Optional方式：链式调用
        String city2 = Optional.ofNullable(dbUser)
            .map(User::getAddress)
            .map(Address::getCity)
            .orElse("未知城市");
        System.out.println("Optional方式获取城市: " + city2);
        
        System.out.println("\n=== 组合多个Optional ===\n");
        
        Optional<String> optA = Optional.empty();
        Optional<String> optB = Optional.of("备选值");
        
        // Java 9+: or方法
        String combinedValue = optA.or(() -> optB).get();
        System.out.println("组合结果: " + combinedValue);
        
        // Java 8兼容方式
        String combinedValue2 = optA.isPresent() ? optA.get() : optB.orElse("默认值");
        System.out.println("兼容方式结果: " + combinedValue2);
        
        System.out.println("\n=== 最佳实践建议 ===\n");
        System.out.println("✅ 推荐用法：");
        System.out.println("  1. 作为方法返回值，表示可能没有结果");
        System.out.println("  2. 链式调用简化null处理");
        System.out.println("  3. 使用orElse/orElseGet提供默认值");
        System.out.println("  4. 使用ifPresent处理有值的情况");
        System.out.println();
        System.out.println("❌ 不推荐用法：");
        System.out.println("  1. 用于类的字段");
        System.out.println("  2. 作为方法参数");
        System.out.println("  3. 直接调用get()不判空");
        System.out.println("  4. 包装集合类型（空集合表示无数据）");
    }
    
    // 传统方式：返回null
    private static User findUserById(String id) {
        if ("123".equals(id)) {
            User user = new User("张三", new Address("杭州"));
            return user;
        }
        return null;
    }
    
    // Optional方式：返回Optional
    private static Optional<User> findUserByIdOptional(String id) {
        User user = findUserById(id);
        return Optional.ofNullable(user);
    }
    
    private static String getValue() {
        return Math.random() > 0.5 ? "随机值" : null;
    }
}

class User {
    private String name;
    private Address address;
    
    public User(String name, Address address) {
        this.name = name;
        this.address = address;
    }
    
    public String getName() {
        return name;
    }
    
    public Address getAddress() {
        return address;
    }
}

class Address {
    private String city;
    
    public Address(String city) {
        this.city = city;
    }
    
    public String getCity() {
        return city;
    }
}
