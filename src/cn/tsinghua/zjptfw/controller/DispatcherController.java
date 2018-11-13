package cn.tsinghua.zjptfw.controller;

import cn.tsinghua.zjptfw.service.IInvalidDataService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Auther: guotao
 * @Date: 2018/11/12 10:15
 * @Description: 对http请求进行分类处理的类
 */
@Controller("dispacherController")
@RequestMapping("/zjptfw")
public class DispatcherController {
    /*
       数据校验的service
     */
    private IInvalidDataService invalidDataService;

}
