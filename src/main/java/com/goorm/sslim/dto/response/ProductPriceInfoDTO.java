package com.goorm.sslim.dto.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ProductPriceInfoDTO {

    private String goodInspectDay;   // 상품_조사_일
    private String entpId;           // 업체_아이디
    private String goodId;           // 상품_아이디
    private String goodPrice;        // 상품_가격
    private String plusoneYn;        // 원플러스원_여부
    private String goodDcYn;         // 상품_할인_여부
    private String goodDcStartDay;   // 상품_할인_시작일
    private String goodDcEndDay;     // 상품_할인_종료일
    private String inputDttm;        // 입력_일시
}
