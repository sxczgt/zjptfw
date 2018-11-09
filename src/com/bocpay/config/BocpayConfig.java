package com.bocpay.config;

import cn.tsinghua.sftp.dao.IPaymentDao;
import cn.tsinghua.sftp.util.DateUtils;
import cn.tsinghua.sftp.util.SpringContextUtil;
import cn.tsinghua.sftp.util.StringUtil;
import com.bocpay.model.BocpayMerchant;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 中行配置类（支持多商户）<br>
 *
 * @author Jack
 */
public class BocpayConfig {
    private static Log log = LogFactory.getLog(BocpayConfig.class);

    /**
     * 支付地址
     */
    private String vpcpay_url;
    /**
     * 查询、关闭、退款等地址
     */
    private String vpcdps_url;
    /**
     * 同步返回地址
     */
    private String return_url;
    /**
     * 字符集
     */
    private String charset;
    /**
     * 商户ID列表
     */
    private List<String> merchantIDs;
    /**
     * 商户列表
     */
    private List<BocpayMerchant> merchantList = new ArrayList<BocpayMerchant>();

    //静态对象
    private static BocpayConfig instance = null;

    public static synchronized BocpayConfig getInstance() {
        if (instance == null) {
            instance = new BocpayConfig();
        }
        return instance;
    }

    public BocpayConfig() {
        if (instance == null) {
            init();
        }
    }

    /**
     * 加载配置信息
     */
    public void init() {
        log.warn("正在加载BocpayConfig配置信息：" + DateUtils.getDateTime());
        IPaymentDao paymentDao = (IPaymentDao) SpringContextUtil.getBean("paymentDao");
        if (paymentDao == null) {
            return;
        }

        // 支付地址
        vpcpay_url = paymentDao.getSysParamsValue("BocPay", "VPCPAY_URL", "https://migs.mastercard.com.au/vpcpay");
        // 查询、关闭、退款等地址
        vpcdps_url = paymentDao.getSysParamsValue("BocPay", "VPCDPS_URL", "https://migs.mastercard.com.au/vpcdps");
        // 同步返回地址
        return_url = paymentDao.getSysParamsValue("BocPay", "RETURN_URL", "https://zhifu.tsinghua.edu.cn/sfpt/bocpayReceivedAction.action");
        // 字符集
        charset = paymentDao.getSysParamsValue("BocPay", "CHARSET", "ISO-8859-1");

        //商户列表
        if (merchantIDs == null) {
            merchantIDs = new ArrayList<>();
        }
        String merchant_ids = paymentDao.getSysParamsValue("BocPay", "MERCHANT_IDS", "");
        if (StringUtil.isNotEmpty(merchant_ids)) {
            List<String> mids = Arrays.asList(merchant_ids.split(","));
            for (int i = 0; i < mids.size(); i++) {
                if (!merchantIDs.contains(mids.get(i)))
                    merchantIDs.add(mids.get(i));
            }
        }
        if (merchantIDs.size() == 0) {
            return;
        }
        // 循环加载
        if (merchantList == null) {
            merchantList = new ArrayList<BocpayMerchant>();
        }
        for (String merchantID : merchantIDs) {
            String boc = paymentDao.getSysParamsValue("BocPay", "BOC" + merchantID, "");
            try {
                BocpayMerchant bm = BocpayMerchant.parse(boc);
                if (bm != null && !merchantList.contains(bm)) {
                    merchantList.add(bm);
                }
            } catch (Exception e) {
                System.out.println("商编：" + merchantID + "配置信息加载失败！");
            }
        }
        System.out.println("初始化BocpayConfig：\n" + description());
    }

    /**
     * 显示描述信息
     */
    private String description() {
        StringBuilder sb = new StringBuilder();
        for (BocpayMerchant bm : merchantList) {
            sb.append("***********************************************************\n");
            sb.append("MERCHANT_ID=" + bm.getMerchantID()).append("\n");
            sb.append(",ACCESS_CODE=" + bm.getAccessCode()).append("\n");
            sb.append(",SECURE_SECRET=" + getKeyDescription(bm.getSecureSecret(), 6)).append("\n");
            sb.append(",USER=" + bm.getUser()).append("\n");
            sb.append(",PASSWORD=" + getKeyDescription(bm.getPassword(), 4)).append("\n");
            sb.append(",VPCPAY_URL=" + vpcpay_url).append("\n");
            sb.append(",VPCDPS_URL=" + vpcdps_url).append("\n");
            sb.append(",RETURN_URL=" + return_url).append("\n");
        }
        sb.append("***********************************************************\n");
        return sb.toString();
    }

    /**
     * 截取显示Key值
     */
    private String getKeyDescription(String key, int showLength) {
        if ((StringUtils.isNotEmpty(key)) && (key.length() > showLength)) {
            return key.substring(0, showLength) + "******" + key.substring(key.length() - showLength);
        }
        return null;
    }

    /**
     * 获取商户ID列表
     *
     * @return
     */
    public List<String> getMerchantIDs() {
        return merchantIDs;
    }

    /**
     * 根据商户号获取商户信息
     *
     * @param merchantID 商户号
     * @return
     */
    public BocpayMerchant getMerchant(String merchantID) {
        for (BocpayMerchant bm : merchantList) {
            if (bm.getMerchantID().equals(merchantID)) {
                return bm;
            }
        }
        return null;
    }

    public String getVpcpayUrl() {
        return vpcpay_url;
    }

    public String getVpcdpsUrl() {
        return vpcdps_url;
    }

    public String getReturnUrl() {
        return return_url;
    }

    public String getCharset() {
        return charset;
    }
}
