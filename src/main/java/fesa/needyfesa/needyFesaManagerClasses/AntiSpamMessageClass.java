package fesa.needyfesa.needyFesaManagerClasses;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class AntiSpamMessageClass {

    private final ZoneOffset zoneOffset = ZoneOffset.UTC;

    private final ArrayList<Long> sendTimes = new ArrayList<>();
    private int count;

    public AntiSpamMessageClass create(LocalDateTime sendTime) {
        this.sendTimes.add(sendTime.toEpochSecond(zoneOffset));
        this.count = 0;

        return  this;
    }

    public void update(LocalDateTime sendTime) {
        long sendTimeEpochSecond = sendTime.toEpochSecond(zoneOffset);

        this.sendTimes.add(sendTimeEpochSecond);
        this.count++;

        // Remove messages more than 5 minutes ago
        for (long epochSecond : sendTimes) {
            if (sendTimeEpochSecond - epochSecond > 5 * 60) {
                sendTimes.remove(epochSecond);
            }
        }
    }

    public boolean safe() {
        return this.sendTimes.size() < 2;
    }

    public boolean checkToDelete() {
        long now = LocalDateTime.now().toEpochSecond(zoneOffset);
        // Remove messages more than 5 minutes ago
        for (long epochSecond : sendTimes) {
            if (now - epochSecond > 5 * 60) {
                sendTimes.remove(epochSecond);
            }
        }
        return sendTimes.size() == 0;
    }
}
