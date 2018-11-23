package org.platform.modules.recharge.entity;

public enum TradeStatus {
	
	INITIAL("INITIAL"),
	SUCCESS("SUCCESS"),
	FAILURE("FAILURE"),
	FINISHED("FINISHED");
	
	private String value = null;
	
	private TradeStatus(String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}
	
	public static String convert(String tradeStatus) {
		String cTradeStatus = null;
		switch(tradeStatus) {
			case "TRADE_SUCCESS" : cTradeStatus = TradeStatus.SUCCESS.value(); break;
			case "TRADE_FINISHED" : cTradeStatus = TradeStatus.FINISHED.value(); break;
		}
		return cTradeStatus;
	}

}
