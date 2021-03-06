package netty.pro.common;

import java.io.FileInputStream;
import java.util.ArrayList;

public class CustomLoadClass extends ClassLoader {

    private ArrayList<String> filePathArr;

    public CustomLoadClass()
    {
        this.filePathArr = new ArrayList<String>();
    }

    public CustomLoadClass addFilePath(String filePath)
    {
        this.filePathArr.add(filePath);
        return this;
    }


    /**
     * Class.forName(className)方法，内部实际调用的方法是  Class.forName(className,true,classloader);
     *
     * 第2个boolean参数表示类是否需要初始化，  Class.forName(className)默认是需要初始化。
     *
     * 一旦初始化，就会触发目标对象的 static块代码执行，static参数也也会被再次初始化。
     *
     *
     *
     * ClassLoader.loadClass(className)方法，内部实际调用的方法是  ClassLoader.loadClass(className,false);
     *
     * 第2个 boolean参数，表示目标对象是否进行链接，false表示不进行链接，由上面介绍可以，
     *
     * 不进行链接意味着不进行包括初始化等一些列步骤，那么静态块和静态对象就不会得到执行
     * @param fileName
     * @return
     * @throws ClassNotFoundException
     */

    public Class<?> findClass(String fileName) throws ClassNotFoundException
    {
        try {
            byte[] data = this.loadByte(fileName);
            return defineClass(fileName, data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ClassNotFoundException();
        }

    }


    /**
     *
     * @param name
     * @return
     * @throws Exception
     */
    private byte[] loadByte(String name) throws Exception
    {
        name = name.replaceAll("\\.", "/");
        byte[] data;
        FileInputStream fis = new FileInputStream(name + ".class");
        int len = fis.available();
        data = new byte[len];
        fis.read(data);
        fis.close();
        return data;

    }
}