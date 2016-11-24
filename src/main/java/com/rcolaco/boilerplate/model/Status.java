package com.rcolaco.boilerplate.model;

import java.io.Serializable;

/**
 *
 */
public class Status implements Serializable, Cloneable
{
    private String errorCode = "ERR-123", message = "Something unexpected happened";
    private boolean complete = false;

    public Status()
    {
    }

    public Status(Status source)
    {
        errorCode = source.errorCode;
        message = source.message;
        complete = source.complete;
    }

    @Override
    public Object clone()
    {
        return new Status(this);
    }

    public String getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(String errorCode)
    {
        this.errorCode = errorCode;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public boolean isComplete()
    {
        return complete;
    }

    public void setComplete(boolean complete)
    {
        this.complete = complete;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Status status = (Status) o;

        if (complete != status.complete) return false;
        if (errorCode != null ? !errorCode.equals(status.errorCode) : status.errorCode != null) return false;
        return message != null ? message.equals(status.message) : status.message == null;

    }

    @Override
    public int hashCode()
    {
        int result = errorCode != null ? errorCode.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (complete ? 1 : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "Status{" +
            "errorCode='" + errorCode + '\'' +
            ", message='" + message + '\'' +
            ", complete=" + complete +
            '}';
    }
}
