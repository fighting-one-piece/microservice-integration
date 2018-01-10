package org.cisiondata.modules.kyfw.service.impl;

import org.cisiondata.modules.kyfw.crawler.KyfwHandler;
import org.cisiondata.modules.kyfw.crawler.KyfwS01LoginInit;
import org.cisiondata.modules.kyfw.crawler.KyfwS02CaptchaImage;
import org.cisiondata.modules.kyfw.crawler.KyfwS03CaptchaCheck;
import org.cisiondata.modules.kyfw.crawler.KyfwS04Login;
import org.cisiondata.modules.kyfw.crawler.KyfwS08AuthUamtk;
import org.cisiondata.modules.kyfw.crawler.KyfwS10LeftTicket;
import org.cisiondata.modules.kyfw.crawler.KyfwS12SubmitOrderRequest;
import org.cisiondata.modules.kyfw.crawler.KyfwS14ConfirmPassengerInitDc;
import org.cisiondata.modules.kyfw.crawler.KyfwS15GetPassengers;
import org.cisiondata.modules.kyfw.crawler.KyfwS16CheckOrderInfo;
import org.cisiondata.modules.kyfw.crawler.KyfwS18GetQueueCount;
import org.cisiondata.modules.kyfw.crawler.KyfwS20ConfirmSingleForQueue;
import org.cisiondata.modules.kyfw.crawler.KyfwS22QueryOrderWaitTime;
import org.cisiondata.modules.kyfw.crawler.KyfwS24ResultOrderForDcQueue;

public class TrainTicketServiceImpl {
	
	public static void main(String[] args) {
		KyfwHandler step01 = new KyfwS01LoginInit();
		KyfwHandler step02 = new KyfwS02CaptchaImage();
		KyfwHandler step03 = new KyfwS03CaptchaCheck();
		KyfwHandler step04 = new KyfwS04Login();
//		KyfwHandler step05 = new KyfwS05PassportRedirect();
		KyfwHandler step06 = new KyfwS08AuthUamtk();
		KyfwHandler step07 = new KyfwS10LeftTicket();
		KyfwHandler step08 = new KyfwS12SubmitOrderRequest();
		KyfwHandler step09 = new KyfwS14ConfirmPassengerInitDc();
		KyfwHandler step10 = new KyfwS15GetPassengers();
		KyfwHandler step11 = new KyfwS16CheckOrderInfo();
		KyfwHandler step12 = new KyfwS18GetQueueCount();
		KyfwHandler step13 = new KyfwS20ConfirmSingleForQueue();
		KyfwHandler step14 = new KyfwS22QueryOrderWaitTime();
		KyfwHandler step15 = new KyfwS24ResultOrderForDcQueue();
		
		step01.setNextHandler(step02);
		step02.setNextHandler(step03);
		step03.setNextHandler(step04);
		step04.setNextHandler(step06);
//		step05.setNextHandler(step06);
		step06.setNextHandler(step07);
		step07.setNextHandler(step08);
		step08.setNextHandler(step09);
		step09.setNextHandler(step10);
		step10.setNextHandler(step11);
		step11.setNextHandler(step12);
		step12.setNextHandler(step13);
		step13.setNextHandler(step14);
		step14.setNextHandler(step15);
		step01.handleChain(null);
	}

}
