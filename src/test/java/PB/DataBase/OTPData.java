package PB.DataBase;

import org.w3c.dom.NodeList;

/**
 * Created with IntelliJ IDEA.
 * User: Oks
 * Date: 25.08.14
 * Time: 21:48
 * To change this template use File | Settings | File Templates.
 */
public class OTPData {
	public OTPData() {}

	public OTPData(String timeStamp, String otp, String sms, String phoneNumber)
	{
		this.timeStamp = timeStamp;
		this.otp = otp;
		this.sms = sms;
		this.phoneNumber = phoneNumber;
	}

	String id = null;
	String timeStamp = null;
	String otp = null;
	String sms = null;
	String phoneNumber = null;
}
