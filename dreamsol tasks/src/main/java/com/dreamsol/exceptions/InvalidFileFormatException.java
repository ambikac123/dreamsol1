package com.dreamsol.exceptions;

public class InvalidFileFormatException extends RuntimeException
{
    String message;
    public InvalidFileFormatException(String message)
    {
        super(message);
        this.message = message;
    }
}
