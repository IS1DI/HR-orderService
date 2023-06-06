package com.sovcombank.orderservice.service;

import com.sovcombank.orderservice.utils.MessageMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;


import java.util.Locale;

@Service
public class MessageService {
    private static final String ORDER_PREFIX = "order.";

    @Autowired
    MessageSource messages;

    public String orderMessage(MessageMethod method, Object[] args, Locale locale){
        return messages.getMessage((ORDER_PREFIX + method.getVal()).intern(), args,locale);
    }
    public String accessErrorMessage(Locale locale){
        return messages.getMessage(MessageMethod.ACCESS_ERROR.getVal(),null,locale);
    }

}
