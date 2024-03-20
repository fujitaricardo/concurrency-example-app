package com.nexti.concurrencyexample.viewmodel;

import static com.nexti.concurrencyexample.viewmodel.ModeEnum.*;

import androidx.lifecycle.ViewModel;

import com.nexti.concurrencyexample.view.ResponseListener;
import com.nexti.concurrencyexample.model.KotlinModel;
import com.nexti.concurrencyexample.model.ResponseDto;
import com.nexti.concurrencyexample.model.api.HttpClient;
import com.nexti.concurrencyexample.model.api.HttpInterface;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class MainViewModel extends ViewModel {

    private ResponseListener resultListener;
    private ModeEnum mode = JAVA_THREADS;
    private Long delay = 5L;
    private Long timeout = 30L;

    public MainViewModel(ResponseListener resultListener) {
        this.resultListener = resultListener;
    }

    public void makeAsyncRequest() {
        switch (mode) {
            case JAVA_THREADS:
                makeRequestUsingJavaThread();
                break;
            case RX_JAVA:
                makeRequestUsingRxJava();
                break;
            case KOTLIN_COROUTINE:
                makeRequestUsingCoroutine();
                break;
            default:
                break;
        }
    }

    private void makeRequestUsingJavaThread() {
        Thread thread = new Thread(() -> {
            try {
                Long t0 = System.currentTimeMillis();
                HttpInterface httpInterface = HttpClient.getClient(timeout).create(HttpInterface.class);
                Response<ResponseBody> response = httpInterface.getDelayedResponse(delay).execute();
                Long t1 = System.currentTimeMillis();

                if (response.body() != null && response.code() == 200) {
                    resultListener.processResult(new ResponseDto(response.body().string(), t1 - t0));
                } else {
                    resultListener.processResult(new ResponseDto("Error while making request. Code: " + response.code(), t1 - t0));
                }
            } catch (Exception e) {
                resultListener.processResult(new ResponseDto("Error: " + e.getMessage(), -1L));
            }
        });
        thread.start();
    }

    private void makeRequestUsingRxJava() {
        Disposable disposable = Observable.fromCallable(() -> {
            try {
                Long t0 = System.currentTimeMillis();
                HttpInterface httpInterface = HttpClient.getClient(timeout).create(HttpInterface.class);
                Response<ResponseBody> response = httpInterface.getDelayedResponse(delay).execute();
                Long t1 = System.currentTimeMillis();

                if (response.body() != null && response.code() == 200) {
                    return new ResponseDto(response.body().string(), t1 - t0);
                } else {
                    return new ResponseDto("Error while making request. Code: " + response.code(), t1 - t0);
                }
            } catch (Exception e) {
                return new ResponseDto("Error: " + e.getMessage(), -1L);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(result -> resultListener.processResult(result));
    }

    private void makeRequestUsingCoroutine() {
        HttpInterface httpInterface = HttpClient.getClient(timeout).create(HttpInterface.class);
        KotlinModel.INSTANCE.makeAsyncRequest(delay, resultListener, httpInterface);
    }

    public void setMode(ModeEnum mode) {
        this.mode = mode;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }
}
