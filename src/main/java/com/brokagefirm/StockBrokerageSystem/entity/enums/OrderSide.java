package com.brokagefirm.StockBrokerageSystem.entity.enums;

import java.util.Arrays;
import java.util.List;

public enum OrderSide {

    BUY(0),SELL(1);

    private int id;
    private OrderSide(int id){
        this.id=id;
    }

    public int getId() {
        return id;
    }
    public static OrderSide getType(Integer id){
        if(id==null){
            return null;
        }
        for(OrderSide orderSide:OrderSide.values()){
            if(id.equals(orderSide.getId())){
                return orderSide;
            }
        }
        throw new IllegalArgumentException("No matching type for id: "+id);
    }
    public static List<OrderSide> getOrderSide(){
        return Arrays.asList(new OrderSide[]{BUY,SELL});
    }
}
