package com.sinosoft.one.bpm.test.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name = "BPM_DEMO_COMBO")
public class Combo {
	private String comboCode;
	private int no;
	private Kind kind;

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	@Id
	public String getComboCode() {
		return comboCode;
	}

	public void setComboCode(String comboCode) {
		this.comboCode = comboCode;
	}

	@OneToOne(cascade={CascadeType.ALL})
	@JoinColumn(name="comboCode")
	public Kind getKind() {
		return kind;
	}

	public void setKind(Kind kind) {
		this.kind = kind;
	}

}
