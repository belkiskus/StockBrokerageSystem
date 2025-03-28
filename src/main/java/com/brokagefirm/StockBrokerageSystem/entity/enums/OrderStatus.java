package com.brokagefirm.StockBrokerageSystem.entity.enums;

import java.util.Arrays;
import java.util.List;

public enum OrderStatus {

    PENDING(0),MATCHED(1),CANCELED(2);

    private int id;
    private OrderStatus(int id){
        this.id=id;
    }

    public int getId() {
        return id;
    }
    public static OrderStatus getType(Integer id){
        if(id==null){
            return null;
        }
        for(OrderStatus orderStatus:OrderStatus.values()){
            if(id.equals(orderStatus.getId())){
                return orderStatus;
            }
        }
        throw new IllegalArgumentException("No matching type for id: "+id);
    }
    public static List<OrderStatus> getOrderStatus(){
        return Arrays.asList(new OrderStatus[]{PENDING,MATCHED,CANCELED});
    }
}
