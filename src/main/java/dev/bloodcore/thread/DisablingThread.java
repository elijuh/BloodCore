package dev.bloodcore.thread;

import lombok.Getter;

@Getter
public class DisablingThread extends Thread {
    private boolean enabled = true;

    public void disable() {
        enabled = false;
    }
}
