package com.yurets_y.document_parser.bin;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 21.05.2017.
 */
@XmlRootElement(name = "column_order")
public class RDocEnumOrderWrapper {
    private RDocEnum[] columnOrder;

    public RDocEnum[] getOrder() {
        return this.columnOrder;
    }

    public void setOrder(RDocEnum[] order) {
        this.columnOrder = order;
    }





}
