package com.bocpay.model;

import java.util.LinkedList;

public class BillColumnConfig {
	private String column; // 列名
	private int row; // 行数 1 or 2
	private int start; // 起始位置
	private int end; // 结束位置
	private boolean sign; // 检查符号，判断金额是否为负值，以最后一位是否是“-”为依据

	public BillColumnConfig(String column, int row, int start, int end, boolean sign) {
		this.column = column;
		this.row = row;
		this.start = start;
		this.end = end;
		this.sign = sign;
	}

	public String toString() {
		return String.format("%s\t%d\t%d\t%d\t%b", column, row, start, end, sign);
	}

	public static LinkedList<BillColumnConfig> getList() {
		LinkedList<BillColumnConfig> list = new LinkedList<BillColumnConfig>();
		// 第1行
		list.add(new BillColumnConfig("终端号", 1, 2, 10, false));
		list.add(new BillColumnConfig("批号", 1, 12, 18, false));
		list.add(new BillColumnConfig("交易卡号", 1, 19, 35, false));
		list.add(new BillColumnConfig("交易日期", 1, 40, 50, false));
		list.add(new BillColumnConfig("交易时间", 1, 51, 57, false));
		list.add(new BillColumnConfig("交易金额", 1, 58, 73, true));
		list.add(new BillColumnConfig("手续费", 1, 74, 89, true));
		list.add(new BillColumnConfig("结算金额", 1, 90, 105, true));
		list.add(new BillColumnConfig("授权码", 1, 106, 112, false));
		list.add(new BillColumnConfig("交易码", 1, 114, 127, false));
		list.add(new BillColumnConfig("卡别", 1, 128, 132, false));
		// 第2行
		list.add(new BillColumnConfig("订单号", 2, 2, 24, false));
		list.add(new BillColumnConfig("协议号", 2, 25, 72, false));
		list.add(new BillColumnConfig("参考号", 2, 73, 85, false));
		list.add(new BillColumnConfig("分期计划", 2, 96, 105, false));
		list.add(new BillColumnConfig("分期期数", 2, 106, 110, false));

		return list;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public boolean isSign() {
		return sign;
	}

	public void setSign(boolean sign) {
		this.sign = sign;
	}

}
