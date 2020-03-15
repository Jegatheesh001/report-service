package com.medas.rewamp.reportservice.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.barcodelib.barcode.Linear;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BarcodePdf {
	
	@Value("${app.path.barcode}")
	private String barcodePath;
	
	public String generateBarCode(String barcodeData) throws Exception {
		Linear barcode = new Linear();
		barcode.setType(Linear.CODE128);

		// unit of measure for X, Y, LeftMargin, RightMargin, TopMargin, BottomMargin
		barcode.setUOM(Linear.UOM_PIXEL);

		// barcode module width in pixel
		barcode.setX(2f);

		// barcode module height in pixel
		barcode.setY(50f);

		barcode.setLeftMargin(0f);
		barcode.setRightMargin(0f);
		barcode.setTopMargin(0f);
		barcode.setBottomMargin(0f);

		// barcode image resolution in dpi
		barcode.setResolution(200);

		// display human readable text under the barcode
		barcode.setShowText(false);

		// human readable text font style
		// barcode.setTextFont(new Font("Arial", 0, 12));

		// ANGLE_0, ANGLE_90, ANGLE_180, ANGLE_270
		barcode.setRotate(Linear.ANGLE_0);

		// barcode data to encode
		barcode.setData(barcodeData);

		// render barcode to image
		String path = null;
		if (barcodePath != null) {
			// getting path for barcode image
			path = (String) barcodePath + "barcode_" + barcodeData + ".png";
			try {
				barcode.renderBarcode(path);
			} catch (Exception e) {
				log.error("Barcode Rendering Failed: {}", path);
			}
		} else {
			log.warn("Please add barcode path in properties.");
		}
		return path;
	}

}
