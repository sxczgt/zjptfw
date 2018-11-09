package com.bocpay.trade;

import cn.tsinghua.sftp.config.MoneyType;
import cn.tsinghua.sftp.config.PaymentState;
import cn.tsinghua.sftp.config.TysfConfig;
import cn.tsinghua.sftp.dao.IPaymentDao;
import cn.tsinghua.sftp.pojo.PaymentItem;
import cn.tsinghua.sftp.pojo.PaymentRecord;
import cn.tsinghua.sftp.pojo.ZhpayBill;
import cn.tsinghua.sftp.util.JsonUtil;
import cn.tsinghua.sftp.util.SpringContextUtil;
import cn.tsinghua.sftp.util.StringUtil;
import com.base.TradeBase;
import com.bocpay.builder.TradeCancelRequestBuilder;
import com.bocpay.builder.TradePayRequestBuilder;
import com.bocpay.builder.TradeQueryRequestBuilder;
import com.bocpay.builder.TradeRefundRequestBuilder;
import com.bocpay.config.BocpayConfig;
import com.bocpay.utils.BocUtils;
import com.unipay.utils.FTPUtil;
import com.utils.ResultInfo;
import net.sf.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * 中国银行服务接口（第三方）
 */
public class BocpayTradeNew extends TradeBase {

    /**
     * 日志
     */
    private static Log log = LogFactory.getLog(BocpayTradeNew.class);

    private IPaymentDao paymentDao = (IPaymentDao) SpringContextUtil.getBean("paymentDao");

    @Override
    public ResultInfo TradePayPage(PaymentRecord paymentRecord) {
        return null;
    }

    @Override
    public ResultInfo TradePayQrcode(PaymentRecord paymentRecord) {
        return null;
    }

    @Override
    public ResultInfo TradePayMicropay(PaymentRecord paymentRecord) {
        return null;
    }

    @Override
    public ResultInfo TradePayApp(PaymentRecord paymentRecord) {
        return null;
    }

    @Override
    public ResultInfo TradePayWap(PaymentRecord paymentRecord) {
        return null;
    }

    /**
     * PC网页外币支付
     * @param paymentRecord
     * @return
     */
    @Override
    public ResultInfo TradePayPageFc(PaymentRecord paymentRecord) {
        try {
            // 生成请求参数
            String out_trade_no = paymentRecord.getPartner() + paymentRecord.getOutTradeNo(); // 订单号
            String vpc_Amount = new BigDecimal((paymentRecord.getTotalFee().doubleValue() * 100)).toPlainString(); // 分转元
            String vpc_Currency = MoneyType.getEname(paymentRecord.getMoneyType()); // 币种
            if (StringUtil.isEmpty(vpc_Currency)) {
                vpc_Currency = "CNY"; // 默认人民币
            }
            String vpc_Locale = "en_US"; // 显示语种：zh_CN、en_US（如果流水表中存语种可以直接对接）
            PaymentItem paymentItem = paymentDao.getPaymentItem(paymentRecord.getItem());
            String merchant_id = paymentItem.getThirdPartySubaccount(); // 商户ID
            TradePayRequestBuilder builder = new TradePayRequestBuilder(merchant_id);
            builder.setVpc_MerchTxnRef(out_trade_no); // 订单号
            builder.setVpc_OrderInfo(out_trade_no); // 订单号
            builder.setVpc_Amount(vpc_Amount); // 订单金额
            builder.setVpc_Currency(vpc_Currency); // 币种
            builder.setVpc_Locale(vpc_Locale); // 显示语种
            Map<String, Object> map = builder.getBuildMap();
            String vpc_SecureHash = BocUtils.getSHAhashAllFields(map);
            map.put("vpc_SecureHash", vpc_SecureHash);
            map.put("vpc_SecureHashType", "SHA256");
            String url = BocpayConfig.getInstance().getVpcpayUrl() + "?" + BocUtils.getQueryUrlAllFields(map);
            return info("OPERATE_SUCCESS", url);
        } catch (Exception e) {
            return info("SYSTEM_ERROR", e.getMessage());
        }
    }

    @Override
    public ResultInfo TradePayWapFc(PaymentRecord paymentRecord) {
        return null;
    }

    /**
     * 订单查询
     * @param tradeNo 订单号
     * @return
     */
    @Override
    public ResultInfo TradeOrderQuery(String tradeNo) {

        try {

            PaymentRecord paymentRecord = paymentDao.getPaymentRecord(tradeNo);

            PaymentItem paymentItem = paymentDao.getPaymentItem(paymentRecord.getItem());
            String merchant_id = paymentItem.getThirdPartySubaccount(); // 商户ID
            TradeQueryRequestBuilder builder = new TradeQueryRequestBuilder(merchant_id);
            builder.setVpc_MerchTxnRef(tradeNo);// 订单号
            Map<String, Object> requestFields = builder.getBuildMap();
            String postData = BocUtils.getQueryUrlAllFields(requestFields);
            log.debug(">>>>TradeQuery.requestFields>>>>" + requestFields);
            String resQS = BocUtils.doPost(BocpayConfig.getInstance().getVpcdpsUrl(), postData, false, "", 0);
            Map<String, Object> responseFields = BocUtils.createMapFromResponse(resQS);
            log.debug(">>>>TradeQuery.responseFields>>>>" + responseFields);

            String amount = BocUtils.null2unknown("vpc_Amount", responseFields);// 支付金额 （分）
            String transactionNo = BocUtils.null2unknown("vpc_TransactionNo", responseFields); // 中行支付订单号
            String acqResponseCode = BocUtils.null2unknown("vpc_AcqResponseCode", responseFields);// 00
            String txnResponseCode = BocUtils.null2unknown("vpc_TxnResponseCode", responseFields);// 0
            String drExists = BocUtils.null2unknown("vpc_DRExists", responseFields);// 判断订单存不存在，存在为Y，不存在为N
            if ("N".equals(drExists)) {
                return info("TRADE_NOT_EXIST", "订单不存在");
            }
            // TotalFee元转分 和 银行返回金额比较
            if (paymentRecord.getTotalFee().doubleValue() * 100 != Double.valueOf(amount)) {
                return info("SYSTEM_ERROR", "订单金额不一致");
            }
            if (txnResponseCode.equals("0") && acqResponseCode.equals("00")) {
                if (!paymentRecord.getState().equals(PaymentState.SUCCESS.getCode())) {
                    paymentRecord.setBankTradeNo(transactionNo); // 记录银行订单号，退款会用到
                    paymentRecord.setState(PaymentState.SUCCESS.getCode());
                    paymentRecord.setGmtPayment(new Date()); // 目前中行没有提供订单付款时间只能用当时时间代替
                    int updateRownum = paymentDao.receivedPayment(paymentRecord);
                    if (updateRownum != 1) {
                        log.error("中行支付记录更新失败，返回值： " + updateRownum);
                        return info("UNKNOWN", "支付记录更新失败");
                    }
                }
                return info("TRADE_SUCCESS", "支付成功");
            } else {
                return info("UNKNOWN", "支付失败");
            }
        } catch (Exception e) {
            log.error("中行订单查询失败：" + e.getMessage(), e);
            return info("SYSTEM_ERROR", e.getMessage());
        }
    }

    /**
     * 订单撤销
     * @param tradeNo 订单号
     * @return
     */
    @Override
    public ResultInfo TradeOrderCancel(String tradeNo) {
        PaymentRecord paymentRecord = paymentDao.getPaymentRecord(tradeNo);

        // 创建查询请求builder，设置请求参数
        String outTradeNo = tradeNo + "C0"; // 交易流水号=合作方号+交易流水号
        String bankTradeNo = paymentRecord.getBankTradeNo();
        try {
            PaymentItem paymentItem = paymentDao.getPaymentItem(paymentRecord.getItem());
            String merchant_id = paymentItem.getThirdPartySubaccount(); // 商户ID
            TradeCancelRequestBuilder builder = new TradeCancelRequestBuilder(merchant_id);
            builder.setVpc_MerchTxnRef(outTradeNo);// 订单号
            builder.setVpc_TransNo(bankTradeNo);// 返回订单号（此值为中行订单号，取自查询结果接口中的vpc_TransactionNo字段）
            Map<String, Object> requestFields = builder.getBuildMap();
            //
            String postData = BocUtils.getQueryUrlAllFields(requestFields);
            String resQS = BocUtils.doPost(BocpayConfig.getInstance().getVpcdpsUrl(), postData, false, "", 0);
            Map<String, Object> responseFields = BocUtils.createMapFromResponse(resQS);
            log.debug(">>>>TradeCancel.responseFields>>>>" + responseFields);

            String acqResponseCode = BocUtils.null2unknown("vpc_AcqResponseCode", responseFields);
            String txnResponseCode = BocUtils.null2unknown("vpc_TxnResponseCode", responseFields);

            if (txnResponseCode.equals("0") && acqResponseCode.equals("00")) {
                paymentRecord.setState(PaymentState.CANCEL.getCode()); // 更新状态
                paymentDao.receivedPayment(paymentRecord);
                return info("OPERATE_SUCCESS", "订单已撤销");
            } else {
                return info("UNKNOWN", "订单撤销失败");
            }
        } catch (Exception e) {
            log.error("中行订单撤消失败：" + e.getMessage(), e);
            return info("SYSTEM_ERROR", e.getMessage());
        }
    }

    /**
     * 订单退款
     * @param tradeNo      订单号
     * @param refundNo     退款号
     * @param refundAmount 退款金额
     * @param refundReason 退款原因
     * @return
     */
    @Override
    public ResultInfo TradeOrderRefund(String tradeNo, String refundNo, BigDecimal refundAmount, String refundReason) {
        PaymentRecord paymentRecord = paymentDao.getPaymentRecord(tradeNo);

        // 创建退款请求builder，设置请求参数
        String outTradeNo = tradeNo + "T0"; // 交易流水号=合作方号+交易流水号
        try {
            PaymentItem paymentItem = paymentDao.getPaymentItem(paymentRecord.getItem());
            String merchant_id = paymentItem.getThirdPartySubaccount(); // 商户ID
            TradeRefundRequestBuilder builder = new TradeRefundRequestBuilder(merchant_id);
            builder.setVpc_MerchTxnRef(outTradeNo);// 订单号
            builder.setVpc_TransNo(paymentRecord.getBankTradeNo());// 返回订单号（此值为中行订单号，取自查询结果接口中的vpc_TransactionNo字段）
            builder.setVpc_Amount(StringUtil.yuanToFen(refundAmount.toPlainString()));
            Map<String, Object> requestFields = builder.getBuildMap();
            //
            String postData = BocUtils.getQueryUrlAllFields(requestFields);
            String resQS = BocUtils.doPost(BocpayConfig.getInstance().getVpcdpsUrl(), postData, false, "", 0);
            Map<String, Object> responseFields = BocUtils.createMapFromResponse(resQS);
            log.debug(">>>>TradeRefund.responseFields>>>>" + responseFields);

            String acqResponseCode = BocUtils.null2unknown("vpc_AcqResponseCode", responseFields);
            String txnResponseCode = BocUtils.null2unknown("vpc_TxnResponseCode", responseFields);

            if (txnResponseCode.equals("0") && acqResponseCode.equals("00")) {
                paymentRecord.setState(PaymentState.REFUND.getCode()); // 更新状态
                paymentDao.receivedPayment(paymentRecord);
                return info("OPERATE_SUCCESS", "订单已退款");
            } else {
                return info("UNKNOWN", "订单退款失败");
            }
        } catch (Exception e) {
            log.error("中行订单退款失败：" + e.getMessage(), e);
            return info("SYSTEM_ERROR", e.getMessage());
        }
    }

    /**
     * 总行对账
     * @param mchId    商户号 null
     * @param billDate 对账日期
     * @param billType 账单类型：ALL，返回当日所有订单信息，默认值、SUCCESS，返回当日成功支付的订单、REFUND，返回当日退款订单 null
     * @return
     */
    @Override
    public ResultInfo DownloadBillData(String mchId, String billDate, String billType) {
        // 1. 从FTP下载对账文件
        List<String> fileList = new ArrayList<String>(); // 对账文件列表
        try {
            // 对账文件本地保存路径
            //String localPath = FileUtil.getBptbPath("BOC/" + bill_date);
            String localPath = TysfConfig.getBptbPath("BOC/" + billDate);
            // 获取FTP配置
            // FTPUtil ftp = new FTPUtil("166.111.14.107", 10022, "ftpboc", "44C9BF1B7B73DB8659ADD738EF142E08");
            String ftpIP = paymentDao.getSysParamsValue("BocPay", "FTP_IP", "166.111.14.107");
            String ftpPort = paymentDao.getSysParamsValue("BocPay", "FTP_PORT", "10022");
            String ftpUser = paymentDao.getSysParamsValue("BocPay", "FTP_USER", "ftpboc");
            String ftpPass = paymentDao.getSysParamsValue("BocPay", "FTP_PASS", "44C9BF1B7B73DB8659ADD738EF142E08");
            FTPUtil ftp = new FTPUtil(ftpIP, Integer.parseInt(ftpPort), ftpUser, ftpPass);
            List<String> list = ftp.getFileNameList(billDate); // FTP上数据必须按日期保存
            for (String file : list) {
                log.debug(billDate + "/" + file);
                // 下载文件
                if (ftp.downloadFile(billDate, file, localPath)) {
                    fileList.add(localPath + file);
                    log.debug("中行对账文件" + file + "下载成功：" + localPath + file);
                } else {
                    log.error("中行对账文件" + file + "下载失败！");
                }
            }
        } catch (Exception e) {
            log.error("中行对账文件下载失败！" + e.getMessage(), e);
            return info("UNKNOWN","中行对账文件下载失败");
        }
        if (fileList.size() == 0) {
            log.error(billDate + "中行对账文件不存在！");
            return info("UNKNOWN","中行对账文件不存在");
        }
        // 2. 解析对账数据
        LinkedList<ZhpayBill> result = new LinkedList<ZhpayBill>(); // 对账数据列表
        for (String filename : fileList) {
            try {
                List<String> fileLines = FileUtils.readLines(new File(filename), "UTF-8");
                for (String fileLine : fileLines) {
                    // 终端号 批次号 交易卡号 交易日期 交易时间 交易金额 手续费 结算金额 参考扣率 授权码 交易码 分期期数 卡别 参考号 商户编号 商户名称
                    // 11112000 070407 489049******1142 2013/04/07 000153 285.35 7.13 278.22 0.00% 0FFHB1 PCEP IP00/0000 VISA 3096*****106 104110053110000 ********科技发展（北京）有限公司
                    String[] items = fileLine.split("\t"); // 拆分记录
                    if (items.length != 16) {
                        log.error("中行对账文件格式错误！" + StringUtil.join(items, ","));
                        continue;
                    }
                    ZhpayBill bill = new ZhpayBill();
                    bill.setZdh(items[0]); // 终端号
                    bill.setPch(items[1]); // 批次号
                    bill.setJykh(items[2]); // 交易卡号
                    bill.setJyrq(items[3].replaceAll("/", "").replaceAll("-", "").replaceAll("\\.", "").trim()); // 交易日期
                    bill.setJysj(items[4]); // 交易时间
                    bill.setJyje(items[5]); // 交易金额
                    bill.setSxf(items[6]); // 手续费
                    bill.setJsje(items[7]); // 结算金额
                    bill.setCkkl(items[8]); // 参考扣率
                    bill.setSqm(items[9]); // 授权码
                    bill.setJym(items[10].trim()); // 交易码：PCEP/REFP（交易/退款）
                    bill.setFqqs(items[11]);// 分期期数
                    bill.setKb(items[12]); // 卡别
                    bill.setCkh(items[13].trim()); // 参考号
                    bill.setShbh(items[14]); // 商户编号
                    bill.setShmc(items[15]);// 商户名称
                    //
                    bill.setBillDate(bill.getJyrq()); // 交易日期
                    if (bill.getJym().equals("PCEP"))// 交易类型
                        bill.setBillType("交易");
                    else if (bill.getJym().equals("REFP"))
                        bill.setBillType("退款");
                    else // 未知交易类型，直接填入
                        bill.setBillType(bill.getJym());
                    bill.setPartner(bill.getCkh().substring(0, 4)); // 合作方
                    bill.setOutTradeNo(bill.getCkh().substring(4)); // 订单号
                    bill.setTotalFee(new BigDecimal(bill.getJyje()));
                    result.add(bill);
                }
                if (fileLines.size() != result.size()) {
                    log.error("解析中行对账数据失败！数据行=" + fileLines.size() + ", 记录数=" + result.size());
                    return info("UNKNOWN","解析中行对账数据失败");
                }
            } catch (Exception e) {
                log.error("解析中行对账数据失败！" + e.getMessage(), e);
                return info("UNKNOWN","解析中行对账数据失败");
            }
        }
        JSONArray json = JSONArray.fromObject(result);
        return info("",json.toString());
    }
}
