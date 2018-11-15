package cn.tsinghua.zjptfw.utils.object;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @Auther: guotao
 * @Date: 2018/11/14 15:11
 * @Description:
 */
public class CheckObjectFiled {
    /**
     * 检查传入字段是否全是null
     * @param obj
     * @Param fields 表示是需要检查的属性
     * @return
     */
    public static boolean isFieldsNull(Object obj, List<String> fields)  {
        Class stuCla = (Class) obj.getClass();// 得到类对象
        Field[] fs = stuCla.getDeclaredFields();//得到属性集合
        boolean flag = true;
        for (Field f : fs) {//遍历属性
            f.setAccessible(true); // 设置属性是可以访问的(私有的也可以)
            String name = f.getName();
            if(!fields.contains(name))continue;//名字不对的属性直接跳过
            Object val = null;
            try {
             val = f.get(obj);// 得到此属性的值
            } catch (IllegalAccessException e) {
                System.out.println("校验查询参数时出错,非法参数异常");
                System.out.println("authLog.toString()====="+obj.toString());
            }
            if(val!=null) {//只要有1个属性不为空,那么就不是所有的属性值都为空
                //空字符串也认为是空
                if(val instanceof String){
                    flag = "".equals(val);
                }else {
                    flag = false;
                    break;
                }
            }
        }
        if(flag){
            System.out.println("被检验的对象的属性全是null，或者String是空字符串");
        }
        return flag;
    }

}


