package com.sovcombank.orderservice.entity;


public enum Status {
    CREATED,
    PROCESSING,
    ALL_VACANCIES_CLOSED,
    CLOSED;
    public static Status getNext(Status status){ //TODO closed
        try{return Status.values()[status.ordinal()+1];}
        catch (Exception e){
            return status;
        }
    }
}
