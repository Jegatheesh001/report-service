package com.medas.rewamp.reportservice.business.vo;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * @author jegatheesh.mageswaran<br>
		   <b>Created</b> On Mar 22, 2020
 *
 */
@Data
public class ServiceVO {
	private String diagnosis_category;
	private String icd_code;
	private String icd_desc;
	
	private String ins_app;
	private String insurar_icd_code;
	private String Procedure_name;
	private String Labtest_name;
	private Integer Quantity;
	private String Procedure_type;
	private String approval_status;
	private Double appr_amt;
	private Double gross_amt;
	private String appr_no;
	private Date approved_date;
	private String appr_remarks;
	private String Valid_upto;
	private String Lab_type;
	
	public void setGross_amt(BigDecimal grossAmt) {
		if (grossAmt != null)
			this.gross_amt = grossAmt.doubleValue();
	}
}
