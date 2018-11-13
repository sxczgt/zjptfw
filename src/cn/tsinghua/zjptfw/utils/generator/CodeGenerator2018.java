package cn.tsinghua.zjptfw.utils.generator;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeGenerator2018 {

    public static String packageName = "cn.edu.zjptfw";
    public static String outputDir = "D:\\temp\\codeGen1";
    //生成前端页面,是否手动填写主键
    static boolean jspWithMainKey = true;

    public static void main(String[] args) {
        enableTableFieldAnnotation = false;
        serviceClassNameStartWithI = false;
        // 单表生成参数格式   表名*前端界面模块名
        /**
         * ZJJS_CASH_BANK*银行代码
         *ZJJS_city*市
         */
//        generateByTables(packageName, "ZJJS_CASH_BANK*收款银行");
    }

    public static void generateByTables(String packageName, String tableModalName) {
        if (StrUtil.isBlank(tableModalName) || !StrUtil.contains(tableModalName, '*')) {
            System.out.println("表名输入格式: tablename*前端界面模块名");
            return;
        }
        File f = new File(outputDir);
        if (!f.exists()) {
            System.out.println("目录初始化...");
            try {
                f.mkdirs();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        String tableName = tableModalName.split("\\*")[0];
        String modalName = tableModalName.split("\\*")[1];

        GlobalConfig config = new GlobalConfig()
                .setOutputDir(outputDir)//输出目录
                .setFileOverride(true)// 是否覆盖文件 false:已存在时不覆盖
                .setActiveRecord(true)// 开启 activeRecord 模式
                .setEnableCache(false)// XML 二级缓存
                .setBaseResultMap(true)// XML ResultMap
                .setBaseColumnList(true)// XML columList
                .setIdType(IdType.INPUT)//主键自动添加IdType注解
                .setAuthor("guotao");//改成自己的名字

        String dbUrl = "jdbc:oracle:thin:@166.111.5.121:1521/devdb";
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.ORACLE)
                .setUrl(dbUrl)
                .setUsername("cwgl")
                .setPassword("info_cwgl")
                .setDriverName("oracle.jdbc.driver.OracleDriver");

        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig
                .setSuperControllerClass("cn.edu.tsinghua.web.common.base.BaseController")
                .setEntityColumnConstant(true)//生成字段常量
                .setCapitalMode(true)
                .setEntityLombokModel(false)
                //.setDbColumnUnderline(true)
                .setNaming(NamingStrategy.underline_to_camel)
                .entityTableFieldAnnotationEnable(enableTableFieldAnnotation)
                .setFieldPrefix(fieldPrefix)//test_id -> id, test_type -> type
                .setTablePrefix(tablePrefix)
                .setInclude(tableName);//修改替换成你需要的表名，多个表名传数组
        if (!serviceClassNameStartWithI) {
            config.setServiceName("%sService");
        }
        // 自定义前端代码生成
        // 注入自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("modalsName", modalName);
                map.put("jspWithMainKey", jspWithMainKey);
                this.setMap(map);
            }
        };

        List<FileOutConfig> focList = new ArrayList<FileOutConfig>();
        focList.add(new FileOutConfig("/templates/list.jsp.ftl") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输入文件名称
                return outputDir + "/JSP/" + StringUtils.firstCharToLower(tableInfo.getEntityName()) +
                        "/" + StringUtils.firstCharToLower(tableInfo.getEntityName()) + "List.jsp";
            }
        });
        focList.add(new FileOutConfig("/templates/add.jsp.ftl") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输入文件名称
                return outputDir + "/JSP/" + StringUtils.firstCharToLower(tableInfo.getEntityName()) +
                        "/" + StringUtils.firstCharToLower(tableInfo.getEntityName()) + "Add.jsp";
            }
        });
        focList.add(new FileOutConfig("/templates/edit.jsp.ftl") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输入文件名称
                return outputDir + "/JSP/" + StringUtils.firstCharToLower(tableInfo.getEntityName()) +
                        "/" + StringUtils.firstCharToLower(tableInfo.getEntityName()) + "Edit.jsp";
            }
        });
        focList.add(new FileOutConfig("/templates/sqlSupport.java.ftl") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return outputDir + "/DynamicSqlSupport/" + toUpperCaseFirstOne(tableInfo.getEntityName())+ "DynamicSqlSupport.java";
            }
        });
        cfg.setFileOutConfigList(focList);

            new AutoGenerator().setGlobalConfig(config)
                .setDataSource(dataSourceConfig)
                .setStrategy(strategyConfig)
                .setPackageInfo(
                        new PackageConfig()
                                .setParent(packageName)
                                .setController("controller")
                                .setEntity("entity")
                ).setCfg(cfg)
                .setTemplateEngine(new FreemarkerTemplateEngine())
                .execute();
    }

    public static String toUpperCaseFirstOne(String s){
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
    /**
     * 是否强制带上注解
     */
    static boolean enableTableFieldAnnotation = true;
    /**
     * 生成的注解带上IdType类型
     */
    /**
     * 是否去掉生成实体的属性名前缀
     */
    static String[] fieldPrefix = null;
    /**
     * 去掉指定table前缀
     */
    static String[] tablePrefix = {"zjjs_"};
    /**
     * 生成的Service 接口类名是否以I开头
     * 默认是以I开头
     * user表 -> IUserService, UserServiceImpl
     */
    static boolean serviceClassNameStartWithI = true;

}
