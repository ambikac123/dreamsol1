package com.dreamsol.exceptions;

public class InvalidInputFormat extends RuntimeException {

    String resourceName;
    public InvalidInputFormat(String resourceName)
    {
        super(String.format("%s is invalid!",resourceName));
        this.resourceName = resourceName;
    }
}
