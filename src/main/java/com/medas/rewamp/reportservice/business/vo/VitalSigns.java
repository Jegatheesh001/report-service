package com.medas.rewamp.reportservice.business.vo;

import lombok.Data;

/**
 * @author jegatheesh.mageswaran<br>
		   <b>Created</b> On Mar 23, 2020
 *
 */
@Data
public class VitalSigns {
	private Integer consult_id;
	private String op_number;
	private Integer department_id;
	private String bloodpressure;
	private String bloodPressureSyst;
	private String bloodpressure_dia;
	private String duration_of_ill;
	private String pulse;
	private String temperature;
	private String vital_sign;
	private String vital_value;
	private String type;
	private String remarks;
}
