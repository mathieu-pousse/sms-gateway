package com.zenika.sms.service;

/**
 * TODO: document me.
 *
 * @author Mathieu POUSSE
 */
public interface HttpServerService {

    void startHttpServer();

    void stopHttpServer();

    boolean isServerRunning();
}
