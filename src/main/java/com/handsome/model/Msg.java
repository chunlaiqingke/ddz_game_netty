package com.handsome.model;

import java.io.Serializable;

public class Msg implements Serializable {

	private String cmd;

	private String data;

	private Integer callindex;

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Integer getCallindex() {
		return callindex;
	}

	public void setCallindex(Integer callindex) {
		this.callindex = callindex;
	}
}
