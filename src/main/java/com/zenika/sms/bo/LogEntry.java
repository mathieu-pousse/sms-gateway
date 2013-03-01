package com.zenika.sms.bo;

import java.util.Date;

/**
 * TODO: document me.
 *
 * @author Mathieu POUSSE
 */
public class LogEntry {

    private Integer id;

    private Date at;

    private String from;

    private String to;

    private String message;

    /**
     * Default constructor.
     */
    public LogEntry() {
        // void
    }

    /**
     * Create a message with the specified parameters.
     *
     * @param from    the from number
     * @param to      the phone number
     * @param message the message (Should be UTF-8)
     */
    public LogEntry(final String from, final String to, final String message) {
        this.at = new Date();
        this.from = from;
        this.to = to;
        this.message = message;
    }


    /**
     * Set the address for the current object.
     *
     * @param to new value of address.
     */
    public void setTo(final String to) {
        this.to = to;
    }


    /**
     * Set the message for the current object.
     *
     * @param message new value of message.
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * Return the message for the current object.
     *
     * @return Value of message.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Return the address for the current object.
     *
     * @return Value of address.
     */
    public String getTo() {
        return this.to;
    }

    /**
     * Set the from for the current object.
     *
     * @param from new value of from.
     */
    public void setFrom(final String from) {
        this.from = from;
    }

    /**
     * Return the from for the current object.
     *
     * @return Value of from.
     */
    public String getFrom() {
        return this.from;
    }

    @Override
    public String toString() {
        return "SMSMessage{" + "from='" + this.from + '\'' + ", address='" + this.to + '\'' + ", message='" + this.message + '\'' + '}';
    }

    /**
     * Set the id for the current object.
     *
     * @param id new value of id.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Set the at for the current object.
     *
     * @param at new value of at.
     */
    public void setAt(Date at) {
        this.at = at;
    }

    /**
     * Return the id for the current object.
     *
     * @return Value of id.
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Return the at for the current object.
     *
     * @return Value of at.
     */
    public Date getAt() {
        return this.at;
    }
}