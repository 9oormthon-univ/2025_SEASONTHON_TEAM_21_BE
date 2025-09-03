package com.goorm.sslim.housingCost.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Body {
	
	@XmlElement(name = "items")
    private Items items; 
    private int numOfRows;
    private int pageNo;
    private int totalCount;
    
}
