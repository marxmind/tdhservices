package com.italia.controller;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "receipt")
public class Receipt {
    private String merchant;
    private String date;
    private String total;

    @XmlElement
    public String getMerchant() { return merchant; }
    public void setMerchant(String merchant) { this.merchant = merchant; }

    @XmlElement
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    @XmlElement
    public String getTotal() { return total; }
    public void setTotal(String totalAmount) { this.total = total; }
}