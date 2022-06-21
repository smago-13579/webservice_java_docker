package ru.yandex.exception;

public class RequestErrorException extends RuntimeException {
    public RequestErrorException(String s) {
        super(s);
    }
}
