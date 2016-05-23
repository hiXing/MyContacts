package hixing.contacts.uitl;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * 邮件发送工具类
 */
public class SendEmailUtils {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
	private final static String MAIL_FROM_ACCOUNT = "";
	private final static String MAIL_FROM_PASSWORD = "";
	private final static String[] MAIL_TO_ACCOUNTS = { "" };
	private final static String MAIL_HOST = "smtp.163.com";
	private final static String MAIL_PORT = "25";
	private final static String MAIL_PROTOCOL = "smtp";
	private final static String MAIL_CONTENT_TYPE = "text/html; charset=utf-8";

	/**
	 * 获取机型信息
	 * 
	 * @return
	 */
	private static String getDeviceInfo() {
		final StringBuilder result = new StringBuilder();
		final Field[] fields_build = Build.class.getFields();
		// 设备信息
		for (final Field field : fields_build) {
			String name = field.getName();
			result.append("<DIV><FONT color=#0000ff size=2 face=宋体>");
			result.append(parseDeviceStr(name));
			try {
				result.append(field.get(null).toString());
			} catch (Exception e) {
				result.append("N/A 未知");
			}
			result.append("</FONT></DIV>");
		}
		// 版本信息
		final Field[] fields_version = Build.VERSION.class.getFields();
		for (final Field field : fields_version) {
			String name = field.getName();
			result.append("<DIV><FONT color=#0000ff size=2 face=宋体>");
			result.append(parseDeviceStr(name));
			try {
				result.append(field.get(null).toString());
			} catch (Exception e) {
				result.append("N/A 未知");
			}
			result.append("</FONT></DIV>");
		}
		return result.toString();
	}

	/**
	 * 解析机型
	 * 
	 * @param name
	 * @return
	 */
	private static String parseDeviceStr(String name) {
		if (name.equalsIgnoreCase("IMEI")) { // 设备串号
			return "【设备串号】IMEI：";
		} else if (name.equalsIgnoreCase("BOARD")) { // 主板
			return "【主板】BOARD：";
		} else if (name.equalsIgnoreCase("BOOTLOADER")) { // 引导
			return "【引导】BOOTLOADER：";
		} else if (name.equalsIgnoreCase("BRAND")) { // Android系统定制商
			return "【Android系统定制商】BRAND：";
		} else if (name.equalsIgnoreCase("CPU_ABI")) { // CPU指令集
			return "【CPU指令集】CPU_ABI：";
		} else if (name.equalsIgnoreCase("CPU_ABI2")) { // CPU指令集
			return "【CPU指令集】CPU_ABI2：";
		} else if (name.equalsIgnoreCase("DEVICE")) { // 设备参数
			return "【设备参数】DEVICE：";
		} else if (name.equalsIgnoreCase("DISPLAY")) { // 显示屏参数
			return "【显示屏参数】DISPLAY：";
		} else if (name.equalsIgnoreCase("FINGERPRINT")) { // 硬件名称
			return "【硬件名称】FINGERPRINT：";
		} else if (name.equalsIgnoreCase("HARDWARE")) { // 硬件
			return "【硬件】HARDWARE：";
		} else if (name.equalsIgnoreCase("HOST")) { // 主机
			return "【主机】HOST：";
		} else if (name.equalsIgnoreCase("ID")) { // 修订版本列表
			return "【修订版本列表 】ID：";
		} else if (name.equalsIgnoreCase("MANUFACTURER")) { // 硬件制造商
			return "【硬件制造商】MANUFACTURER：";
		} else if (name.equalsIgnoreCase("MODEL")) { // 机型
			return "【机型】MODEL：";
		} else if (name.equalsIgnoreCase("PRODUCT")) { // 手机制造商
			return "【手机制造商】PRODUCT：";
		} else if (name.equalsIgnoreCase("RADIO")) { // 无线电通讯
			return "【无线电通讯】RADIO：";
		} else if (name.equalsIgnoreCase("SERIAL")) { // 序列
			return "【序列】SERIAL：";
		} else if (name.equalsIgnoreCase("TAGS")) { // 描述build的标签
			return "【描述build的标签 】TAGS：";
		} else if (name.equalsIgnoreCase("TIME")) { // 时间
			return "【时间】TIME：";
		} else if (name.equalsIgnoreCase("TYPE")) { // builder类型
			return "【builder类型 】TYPE：";
		} else if (name.equalsIgnoreCase("UNKNOWN")) { // 未知
			return "【未知】UNKNOWN：";
		} else if (name.equalsIgnoreCase("USER")) { // 用户
			return "【用户】USER：";
		} else if (name.equalsIgnoreCase("BASEBAND")) { // 基带
			return "【基带】BASEBAND：";
		} else if (name.equalsIgnoreCase("CODENAME")) { // 当前开发代号
			return "【当前开发代号】CODENAME：";
		} else if (name.equalsIgnoreCase("HW_VERSION")) { // 硬件版本
			return "【硬件版本】HW_VERSION：";
		} else if (name.equalsIgnoreCase("INCREMENTAL")) { // 源码控制版本号
			return "【源码控制版本号】INCREMENTAL：";
		} else if (name.equalsIgnoreCase("RELEASE")) { // 系统版本号
			return "【系统版本号 】RELEASE：";
		} else if (name.equalsIgnoreCase("SDK")) { // SDK版本号
			return "【SDK版本号】SDK：";
		} else if (name.equalsIgnoreCase("SDK_INT")) { // SDK_INT版本号
			return "【SDK_INT版本号】SDK_INT：";
		} else if (name.equalsIgnoreCase("SW_VERSION")) { // 软件版本
			return "【软件版本】SW_VERSION：";
		} else if (name.equalsIgnoreCase("AppVerName")) { // 客户端版本
			return "【客户端版本】AppVerName：";
		} else { // 未知
			return "【未知】" + name + "：";
		}
	}

	/**
	 * 将信息发送email
	 * 
	 * @param attachmentPath
	 *            String 附件的绝地地址路径
	 * @throws MessagingException
	 */
	public static void sendClientEmail(String attachmentPath, Context context) throws MessagingException {
		SendEmailUtils sender = new SendEmailUtils();
		// 设置服务器地址和端口
		sender.setProperties(MAIL_HOST, MAIL_PORT);
		// 分别设置发件人，邮件标题和文本内容
		StringBuilder title = new StringBuilder();
		title.append("【" + AppInfoUtils.getAppName(context) + "-客户端日志】");
		title.append("【");
		title.append(AppInfoUtils.getVersionName(context));
		title.append("】");
		title.append("【");
		title.append(AppInfoUtils.getSvnVersionName(context));
		title.append("】");
		title.append("【");
//		title.append(DateUtils.getCurrentTime());
		title.append("】");

		StringBuilder content = new StringBuilder();
		content.append("您好：\n\n").append("机型:").append(Build.MODEL).append("\n\n");
		content.append("厂商:").append(Build.PRODUCT).append("\n\n");
		content.append("日期:").append(DATE_FORMAT.format(new Date())).append("\n\n\n\n");
		content.append("详细设备信息:").append(getDeviceInfo()).append("\n\n");

		sender.setMessage(MAIL_FROM_ACCOUNT, title.toString(), content.toString());
		// 设置收件人
		String[] toEmails = MAIL_TO_ACCOUNTS;
		sender.setReceiver(toEmails);
		// 添加附件
		sender.addAttachment(attachmentPath);
		// 发送邮件
		sender.sendEmail(MAIL_HOST, MAIL_FROM_ACCOUNT, MAIL_FROM_PASSWORD);
		Log.e("test", "------------------------发送邮件");
	}

	public Properties mProperties;
	public Session mSession;
	public Message mMessage;
	public MimeMultipart mMultipart;

	public SendEmailUtils() {
		super();
		this.mProperties = new Properties();
	}

	/**
	 * 添加附件
	 * 
	 * @param filePath
	 *            文件路径
	 * @throws MessagingException
	 */
	public void addAttachment(String filePath) throws MessagingException {
		FileDataSource fileDataSource = new FileDataSource(new File(filePath));
		DataHandler dataHandler = new DataHandler(fileDataSource);
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setDataHandler(dataHandler);
		mimeBodyPart.setFileName(fileDataSource.getName());
		this.mMultipart.addBodyPart(mimeBodyPart);
	}

	/**
	 * 发送邮件
	 * 
	 * @param host
	 *            地址
	 * @param account
	 *            账户名
	 * @param pwd
	 *            密码
	 * @throws MessagingException
	 */
	public void sendEmail(String host, String account, String pwd) throws MessagingException {
		// 发送时间
		this.mMessage.setSentDate(new Date());
		// 发送的内容，文本和附件
		this.mMessage.setContent(this.mMultipart);
		this.mMessage.saveChanges();
		// 创建邮件发送对象，并指定其使用SMTP协议发送邮件
		Transport transport = mSession.getTransport(MAIL_PROTOCOL);
		// 登录邮箱
		transport.connect(host, account, pwd);
		// 发送邮件
		transport.sendMessage(mMessage, mMessage.getAllRecipients());
		// 关闭连接
		transport.close();
	}

	/**
	 * 设置邮件
	 * 
	 * @param from
	 *            来源
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void setMessage(String from, String title, String content) throws AddressException,

	MessagingException {
		this.mMessage.setFrom(new InternetAddress(from));
		this.mMessage.setSubject(title);
		// 纯文本的话用setText()就行，不过有附件就显示不出来内容了
		MimeBodyPart textBody = new MimeBodyPart();
		textBody.setContent(content, MAIL_CONTENT_TYPE);
		this.mMultipart.addBodyPart(textBody);
	}

	/**
	 * 设置邮件属性
	 * 
	 * @param host
	 * @param post
	 */
	public void setProperties(String host, String port) {
		// 地址
		this.mProperties.put("mail.smtp.host", host);
		// 端口号
		this.mProperties.put("mail.smtp.post", port);
		// 是否验证
		this.mProperties.put("mail.smtp.auth", true);
		this.mSession = Session.getInstance(mProperties);
		this.mMessage = new MimeMessage(mSession);
		this.mMultipart = new MimeMultipart("mixed");
	}

	/**
	 * 设置收件人
	 * 
	 * @param receiver
	 * @throws MessagingException
	 */
	public void setReceiver(String[] receiver) throws MessagingException {
		Address[] address = new InternetAddress[receiver.length];
		for (int i = 0; i < receiver.length; i++) {
			address[i] = new InternetAddress(receiver[i]);
		}
		this.mMessage.setRecipients(Message.RecipientType.TO, address);
	}
}
