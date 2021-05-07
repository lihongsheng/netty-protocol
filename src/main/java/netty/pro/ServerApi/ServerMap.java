package netty.pro.ServerApi;

import java.util.HashMap;
import java.util.Map;

public class ServerMap {
   private static Map<String, Object> serviceMap = new HashMap<>();

   static {
       System.out.println("执行了");
       serviceMap.put(User.class.getName(),new UserImpl());
   }
   public static void add(String className,Object object) {
       serviceMap.put(className, object);
   }

    public static Object get(String className) {
       return serviceMap.get(className);
    }
}
