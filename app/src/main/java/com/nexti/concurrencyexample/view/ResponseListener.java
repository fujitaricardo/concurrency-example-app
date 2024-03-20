package com.nexti.concurrencyexample.view;

import com.nexti.concurrencyexample.model.ResponseDto;

public interface ResponseListener {
    void processResult(ResponseDto responseDto);
}
