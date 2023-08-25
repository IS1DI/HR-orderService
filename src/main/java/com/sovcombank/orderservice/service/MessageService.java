package com.sovcombank.orderservice.service;

import com.sovcombank.orderservice.utils.MessageMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MessageService {

    @Autowired
    MessageSource messages;

    public String getMessage(MessageMethod method, Object[] args, Locale locale){
        return messages.getMessage(method.getVal(), args,locale);
    }

}
