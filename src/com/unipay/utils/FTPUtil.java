package com.unipay.utils;

import cn.tsinghua.sftp.config.TysfConfig;
import cn.tsinghua.sftp.util.OSUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FTP工具类
 */
public class FTPUtil {
    /**
     * FTP地址
     */
    private String ftpHost;
    /**
     * FTP端口
     */
    private int ftpPort;
    /**
     * FTP登录用户名
     */
    private String ftpUser;
    /**
     * FTP登录密码
     */
    private String ftpPass;

    public static void main(String[] args) {
        // "166.111.14.107", 10021, "ftpccb", "C1A0503C23BE5B34C411B94D73B86EF0","D:\ftpfiles\ftpccb"
        // "166.111.14.107", 10022, "ftpboc", "44C9BF1B7B73DB8659ADD738EF142E08","D:\ftpfiles\ftpboc"
        FTPUtil ftp = new FTPUtil("166.111.14.107", 10022, "ftpboc", "44C9BF1B7B73DB8659ADD738EF142E08");
        // 上传文件
        // String remotePath = DateUtils.getNow("yyyyMMdd");
        String remotePath = "20180628";
        // boolean result = ftp.uploadFile(remotePath, DateUtils.getNow("yyyyMMddHHmmssSSS"), "D:\\EACQ_tsinghua_20180628.txt");
        // if (!ftp.uploadFile(remotePath, "EACQ_tsinghua_20180628.txt", "D:\\EACQ_tsinghua_20180628.txt")) {
        // return;
        // }
        // 删除文件
        // ftp.uploadFile(remotePath, "D:\\EACQ_tsinghua_20180628.txt");
        // ftp.deleteFile(remotePath, "EACQ_tsinghua_20180628.txt");
        // 列表文件
        //String localPath = FileUtil.getBptbPath("BOC/20180628");
        String localPath = TysfConfig.getBptbPath("BOC/20180628");
        List<String> list = ftp.getFileNameList(remotePath);
        for (String file : list) {
            System.out.println(remotePath + "/" + file);
            // 下载文件
            if (ftp.downloadFile(remotePath, file, localPath)) {
                System.out.println("文件" + file + "下载成功：" + localPath + file);
            } else {
                System.out.println("文件" + file + "下载失败！");
            }
        }
    }

    /**
     * 构造函数
     *
     * @param ftpHost FTP地址
     * @param ftpPort FTP端口
     * @param ftpUser FTP登录用户名
     * @param ftpPass FTP登录密码
     */
    public FTPUtil(String ftpHost, int ftpPort, String ftpUser, String ftpPass) {
        this.ftpHost = ftpHost;
        this.ftpPort = ftpPort;
        this.ftpUser = ftpUser;
        this.ftpPass = ftpPass;
    }

    /**
     * 从FTP服务器列出指定文件夹下文件名列表
     *
     * @param remotePath FTP服务器上的相对路径
     * @return List<String> 文件名列表，如果出现异常返回null
     */
    public List<String> getFileNameList(String remotePath) {
        // 目录列表记录
        List<String> fileNames = new ArrayList<String>();
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("UTF-8");
        try {
            ftp.connect(ftpHost, ftpPort);
            // 如果采用默认端口，可以使用ftp.connect(ftpHost)的方式直接连接FTP服务器
            ftp.login(ftpUser, ftpPass);// 登录
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return null;
            }
            if (!OSUtil.isWindows()) {
                ftp.enterLocalPassiveMode(); // 解决Linux阻塞问题
            }
            // 转移到FTP服务器目录
            if (ftp.changeWorkingDirectory(remotePath)) {
                FTPFile[] files = ftp.listFiles();
                for (FTPFile file : files) {
                    fileNames.add(file.getName());
                }
            }
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return fileNames;
    }

    /**
     * 上传本地文件到FTP服务器
     *
     * @param remotePath  FTP服务器保存目录
     * @param fileName    上传到FTP服务器后的文件名称
     * @param inputStream 上传文件流
     * @return 成功返回true，否则返回false
     */
    public boolean uploadFile(String remotePath, String fileName, InputStream inputStream) {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("UTF-8");
        try {
            // 连接FTP服务器
            ftp.connect(ftpHost, ftpPort);
            // 登录FTP服务器
            ftp.login(ftpUser, ftpPass);
            // 是否成功登录FTP服务器
            int replyCode = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                return success;
            }
            if (!OSUtil.isWindows()) {
                ftp.enterLocalPassiveMode(); // 解决Linux阻塞问题
            }
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.makeDirectory(remotePath);
            ftp.changeWorkingDirectory(remotePath);
            ftp.enterLocalPassiveMode();
            ftp.storeFile(fileName, inputStream);
            inputStream.close();
            ftp.logout();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    /**
     * 上传本地文件到FTP服务器（可对文件进行重命名）
     *
     * @param remotePath FTP服务器保存目录
     * @param fileName   上传到FTP服务器后的文件名称
     * @param localFile  待上传文件的名称（绝对地址）
     * @return 成功返回true，否则返回false
     */
    public boolean uploadFile(String remotePath, String fileName, String localFile) {
        boolean success = false;
        try {
            InputStream inputStream = new FileInputStream(new File(localFile));
            success = uploadFile(remotePath, fileName, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * 上传本地文件到FTP服务器（不可以进行文件的重命名操作）
     *
     * @param remotePath FTP服务器保存目录
     * @param localFile  待上传文件的名称（绝对地址）
     * @return 成功返回true，否则返回false
     */
    public boolean uploadFile(String remotePath, String localFile) {
        boolean success = false;
        try {
            String fileName = new File(localFile).getName();
            InputStream inputStream = new FileInputStream(new File(localFile));
            success = uploadFile(remotePath, fileName, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * 删除FTP服务器文件
     *
     * @param remotePath FTP服务器保存目录
     * @param fileName   要删除的文件名称
     * @return 成功返回true，否则返回false
     */
    public boolean deleteFile(String remotePath, String fileName) {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("UTF-8");
        try {
            // 连接FTP服务器
            ftp.connect(ftpHost, ftpPort);
            // 登录FTP服务器
            ftp.login(ftpUser, ftpPass);
            // 验证FTP服务器是否登录成功
            int replyCode = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                return success;
            }
            if (!OSUtil.isWindows()) {
                ftp.enterLocalPassiveMode(); // 解决Linux阻塞问题
            }
            // 切换FTP目录
            ftp.changeWorkingDirectory(remotePath);
            ftp.dele(fileName);
            ftp.logout();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }

    /**
     * 从FTP服务器下载指定文件名的文件
     *
     * @param remotePath FTP服务器上的相对路径
     * @param fileName   要下载的文件名
     * @param localPath  下载后保存到本地的路径
     * @return 成功返回true，否则返回false
     */
    public boolean downloadFile(String remotePath, String fileName, String localPath) {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        try {
            ftp.setControlEncoding("UTF-8");
            ftp.setConnectTimeout(15 * 1000);
            ftp.setDataTimeout(30 * 1000);
            ftp.connect(ftpHost, ftpPort); // 连接FTP服务器
            ftp.setSoTimeout(60 * 1000);
            ftp.login(ftpUser, ftpPass); // 登录FTP服务器
            // 验证FTP服务器是否登录成功
            int replyCode = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                return success;
            }
            if (!OSUtil.isWindows()) {
                ftp.enterLocalPassiveMode(); // 解决Linux阻塞问题
            }
            // 切换FTP目录
            ftp.changeWorkingDirectory(remotePath);
            FTPFile[] ftpFiles = ftp.listFiles();
            for (FTPFile file : ftpFiles) {
                if (fileName.equalsIgnoreCase(file.getName())) {
                    File localFile = new File(localPath + "/" + file.getName());
                    OutputStream os = new FileOutputStream(localFile);
                    ftp.retrieveFile(file.getName(), os);
                    os.close();
                }
            }
            ftp.logout();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException e) {
                }
            }
        }
        return success;
    }

}
