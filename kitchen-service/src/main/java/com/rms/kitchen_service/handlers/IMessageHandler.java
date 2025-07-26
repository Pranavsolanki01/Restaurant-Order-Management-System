package com.rms.kitchen_service.handlers;

import com.rms.kitchen_service.dto.OrderMessageIncomming;

public interface IMessageHandler {

    public void messageProcess(OrderMessageIncomming orderMessage);

}
