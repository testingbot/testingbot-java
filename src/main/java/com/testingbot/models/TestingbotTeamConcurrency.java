package com.testingbot.models;

import java.io.Serializable;

/**
 * A pair of concurrency limits — VM sessions and physical-device sessions.
 *
 * @since 1.1.0
 */
public class TestingbotTeamConcurrency implements Serializable {
    private static final long serialVersionUID = 1L;
    private int vms;
    private int physical;

    public int getVms() {
        return vms;
    }

    public void setVms(int vms) {
        this.vms = vms;
    }

    public int getPhysical() {
        return physical;
    }

    public void setPhysical(int physical) {
        this.physical = physical;
    }
}
