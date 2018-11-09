package com.bocpay.trade;

import cn.tsinghua.sftp.config.MoneyType;
import cn.tsinghua.sftp.config.PaymentState;
import cn.tsinghua.sftp.config.TysfConfig;
import cn.tsinghua.sftp.dao.IPaymentDao;
import cn.tsinghua.sftp.pojo.Partner;
import cn.tsinghua.sftp.pojo.PaymentItem;
import cn.tsinghua.sftp.pojo.PaymentRecord;
import cn.tsinghua.sftp.pojo.ZhpayBill;
import cn.tsinghua.sftp.util.Base64;
import cn.tsinghua.sftp.util.*;
import com.bocpay.builder.TradeCancelRequestBuilder;
import com.bocpay.builder.TradePayRequestBuilder;
import com.bocpay.builder.TradeQueryRequestBuilder;
import com.bocpay.builder.TradeRefundRequestBuilder;
import com.bocpay.config.BocpayConfig;
import com.bocpay.utils.BocUtils;
import com.tencent.utils.WxpayUtil;
import com.unipay.utils.FTPUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BocpayTrade {
    private static Log log = LogFactory.getLog(BocpayTrade.class);

    /**
     * 生成交易地址，传入流水记录不做参数判断
     *
     * @param paymentRecord 流水记录
     * @return
     */
    public String TradePay(PaymentRecord paymentRecord) {
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
            if (StringUtil.isEmpty(merchant_id)) {
                String errMsg = "收费项目未指定第三方商户号";
                log.error(errMsg + "！out_trade_no=" + out_trade_no);
                return JsonUtil.getJson(-10, errMsg);
            }
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
            return JsonUtil.getJson(0, url);
        } catch (Exception e) {
            return JsonUtil.getJson(-11, e.getMessage());
        }
    }

    /**
     * 订单查询接口
     *
     * @param t_partner      合作方编号，由财务处统一编码，分配给合作方
     * @param t_out_trade_no 商户订单号，通过此商户订单号查询当面付的交易状态(必填)
     */
    public String TradeQuery(String t_partner, String t_out_trade_no) {
        log.info("TradeQuery t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
        if (null == t_out_trade_no || t_out_trade_no.trim().length() == 0) {
            return JsonUtil.getJson(-1, "订单号不能为空");
        }
        PaymentRecord paymentRecord = paymentDao.queryPaymentRecord(t_partner, t_out_trade_no);
        if (null == paymentRecord) {
            log.error("订单不存在！" + t_out_trade_no);
            return JsonUtil.getJson(-2, "订单不存在");
        }
        // 根据支付状态返回："0,未完成","1,已提交","2,支付成功","3,支付失败","4,已关闭"，"5,已撤消"，"6,已退款"
        if (paymentRecord.getState().equals(PaymentState.CLOSED.getCode())) {
            return JsonUtil.getJson(-4, "订单已关闭");
        }
        if (paymentRecord.getState().equals(PaymentState.CANCEL.getCode())) {
            return JsonUtil.getJson(-5, "订单已撤消");
        }
        if (paymentRecord.getState().equals(PaymentState.REFUND.getCode())) {
            return JsonUtil.getJson(-6, "订单已退款");
        }

        // 创建查询请求builder，设置请求参数
        String outTradeNo = t_partner + t_out_trade_no; // 交易流水号=合作方号+交易流水号
        try {
            PaymentItem paymentItem = paymentDao.getPaymentItem(paymentRecord.getItem());
            String merchant_id = paymentItem.getThirdPartySubaccount(); // 商户ID
            if (StringUtil.isEmpty(merchant_id)) {
                log.warn("收费项目未指定第三方商户号！t_partner：" + t_partner + " t_out_trade_no：" + t_out_trade_no);
                return JsonUtil.getJson(-13, "收费项目未指定第三方商户号");
            }
            TradeQueryRequestBuilder builder = new TradeQueryRequestBuilder(merchant_id);
            builder.setVpc_MerchTxnRef(outTradeNo);// 订单号
            Map<String, Object> requestFields = builder.getBuildMap();
            //
            // String resQS = HttpUtils.post(BocpayConfig.VPCDPS_URL, requestFields, "UTF-8");
            String postData = BocUtils.getQueryUrlAllFields(requestFields);
            log.debug(">>>>TradeQuery.requestFields>>>>" + requestFields);
            String resQS = BocUtils.doPost(BocpayConfig.getInstance().getVpcdpsUrl(), postData, false, "", 0);
            Map<String, Object> responseFields = BocUtils.createMapFromResponse(resQS);
            log.debug(">>>>TradeQuery.responseFields>>>>" + responseFields);
            // String message = BocUtils.null2unknown("vpc_Message", responseFields);

            // Standard Receipt Data
            String amount = BocUtils.null2unknown("vpc_Amount", responseFields);// 支付金额 （分）
            // String locale = BocUtils.null2unknown("vpc_Locale", responseFields);
            // String batchNo = BocUtils.null2unknown("vpc_BatchNo", responseFields);
            // String command = BocUtils.null2unknown("vpc_Command", responseFields);
            // String version = BocUtils.null2unknown("vpc_Version", responseFields);
            // String cardType = BocUtils.null2unknown("vpc_Card", responseFields);
            // String orderInfo = BocUtils.null2unknown("vpc_OrderInfo", responseFields);
            // String receiptNo = BocUtils.null2unknown("vpc_ReceiptNo", responseFields);
            // String merchantID = BocUtils.null2unknown("vpc_Merchant", responseFields);
            // String authorizeID = BocUtils.null2unknown("vpc_AuthorizeId", responseFields);
            String transactionNo = BocUtils.null2unknown("vpc_TransactionNo", responseFields); // 中行支付订单号
            String acqResponseCode = BocUtils.null2unknown("vpc_AcqResponseCode", responseFields);// 00
            String txnResponseCode = BocUtils.null2unknown("vpc_TxnResponseCode", responseFields);// 0

            // // CSC Receipt Data
            // String cscResultCode = BocUtils.null2unknown("vpc_CSCResultCode", responseFields);
            // String cscRequestCode = BocUtils.null2unknown("vpc_CSCRequestCode", responseFields);
            // String cscACQRespCode = BocUtils.null2unknown("vpc_AcqCSCRespCode", responseFields);
            //
            // // AVS Receipt Data
            // String avs_City = BocUtils.null2unknown("vpc_AVS_City", responseFields);
            // String avs_Country = BocUtils.null2unknown("vpc_AVS_Country", responseFields);
            // String avs_Street01 = BocUtils.null2unknown("vpc_AVS_Street01", responseFields);
            // String avs_PostCode = BocUtils.null2unknown("vpc_AVS_PostCode", responseFields);
            // String avs_StateProv = BocUtils.null2unknown("vpc_AVS_StateProv", responseFields);
            // String avsResultCode = BocUtils.null2unknown("vpc_AVSResultCode", responseFields);
            // String avsRequestCode = BocUtils.null2unknown("vpc_AVSRequestCode", responseFields);
            // String avsACQRespCode = BocUtils.null2unknown("vpc_AcqAVSRespCode", responseFields);
            //
            // // 3-D Secure Data
            // String transType3DS = BocUtils.null2unknown("vpc_VerType", responseFields);
            // String verStatus3DS = BocUtils.null2unknown("vpc_VerStatus", responseFields);
            // String token3DS = BocUtils.null2unknown("vpc_VerToken", responseFields);
            // String secureLevel3DS = BocUtils.null2unknown("vpc_VerSecurityLevel", responseFields);
            // String enrolled3DS = BocUtils.null2unknown("vpc_3DSenrolled", responseFields);
            // String xid3DS = BocUtils.null2unknown("vpc_3DSXID", responseFields);
            // String eci3DS = BocUtils.null2unknown("vpc_3DSECI", responseFields);
            // String status3DS = BocUtils.null2unknown("vpc_3DSstatus", responseFields);
            //
            // // Financial Transaction Data
            // String shopTransNo = BocUtils.null2unknown("vpc_ShopTransactionNo", responseFields);
            // String authorisedAmount = BocUtils.null2unknown("vpc_AuthorisedAmount", responseFields);
            // String capturedAmount = BocUtils.null2unknown("vpc_CapturedAmount", responseFields);
            // String refundedAmount = BocUtils.null2unknown("vpc_RefundedAmount", responseFields);
            // String ticketNumber = BocUtils.null2unknown("vpc_TicketNo", responseFields);
            //
            // // Specific QueryDR Data
            // String multipleDRs = BocUtils.null2unknown("vpc_FoundMultipleDRs", responseFields);
            String drExists = BocUtils.null2unknown("vpc_DRExists", responseFields);// 判断订单存不存在，存在为Y，不存在为N
            if ("N".equals(drExists)) {
                return JsonUtil.getJson(-7, "订单不存在");
            }
            // TotalFee元转分 和 银行返回金额比较
            if (paymentRecord.getTotalFee().doubleValue() * 100 != Double.valueOf(amount)) {
                return JsonUtil.getJson(-8, "订单金额不一致");
            }
            if (txnResponseCode.equals("0") && acqResponseCode.equals("00")) {
                if (!paymentRecord.getState().equals(PaymentState.SUCCESS.getCode())) {
                    paymentRecord.setBankTradeNo(transactionNo); // 记录银行订单号，退款会用到
                    paymentRecord.setState(PaymentState.SUCCESS.getCode());
                    paymentRecord.setGmtPayment(new Date()); // 目前中行没有提供订单付款时间只能用当时时间代替
                    int updateRownum = paymentDao.receivedPayment(paymentRecord);
                    if (updateRownum != 1) {
                        log.error("中行支付记录更新失败，返回值： " + updateRownum);
                        return JsonUtil.getJson(-9, "支付记录更新失败");
                    }
                }
                return JsonUtil.getJson(0, "支付成功");
            } else {
                return JsonUtil.getJson(-10, "支付失败");
            }
        } catch (Exception e) {
            log.error("中行订单查询失败：" + e.getMessage(), e);
            return JsonUtil.getJson(-10, e.getMessage());
        }
    }

    /**
     * 订单撤销接口<br>
     *
     * @param t_partner      合作方编号，由财务处统一编码，分配给合作方
     * @param t_out_trade_no 原支付请求的商户订单号不能为空
     * @param t_sign         签名
     * @return
     */
    public String TradeCancel(String t_partner, String t_out_trade_no, String t_sign) {
        log.info("TradeCancel t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
        if (null == t_out_trade_no || t_out_trade_no.trim().length() == 0) {
            return JsonUtil.getJson(-1, "订单号不能为空");
        }
        PaymentRecord paymentRecord = paymentDao.queryPaymentRecord(t_partner, t_out_trade_no);
        if (null == paymentRecord) {
            log.error("订单" + t_out_trade_no + "不存在！");
            return JsonUtil.getJson(-2, "订单不存在");
        }
        // 根据支付状态返回："0,未完成","1,已提交","2,支付成功","3,支付失败","4,已关闭"，"5,已撤消"，"6,已退款"
        if (paymentRecord.getState().equals(PaymentState.CLOSED.getCode())) {
            return JsonUtil.getJson(-4, "订单已关闭");
        }
        if (paymentRecord.getState().equals(PaymentState.CANCEL.getCode())) {
            return JsonUtil.getJson(-5, "订单已撤消");
        }
        if (paymentRecord.getState().equals(PaymentState.REFUND.getCode())) {
            return JsonUtil.getJson(-6, "订单已退款");
        }
        // 读取合作方密钥
        Partner partner = paymentDao.getPartner(paymentRecord.getPartner());
        if (null == partner) {
            log.error("合作方(" + paymentRecord.getPartner() + ")未授权！");
            return JsonUtil.getJson(-3, "合作方未授权 ");
        }
        // 验证签名
        String partnerKey = partner.getDecodeKey();
        SignBuilder signBulider = new SignBuilder();
        signBulider.add("t_out_trade_no", t_out_trade_no);
        signBulider.add("t_partner", t_partner);
        String rSign = signBulider.createSign(partnerKey, "GBK");
        if (!rSign.equals(t_sign)) {
            log.error("签名验证失败   [" + rSign + "]  [" + t_sign + "]  " + t_partner + ":" + t_out_trade_no);
            if (TysfConfig.IsCheckSign()) {
                return JsonUtil.getJson(-4, "签名验证失败");
            }
        }
        // 创建查询请求builder，设置请求参数
        String outTradeNo = t_partner + t_out_trade_no + "C0"; // 交易流水号=合作方号+交易流水号
        String bankTradeNo = paymentRecord.getBankTradeNo();
        try {
            PaymentItem paymentItem = paymentDao.getPaymentItem(paymentRecord.getItem());
            String merchant_id = paymentItem.getThirdPartySubaccount(); // 商户ID
            if (StringUtil.isEmpty(merchant_id)) {
                log.warn("收费项目未指定第三方商户号！t_partner：" + t_partner + " t_out_trade_no：" + t_out_trade_no);
                return JsonUtil.getJson(-13, "收费项目未指定第三方商户号");
            }
            TradeCancelRequestBuilder builder = new TradeCancelRequestBuilder(merchant_id);
            builder.setVpc_MerchTxnRef(outTradeNo);// 订单号
            builder.setVpc_TransNo(bankTradeNo);// 返回订单号（此值为中行订单号，取自查询结果接口中的vpc_TransactionNo字段）
            Map<String, Object> requestFields = builder.getBuildMap();
            //
            // String resQS = HttpUtils.post(BocpayConfig.VPCDPS_URL, requestFields, "UTF-8");
            String postData = BocUtils.getQueryUrlAllFields(requestFields);
            String resQS = BocUtils.doPost(BocpayConfig.getInstance().getVpcdpsUrl(), postData, false, "", 0);
            Map<String, Object> responseFields = BocUtils.createMapFromResponse(resQS);
            log.debug(">>>>TradeCancel.responseFields>>>>" + responseFields);

            // String message = BocUtils.null2unknown("vpc_Message", responseFields);
            String acqResponseCode = BocUtils.null2unknown("vpc_AcqResponseCode", responseFields);
            String txnResponseCode = BocUtils.null2unknown("vpc_TxnResponseCode", responseFields);

            if (txnResponseCode.equals("0") && acqResponseCode.equals("00")) {
                paymentRecord.setState(PaymentState.CANCEL.getCode()); // 更新状态
                paymentDao.receivedPayment(paymentRecord);
                return JsonUtil.getJson(0, "订单已撤销");
            } else {
                return JsonUtil.getJson(-7, "订单撤销失败");
            }
        } catch (Exception e) {
            log.error("中行订单撤消失败：" + e.getMessage(), e);
            return JsonUtil.getJson(-8, e.getMessage());
        }
    }

    /**
     * 订单退款接口
     *
     * @param t_partner       合作方编号，由财务处统一编码，分配给合作方
     * @param t_out_trade_no  订单编号
     * @param t_refund_reason 退款原因
     * @param t_charset       字符集
     * @param t_version
     * @param t_sign          签名
     * @return
     */
    public String TradeRefund(String t_partner, String t_out_trade_no, String t_refund_reason, String t_charset, String t_version, String t_sign) {
        if (StringUtils.isEmpty(t_partner)) {
            return JsonUtil.getJson(-1, "合作方不能为空");
        }
        if (StringUtils.isEmpty(t_out_trade_no)) {
            return JsonUtil.getJson(-2, "订单号不能为空");
        }
        if (StringUtils.isEmpty(t_refund_reason)) {
            return JsonUtil.getJson(-3, "退款原因不能为空");
        }
        if ("1.1".equalsIgnoreCase(t_version)) {
            t_refund_reason = DecodeBase64(t_refund_reason);
        }
        if (null != t_charset && t_charset.length() > 0) {
            try {
                t_refund_reason = TransformCharset(t_refund_reason, t_charset);
            } catch (UnsupportedEncodingException e) {
                log.error("字符编码转换错误！" + e.getMessage() + " " + t_charset, e);
                return JsonUtil.getJson(-4, "字符编码转换错误");
            }
        }
        if (t_refund_reason != null && t_refund_reason.length() > 256) {
            return JsonUtil.getJson(-5, "退款原因超长");
        }
        PaymentRecord paymentRecord = paymentDao.queryPaymentRecord(t_partner, t_out_trade_no);
        if (null == paymentRecord) {
            log.error("订单不存在！t_partner=" + t_partner + ",t_out_trade_no=" + t_out_trade_no);
            return JsonUtil.getJson(-6, "订单不存在");
        }
        // 退款金额，该金额不能大于订单金额,单位为元，支持两位小数
        String t_total_fee = paymentRecord.getTotalFee().toString();
        // 读取合作方密钥
        Partner partner = paymentDao.getPartner(t_partner);
        if (null == partner) {
            log.error("合作方未授权！t_partner=" + t_partner);
            return JsonUtil.getJson(-7, "合作方未授权 ");
        }
        // 验证签名
        String partnerKey = partner.getDecodeKey();
        SignBuilder signBulider = new SignBuilder();
        signBulider.add("t_partner", t_partner);
        signBulider.add("t_out_trade_no", t_out_trade_no);
        signBulider.add("t_refund_reason", t_refund_reason);
        signBulider.add("t_charset", t_charset);
        signBulider.add("t_version", t_version);
        String rSign = signBulider.createSign(partnerKey, "GBK");
        if (!rSign.equals(t_sign)) {
            log.error("签名验证失败 [" + rSign + "] [" + t_sign + "] " + t_partner + ":" + t_out_trade_no);
            if (TysfConfig.IsCheckSign()) {
                return JsonUtil.getJson(-8, "签名验证失败");
            }
        }

        // 非当天的订单不允许退款操作 add by dxj 20170703
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 系统当前时间
        Date currentDate = new Date();
        // 订单创建时间
        Date createDate = paymentRecord.getCreateDate();
        log.debug("订单创建日期：" + sdf.format(createDate) + "，系统当前日期：" + sdf.format(currentDate));
        int compare = currentDate.compareTo(createDate);
        if (compare < 0) {
            log.error("订单创建时间不能大于系统当前时间！创建日期：" + sdf.format(createDate) + " 当前日期：" + sdf.format(currentDate));
            return JsonUtil.getJson(-9, "订单创建时间不能大于系统当前时间");
        }
        // 计算结束时间
        Calendar date = Calendar.getInstance();
        date.setTime(createDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);
        Date endDate = null;
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            endDate = df.parse(df.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        compare = currentDate.compareTo(endDate);
        if (compare >= 0 && !WxpayUtil.isDebug()) {
            log.warn("非当天订单不允许退款！创建日期：" + createDate + " 当前日期：" + currentDate);
            return JsonUtil.getJson(-11, "非当天订单不允许退款");
        }
        // 创建退款请求builder，设置请求参数
        String outTradeNo = t_partner + t_out_trade_no + "T0"; // 交易流水号=合作方号+交易流水号
        try {
            PaymentItem paymentItem = paymentDao.getPaymentItem(paymentRecord.getItem());
            String merchant_id = paymentItem.getThirdPartySubaccount(); // 商户ID
            if (StringUtil.isEmpty(merchant_id)) {
                log.warn("收费项目未指定第三方商户号！t_partner：" + t_partner + " t_out_trade_no：" + t_out_trade_no);
                return JsonUtil.getJson(-13, "收费项目未指定第三方商户号");
            }
            TradeRefundRequestBuilder builder = new TradeRefundRequestBuilder(merchant_id);
            builder.setVpc_MerchTxnRef(outTradeNo);// 订单号
            builder.setVpc_TransNo(paymentRecord.getBankTradeNo());// 返回订单号（此值为中行订单号，取自查询结果接口中的vpc_TransactionNo字段）
            builder.setVpc_Amount(StringUtil.yuanToFen(t_total_fee));
            Map<String, Object> requestFields = builder.getBuildMap();
            //
            // String resQS = HttpUtils.post(BocpayConfig.VPCDPS_URL, requestFields, "UTF-8");
            String postData = BocUtils.getQueryUrlAllFields(requestFields);
            String resQS = BocUtils.doPost(BocpayConfig.getInstance().getVpcdpsUrl(), postData, false, "", 0);
            Map<String, Object> responseFields = BocUtils.createMapFromResponse(resQS);
            log.debug(">>>>TradeRefund.responseFields>>>>" + responseFields);

            // String transactionNo = BocUtils.null2unknown("vpc_TransactionNo", responseFields);
            String acqResponseCode = BocUtils.null2unknown("vpc_AcqResponseCode", responseFields);
            String txnResponseCode = BocUtils.null2unknown("vpc_TxnResponseCode", responseFields);

            if (txnResponseCode.equals("0") && acqResponseCode.equals("00")) {
                paymentRecord.setState(PaymentState.REFUND.getCode()); // 更新状态
                paymentDao.receivedPayment(paymentRecord);
                return JsonUtil.getJson(0, "订单已退款");
            } else {
                return JsonUtil.getJson(-10, "订单退款失败");
            }
        } catch (Exception e) {
            log.error("中行订单退款失败：" + e.getMessage(), e);
            return JsonUtil.getJson(-12, e.getMessage());
        }
    }

    /**
     * 下载中行对账结果
     *
     * @param bill_date 对账日期，格式：yyyyMMdd
     * @return
     */
    public LinkedList<ZhpayBill> DownloadBill(String bill_date) {
        // 1. 从FTP下载对账文件
        List<String> fileList = new ArrayList<String>(); // 对账文件列表
        try {
            // 对账文件本地保存路径
            //String localPath = FileUtil.getBptbPath("BOC/" + bill_date);
            String localPath = TysfConfig.getBptbPath("BOC/" + bill_date);
            // 获取FTP配置
            // FTPUtil ftp = new FTPUtil("166.111.14.107", 10022, "ftpboc", "44C9BF1B7B73DB8659ADD738EF142E08");
            String ftpIP = paymentDao.getSysParamsValue("BocPay", "FTP_IP", "166.111.14.107");
            String ftpPort = paymentDao.getSysParamsValue("BocPay", "FTP_PORT", "10022");
            String ftpUser = paymentDao.getSysParamsValue("BocPay", "FTP_USER", "ftpboc");
            String ftpPass = paymentDao.getSysParamsValue("BocPay", "FTP_PASS", "44C9BF1B7B73DB8659ADD738EF142E08");
            FTPUtil ftp = new FTPUtil(ftpIP, Integer.parseInt(ftpPort), ftpUser, ftpPass);
            List<String> list = ftp.getFileNameList(bill_date); // FTP上数据必须按日期保存
            for (String file : list) {
                log.debug(bill_date + "/" + file);
                // 下载文件
                if (ftp.downloadFile(bill_date, file, localPath)) {
                    fileList.add(localPath + file);
                    log.debug("中行对账文件" + file + "下载成功：" + localPath + file);
                } else {
                    log.error("中行对账文件" + file + "下载失败！");
                }
            }
        } catch (Exception e) {
            log.error("中行对账文件下载失败！" + e.getMessage(), e);
            return null;
        }
        if (fileList.size() == 0) {
            log.error(bill_date + "中行对账文件不存在！");
            return null;
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
                    return null;
                }
            } catch (Exception e) {
                log.error("解析中行对账数据失败！" + e.getMessage(), e);
                return null;
            }
        }
        return result;
    }

    private String TransformCharset(String text, String charset) throws UnsupportedEncodingException {
        if (null == text)
            return null;
        byte[] bytes = text.getBytes(charset);
        return new String(bytes);
    }

    private String DecodeBase64(String text) {
        if (null == text) {
            return null;
        }
        return new String(Base64.decode(text));
    }

    private IPaymentDao paymentDao = (IPaymentDao) SpringContextUtil.getBean("paymentDao");

    public IPaymentDao getPaymentDao() {
        return paymentDao;
    }

    public void setPaymentDao(IPaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }
}
