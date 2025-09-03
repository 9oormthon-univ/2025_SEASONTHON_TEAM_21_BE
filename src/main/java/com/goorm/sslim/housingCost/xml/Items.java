package com.goorm.sslim.housingCost.xml;

import java.util.List;

import com.goorm.sslim.housingCost.dto.OfficetelRentDto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Items {
	
    @XmlElement(name = "item")
    private List<OfficetelRentDto> item;
    
}
