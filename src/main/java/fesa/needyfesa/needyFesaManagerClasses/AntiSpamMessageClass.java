package fesa.needyfesa.needyFesaManagerClasses;


import fesa.needyfesa.NeedyFesa;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class AntiSpamMessageClass {

    private static final int spamTimeMinute = NeedyFesa.configManager.needyFesaConfig.get("spam-time").getAsInt();
    private static final int spamCount = NeedyFesa.configManager.needyFesaConfig.get("spam-count").getAsInt();

    private final ZoneOffset zoneOffset = ZoneOffset.UTC;
    private final ArrayList<Long> sendTimes = new ArrayList<>();

    public AntiSpamMessageClass create(LocalDateTime sendTime) {
        this.sendTimes.add(sendTime.toEpochSecond(zoneOffset));
        return this;
    }
    public void update(LocalDateTime sendTime) {this.sendTimes.add(sendTime.toEpochSecond(zoneOffset));}
    public boolean safe() {return sendTimes.size() - countPassed() < spamCount;}
    public boolean checkToDelete() {return sendTimes.size() == countPassed();}
    private int countPassed() {
        long now = LocalDateTime.now().toEpochSecond(zoneOffset);
        int count = 0;
        for (long epochSecond : sendTimes) {
            if (now - epochSecond > spamTimeMinute * 60L) {
                count++;
            }
        }
        return count;
    }
}
