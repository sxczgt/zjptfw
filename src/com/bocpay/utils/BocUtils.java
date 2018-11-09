package com.bocpay.utils;

import com.bocpay.config.BocpayConfig;
import com.bocpay.model.BocpayMerchant;
import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.X509TrustManager;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.*;

@SuppressWarnings("deprecation")
public class BocUtils {
    public static X509TrustManager x509TrustManager = null;
    public static SSLSocketFactory sslSocketFactory = null;

    static {
        x509TrustManager = new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            public boolean isClientTrusted(X509Certificate[] chain) {
                return true;
            }

            public boolean isServerTrusted(X509Certificate[] chain) {
                return true;
            }
        };

        java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{x509TrustManager}, null);
            sslSocketFactory = context.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        // String SECURE_SECRET = "B659B3A5C8A2443EDD3FCB6C15AA2EE4";
        // String ss = new String(SECURE_SECRET.getBytes("ISO-8859-1"), "ISO-8859-1");
        // byte[] b = getBytesFromHEX(ss, 0, ss.length());
        // System.out.println("b1>>>>>>>>>" + Arrays.toString(b));// 字节数组打印
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("vpc_Command", "queryDR");
        param.put("vpc_AccessCode", "41EA4FC0");
        param.put("vpc_Merchant", "110082202460");
        // param.put("vpc_MerchTxnRef", "2999180710142629-10-012018430721");
        param.put("vpc_MerchTxnRef", "2999180711103932-10-012018280205");
        param.put("vpc_User", "tstrans");
        param.put("vpc_Password", "TsTrs2018");
        param.put("vpc_Version", "1");
        String data = getQueryUrlAllFields(param);
        String html = doPost("https://migs.mastercard.com.au/vpcdps", data, false, "", 0);
        System.out.println(html);
    }

    public static String doPost(String vpc_Host, String data, boolean useProxy, String proxyHost, int proxyPort) throws IOException {
        Socket socket = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            String fileName = "";
            int vpc_Port = 443;
            boolean useSSL = false;
            // determine if SSL encryption is being used
            if (vpc_Host.substring(0, 8).equalsIgnoreCase("HTTPS://")) {
                useSSL = true;
                // remove 'HTTPS://' from host URL
                vpc_Host = vpc_Host.substring(8);
                // get the filename from the last section of vpc_URL
                fileName = vpc_Host.substring(vpc_Host.lastIndexOf("/"));
                // get the IP address of the VPC machine
                vpc_Host = vpc_Host.substring(0, vpc_Host.lastIndexOf("/"));
            }

            // use the next block of code if using a proxy server
            if (useProxy) {
                socket = new Socket(proxyHost, proxyPort);
                os = socket.getOutputStream();
                is = socket.getInputStream();
                // use next block of code if using SSL encryption
                if (useSSL) {
                    String msg = "CONNECT " + vpc_Host + ":" + vpc_Port + " HTTP/1.0\r\n" + "User-Agent: HTTP Client\r\n\r\n";
                    os.write(msg.getBytes());
                    byte[] buf = new byte[4096];
                    int len = is.read(buf);
                    String res = new String(buf, 0, len);
                    if (os != null) {
                        os.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                    // check if a successful HTTP connection
                    if (res.indexOf("200") < 0) {
                        throw new IOException("Proxy would now allow connection - " + res);
                    }

                    // write output to VPC
                    SSLSocket ssl = (SSLSocket) sslSocketFactory.createSocket(socket, vpc_Host, vpc_Port, true);
                    ssl.startHandshake();
                    os = ssl.getOutputStream();
                    // get response data from VPC
                    is = ssl.getInputStream();
                    // use the next block of code if NOT using SSL encryption
                } else {
                    fileName = vpc_Host;
                }
                // use the next block of code if NOT using a proxy server
            } else {
                // use next block of code if using SSL encryption
                if (useSSL) {
                    socket = sslSocketFactory.createSocket(vpc_Host, vpc_Port);
                    os = socket.getOutputStream();
                    is = socket.getInputStream();
                    // use next block of code if NOT using SSL encryption
                } else {
                    socket = new Socket(vpc_Host, vpc_Port);
                    os = socket.getOutputStream();
                    is = socket.getInputStream();
                }
            }
            String req = "POST " + fileName + " HTTP/1.0\r\n" + "User-Agent: HTTP Client\r\n" + "Content-Type: application/x-www-form-urlencoded\r\n" + "Content-Length: " + data.length() + "\r\n\r\n" + data;

            os.write(req.getBytes());
            String res = new String(readAll(is));

            // check if a successful connection
            if (res.indexOf("200") < 0) {
                throw new IOException("Connection Refused - " + res);
            }

            if (res.indexOf("404 Not Found") > 0) {
                throw new IOException("File Not Found Error - " + res);
            }

            int resIndex = res.indexOf("\r\n\r\n");
            String body = res.substring(resIndex + 4, res.length());
            return body;
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
            if (socket != null) {
                socket.close();
            }
        }
    }

    // ----------------------------------------------------------------------------

    /**
     * This method is for sorting the fields and creating a SHA256 secure hash.
     *
     * @param fields is a map of all the incoming hey-value pairs from the VPC
     */
    public static String getQueryUrlAllFields(Map<String, Object> fields) {
        StringBuffer buf = new StringBuffer();
        // create a list
        List<String> fieldNames = new ArrayList<String>(fields.keySet());
        Iterator<String> itr = fieldNames.iterator();

        // move through the list and create a series of URL key/value pairs
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);

            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                buf.append(URLEncoder.encode(fieldName));
                buf.append('=');
                buf.append(URLEncoder.encode(fieldValue));
            }

            // add a '&' to the end if we have more fields coming.
            if (itr.hasNext()) {
                buf.append('&');
            }
        }
        return buf.toString();
    }

    public static String getSHAhashAllFields(Map<String, Object> fields) throws Exception {
        // create a list and sort it
        List<String> fieldNames = new ArrayList<String>(fields.keySet());
        Collections.sort(fieldNames);
        // create a buffer for the SHA256 input
        StringBuffer buf = new StringBuffer();
        // iterate through the list and add the remaining field values
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                buf.append(fieldName + "=" + fieldValue);
                if (itr.hasNext()) {
                    buf.append('&');
                }
            }
        }
        byte[] mac = null;
        // 根据商户ID获取商户密钥
        String merchant_id = (String) fields.get("vpc_Merchant");
        BocpayMerchant bm = BocpayConfig.getInstance().getMerchant(merchant_id);
        if (bm == null) {
            throw new Exception("商户号[" + merchant_id + "]配置信息不存在！");
        }
        String secure_secret = bm.getSecureSecret();
        //
        byte[] b = getBytesFromHEX(secure_secret, 0, secure_secret.length());
        SecretKey key = new SecretKeySpec(b, "HmacSHA256");
        Mac m = Mac.getInstance("HmacSHA256");
        m.init(key);
        m.update(buf.toString().getBytes("ISO-8859-1"));
        mac = m.doFinal();
        String hashValue = getHEXFromBytes(mac);
        return hashValue;
    }

    /**
     * 将十六进制字符串转成字节数组
     */
    private static byte[] getBytesFromHEX(String s, int offset, int length) {
        if ((length % 2) != 0)
            return null;
        byte[] byteArray = new byte[length / 2];
        int j = 0;
        int end = offset + length;
        for (int i = offset; i < end; i += 2) {
            int high_nibble = Character.digit(s.charAt(i), 16);
            int low_nibble = Character.digit(s.charAt(i + 1), 16);
            if (high_nibble == -1 || low_nibble == -1) {
                // illegal format
                return null;
            }
            byteArray[j++] = (byte) (((high_nibble << 4) & 0xf0) | (low_nibble & 0x0f));
        }
        return byteArray;
    }

    // ----------------------------------------------------------------------------

    /**
     * 十六进制字符码表
     */
    private static final char[] HEX_TABLE = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 将字节数组转成十六进制字符串
     */
    private static String getHEXFromBytes(byte[] input) {
        // create a StringBuffer 2x the size of the hash array
        StringBuffer sb = new StringBuffer(input.length * 2);
        // retrieve the byte array data, convert it to hex and add it to the StringBuffer
        for (int i = 0; i < input.length; i++) {
            sb.append(HEX_TABLE[(input[i] >> 4) & 0xf]);
            sb.append(HEX_TABLE[input[i] & 0xf]);
        }
        return sb.toString();
    }

    // ----------------------------------------------------------------------------

    /*
     * This method takes a data String and returns a predefined value if empty If data Sting is null, returns string "No Value Returned", else returns input
     *
     * @param in String containing the data String
     *
     * @return String containing the output String
     */
    public static String null2unknown(String in) {
        if (in == null || in.length() == 0) {
            return "No Value Returned";
        } else {
            return in;
        }
    }

    /*
     * This method takes a data String and returns a predefined value if empty If data Sting is null, returns string "No Value Returned", else returns input
     *
     * @param in String containing the data String
     *
     * @return String containing the output String
     */
    public static String null2unknown(String in, Map<String, Object> responseFields) {
        if (in == null || in.length() == 0 || responseFields == null || (String) responseFields.get(in) == null) {
            return "No Value Returned";
        } else {
            return (String) responseFields.get(in);
        }
    }// null2unknown()

    // ----------------------------------------------------------------------------

    /*
     * This function uses the returned status code retrieved from the Digital Response and returns an appropriate description for the code
     *
     * @param vResponseCode String containing the vpc_TxnResponseCode
     *
     * @return description String containing the appropriate description
     */
    public static String getResponseDescription(String vResponseCode) {
        String result = "";
        // check if a single digit response code
        if (vResponseCode != null && vResponseCode.length() != 1) {
            // Java cannot switch on a string so turn everything to a char
            char input = vResponseCode.charAt(0);
            switch (input) {
                case '0':
                    result = "Transaction Successful";
                    break;
                case '1':
                    result = "Transaction Declined";
                    break;
                case '2':
                    result = "Bank Declined Transaction";
                    break;
                case '3':
                    result = "No Reply from Bank";
                    break;
                case '4':
                    result = "Expired Card";
                    break;
                case '5':
                    result = "Insufficient Funds";
                    break;
                case '6':
                    result = "Error Communicating with Bank";
                    break;
                case '7':
                    result = "Payment Server detected an error";
                    break;
                case '8':
                    result = "Transaction Type Not Supported";
                    break;
                case '9':
                    result = "Bank declined transaction (Do not contact Bank)";
                    break;
                case 'A':
                    result = "Transaction Aborted";
                    break;
                case 'B':
                    result = "Transaction Declined - Contact the Bank";
                    break;
                case 'C':
                    result = "Transaction Cancelled";
                    break;
                case 'D':
                    result = "Deferred transaction has been received and is awaiting processing";
                    break;
                case 'E':
                    result = "Transaction Declined - Refer to card issuer";
                    break;
                case 'F':
                    result = "3-D Secure Authentication failed";
                    break;
                case 'I':
                    result = "Card Security Code verification failed";
                    break;
                case 'L':
                    result = "Shopping Transaction Locked (Please try the transaction again later)";
                    break;
                case 'M':
                    result = "Transaction Submitted (No response from acquirer)";
                    break;
                case 'N':
                    result = "Cardholder is not enrolled in Authentication scheme";
                    break;
                case 'P':
                    result = "Transaction has been received by the Payment Adaptor and is being processed";
                    break;
                case 'R':
                    result = "Transaction was not processed - Reached limit of retry attempts allowed";
                    break;
                case 'S':
                    result = "Duplicate SessionID";
                    break;
                case 'T':
                    result = "Address Verification Failed";
                    break;
                case 'U':
                    result = "Card Security Code Failed";
                    break;
                case 'V':
                    result = "Address Verification and Card Security Code Failed";
                    break;
                case '?':
                    result = "Transaction status is unknown";
                    break;
                default:
                    result = "Unable to be determined";
                    break;
            }

            return result;
        } else {
            return "No Value Returned";
        }
    } // getResponseDescription()

    // ----------------------------------------------------------------------------

    /**
     * This function uses the QSI AVS Result Code retrieved from the Digital Receipt and returns an appropriate description for this code.
     *
     * @param vAVSResultCode String containing the vpc_AVSResultCode
     * @return description String containing the appropriate description
     */
    public static String displayAVSResponse(String vAVSResultCode) {
        String result = "";
        // if (vCSCResultCode != null || vCSCResultCode.length() == 0) { //示例代码逻辑有问题
        if (vAVSResultCode != null && vAVSResultCode.length() > 0) {
            if (vAVSResultCode.equalsIgnoreCase("Unsupported") || vAVSResultCode.equalsIgnoreCase("No Value Returned")) {
                result = "AVS not supported or there was no AVS data provided";
            } else {
                // Java cannot switch on a string so turn everything to a char
                char input = vAVSResultCode.charAt(0);
                switch (input) {
                    case 'X':
                        result = "Exact match - address and 9 digit ZIP/postal code";
                        break;
                    case 'Y':
                        result = "Exact match - address and 5 digit ZIP/postal code";
                        break;
                    case 'S':
                        result = "Service not supported or address not verified (international transaction)";
                        break;
                    case 'G':
                        result = "Issuer does not participate in AVS (international transaction)";
                        break;
                    case 'C':
                        result = "Street Address and Postal Code not verified for International Transaction due to incompatible formats.";
                        break;
                    case 'I':
                        result = "Visa Only. Address information not verified for international transaction.";
                        break;
                    case 'A':
                        result = "Address match only";
                        break;
                    case 'W':
                        result = "9 digit ZIP/postal code matched, Address not Matched";
                        break;
                    case 'Z':
                        result = "5 digit ZIP/postal code matched, Address not Matched";
                        break;
                    case 'R':
                        result = "Issuer system is unavailable";
                        break;
                    case 'U':
                        result = "Address unavailable or not verified";
                        break;
                    case 'E':
                        result = "Address and ZIP/postal code not provided";
                        break;
                    case 'B':
                        result = "Street Address match for international transaction. Postal Code not verified due to incompatible formats.";
                        break;
                    case 'N':
                        result = "Address and ZIP/postal code not matched";
                        break;
                    case '0':
                        result = "AVS not requested";
                        break;
                    case 'D':
                        result = "Street Address and postal code match for international transaction.";
                        break;
                    case 'M':
                        result = "Street Address and postal code match for international transaction.";
                        break;
                    case 'P':
                        result = "Postal Codes match for international transaction but street address not verified due to incompatible formats.";
                        break;
                    case 'K':
                        result = "Card holder name only matches.";
                        break;
                    case 'F':
                        result = "Street address and postal code match. Applies to U.K. only.";
                        break;
                    default:
                        result = "Unable to be determined";
                        break;
                }
            }
        } else {
            result = "null response";
        }
        return result;
    }

    // ----------------------------------------------------------------------------

    /**
     * This function uses the QSI CSC Result Code retrieved from the Digital Receipt and returns an appropriate description for this code.
     *
     * @param vCSCResultCode String containing the vpc_CSCResultCode
     * @return description String containing the appropriate description
     */
    public static String displayCSCResponse(String vCSCResultCode) {
        String result = "";
        // if (vCSCResultCode != null || vCSCResultCode.length() == 0) {
        if (vCSCResultCode != null && vCSCResultCode.length() > 0) {
            if (vCSCResultCode.equalsIgnoreCase("Unsupported") || vCSCResultCode.equalsIgnoreCase("No Value Returned")) {
                result = "CSC not supported or there was no CSC data provided";
            } else {
                // Java cannot switch on a string so turn everything to a char
                char input = vCSCResultCode.charAt(0);
                switch (input) {
                    case 'M':
                        result = "Exact code match";
                        break;
                    case 'S':
                        result = "Merchant has indicated that CSC is not present on the card (MOTO situation)";
                        break;
                    case 'P':
                        result = "Code not processed";
                        break;
                    case 'U':
                        result = "Card issuer is not registered and/or certified";
                        break;
                    case 'N':
                        result = "Code invalid or not matched";
                        break;
                    default:
                        result = "Unable to be determined";
                }
            }
        } else {
            result = "null response";
        }
        return result;
    }

    // ----------------------------------------------------------------------------

    /**
     * This method uses the 3DS verStatus retrieved from the Response and returns an appropriate description for this code.
     *
     * @param vStatus String containing the status code
     * @return description String containing the appropriate description
     */
    public static String getStatusDescription(String vStatus) {
        String result = "";
        if (vStatus != null && !vStatus.equals("")) {
            if (vStatus.equalsIgnoreCase("Unsupported") || vStatus.equals("No Value Returned")) {
                result = "3DS not supported or there was no 3DS data provided";
            } else {
                // Java cannot switch on a string so turn everything to a character
                char input = vStatus.charAt(0);
                switch (input) {
                    case 'Y':
                        result = "The cardholder was successfully authenticated.";
                        break;
                    case 'E':
                        result = "The cardholder is not enrolled.";
                        break;
                    case 'N':
                        result = "The cardholder was not verified.";
                        break;
                    case 'U':
                        result = "The cardholder's Issuer was unable to authenticate due to some system error at the Issuer.";
                        break;
                    case 'F':
                        result = "There was an error in the format of the request from the merchant.";
                        break;
                    case 'A':
                        result = "Authentication of your Merchant ID and Password to the ACS Directory Failed.";
                        break;
                    case 'D':
                        result = "Error communicating with the Directory Server.";
                        break;
                    case 'C':
                        result = "The card type is not supported for authentication.";
                        break;
                    case 'S':
                        result = "The signature on the response received from the Issuer could not be validated.";
                        break;
                    case 'P':
                        result = "Error parsing input from Issuer.";
                        break;
                    case 'I':
                        result = "Internal Payment Server system error.";
                        break;
                    default:
                        result = "Unable to be determined";
                        break;
                }
            }
        } else {
            result = "null response";
        }
        return result;
    }

    // ----------------------------------------------------------------------------
    /**
     * This method is for performing a Form POST operation from input data parameters.
     *
     * @param vpc_Host - is a String containing the vpc URL
     * @param data - is a String containing the post data key value pairs
     * @param useProxy - is a boolean indicating if a Proxy Server is involved in the transfer
     * @param proxyHost - is a String containing the IP address of the Proxy to send the data to
     * @param proxyPort - is an integer containing the port number of the Proxy socket listener
     * @return - is body data of the POST data response
     */
    // public static String doPost(String vpc_Host, String data, boolean useProxy, String proxyHost, int proxyPort) throws IOException {
    //
    // InputStream is;
    // OutputStream os;
    // int vpc_Port = 443;
    // String fileName = "";
    // boolean useSSL = false;
    //
    // // determine if SSL encryption is being used
    // if (vpc_Host.substring(0, 8).equalsIgnoreCase("HTTPS://")) {
    // useSSL = true;
    // // remove 'HTTPS://' from host URL
    // vpc_Host = vpc_Host.substring(8);
    // // get the filename from the last section of vpc_URL
    // fileName = vpc_Host.substring(vpc_Host.lastIndexOf("/"));
    // // get the IP address of the VPC machine
    // vpc_Host = vpc_Host.substring(0, vpc_Host.lastIndexOf("/"));
    // }
    //
    // // use the next block of code if using a proxy server
    // if (useProxy) {
    // Socket s = new Socket(proxyHost, proxyPort);
    // os = s.getOutputStream();
    // is = s.getInputStream();
    // // use next block of code if using SSL encryption
    // if (useSSL) {
    // String msg = "CONNECT " + vpc_Host + ":" + vpc_Port + " HTTP/1.0\r\n" + "User-Agent: HTTP Client\r\n\r\n";
    // os.write(msg.getBytes());
    // byte[] buf = new byte[4096];
    // int len = is.read(buf);
    // String res = new String(buf, 0, len);
    //
    // // check if a successful HTTP connection
    // if (res.indexOf("200") < 0) {
    // throw new IOException("Proxy would now allow connection - " + res);
    // }
    //
    // // write output to VPC
    // SSLSocket ssl = (SSLSocket) s_sslSocketFactory.createSocket(s, vpc_Host, vpc_Port, true);
    // ssl.startHandshake();
    // os = ssl.getOutputStream();
    // // get response data from VPC
    // is = ssl.getInputStream();
    // // use the next block of code if NOT using SSL encryption
    // } else {
    // fileName = vpc_Host;
    // }
    // // use the next block of code if NOT using a proxy server
    // } else {
    // // use next block of code if using SSL encryption
    // if (useSSL) {
    // Socket s = s_sslSocketFactory.createSocket(vpc_Host, vpc_Port);
    // os = s.getOutputStream();
    // is = s.getInputStream();
    // // use next block of code if NOT using SSL encryption
    // } else {
    // Socket s = new Socket(vpc_Host, vpc_Port);
    // os = s.getOutputStream();
    // is = s.getInputStream();
    // }
    // }
    //
    // String req = "POST " + fileName + " HTTP/1.0\r\n" + "User-Agent: HTTP Client\r\n" + "Content-Type: application/x-www-form-urlencoded\r\n" + "Content-Length: " + data.length() + "\r\n\r\n" + data;
    //
    // os.write(req.getBytes());
    // String res = new String(readAll(is));
    //
    // // check if a successful connection
    // if (res.indexOf("200") < 0) {
    // throw new IOException("Connection Refused - " + res);
    // }
    //
    // if (res.indexOf("404 Not Found") > 0) {
    // throw new IOException("File Not Found Error - " + res);
    // }
    //
    // int resIndex = res.indexOf("\r\n\r\n");
    // String body = res.substring(resIndex + 4, res.length());
    // return body;
    // }

    /**
     * This method is for creating a byte array from input stream data.
     *
     * @param is - the input stream containing the data
     * @return is the byte array of the input stream data
     */
    private static byte[] readAll(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        while (true) {
            int len = is.read(buf);
            if (len < 0) {
                break;
            }
            baos.write(buf, 0, len);
        }
        return baos.toByteArray();
    }

    /**
     * This method is for creating a URL POST data string.
     *
     * @param queryString is the input String from POST data response
     * @return is a Hashmap of Post data response inputs
     */
    public static Map<String, Object> createMapFromResponse(String queryString) {
        Map<String, Object> map = new HashMap<String, Object>();
        StringTokenizer st = new StringTokenizer(queryString, "&");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            int i = token.indexOf('=');
            if (i > 0) {
                try {
                    String key = token.substring(0, i);
                    String value = URLDecoder.decode(token.substring(i + 1, token.length()));
                    map.put(key, value);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return map;
    }
}
