/*
 * Njiwa Open Source Embedded M2M UICC Remote Subscription Manager
 * 
 * 
 * Copyright (C) 2019 - , Digital Solutions Ltd. - http://www.dsmagic.com
 *
 * Njiwa Dev <dev@njiwa.io>
 * 
 * This program is free software, distributed under the terms of
 * the GNU General Public License.
 */ 

package io.njiwa.common;

import io.njiwa.common.model.RpaEntity;
import io.njiwa.sr.transports.Transport;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Stats collector
 */

public class StatsCollector {

    private static final int MAX_EVENTS_HOURS = 10;
    private static final int MAX_EVENTS_SECONDS = 3600 * MAX_EVENTS_HOURS;

    private static int maxEventSeconds = MAX_EVENTS_SECONDS;
    private static OurRPACounters smdp = new OurRPACounters();
    private static OurRPACounters smsr = new OurRPACounters();

    private static EventCtr smdpTransactions = new EventCtr();
    private static EventCtr smsrTransactions = new EventCtr();
    private static Map<String, OtherRPACounters> entityCounters = new CaseInsensitiveMap<>();
    private static ExecutorService executor = Executors.newFixedThreadPool(5);
    private static Map<Transport.TransportType, TransportEvents> transportEventsMap = new ConcurrentHashMap<Transport
            .TransportType, TransportEvents>() {{
        put(Transport.TransportType.SMS, new TransportEvents());
        put(Transport.TransportType.BIP, new TransportEvents());
        put(Transport.TransportType.RAMHTTP, new TransportEvents());
    }};

    public static void recordTransaction(RpaEntity.Type type) {
        if (type == RpaEntity.Type.SMSR)
            smsrTransactions.tick();
        else if (type == RpaEntity.Type.SMDP)
            smdpTransactions.tick();
    }

    public static Map<RpaEntity.Type, double[]> getTransactionStats() {
        int[] intervals = ServerSettings.getStatsIntervals();
        return new HashMap<RpaEntity.Type, double[]>() {
            {
                put(RpaEntity.Type.SMDP, smdpTransactions.getFrequencies(intervals));
                put(RpaEntity.Type.SMSR, smsrTransactions.getFrequencies(intervals));
            }
        };
    }

    static {
        maxEventSeconds = ServerSettings.getMaxEventsHours() * 3600;
    }

    public static void recordOwnEvent(RpaEntity.Type ourType, EventType eventType) {
        // Do so asynchronously
        executor.execute(() -> {
            try {
                if (ourType == RpaEntity.Type.SMDP)
                    smdp.recordEvent(eventType);
                else if (ourType == RpaEntity.Type.SMSR)
                    smsr.recordEvent(eventType);
            } catch (Exception ex) {}
        });

    }

    public static OSData getOperatingSystemData() {
        return new OSData();
    }

    public static void recordOtherEntityEvent(String entityOid, EventType eventType) {
        executor.execute(() -> {
            OtherRPACounters other;
            if (entityOid == null)
                return;
            try {
                synchronized (entityCounters) {
                    if ((other = entityCounters.get(entityOid)) == null) {
                        other = new OtherRPACounters();
                        entityCounters.put(entityOid, other);
                    }
                }
                other.recordEvent(eventType);
            } catch (Exception ex) {}
        });
    }

    public static Map<EventType, double[]> getOurEventFrequencies(RpaEntity.Type ourType) {
        int[] intervals = ServerSettings.getStatsIntervals();
        if (ourType == RpaEntity.Type.SMDP)
            return smdp.getStats(intervals);
        else
            return smsr.getStats(intervals);
    }

    public static Map<String, Map<EventType, double[]>> getOtherEntityFrequencyCounters() {
        int[] intervals = ServerSettings.getStatsIntervals();
        Map<String, Map<EventType, double[]>> map = new HashMap<>();
        for (Map.Entry<String, OtherRPACounters> e : entityCounters.entrySet()) {
            OtherRPACounters c = e.getValue();
            Map<EventType, double[]> evt = c.getStats(intervals);
            map.put(e.getKey(), evt);
        }
        return map;
    }

    public static void recordTransportEvent(Transport.TransportType transportType, Transport.PacketType packetType) {
        TransportEvents evt = transportEventsMap.get(transportType);
        evt.recordPacket(packetType);
    }

    public static Map<Transport.TransportType, Map<Transport.PacketType, double[]>> getTransportStats() {
        int[] intervals = ServerSettings.getStatsIntervals();
        return new HashMap<Transport.TransportType, Map<Transport.PacketType, double[]>>() {
            {
                put(Transport.TransportType.SMS, transportEventsMap.get(Transport.TransportType.SMS).getStats(intervals));
                put(Transport.TransportType.BIP, transportEventsMap.get(Transport.TransportType.BIP).getStats(intervals));
                put(Transport.TransportType.RAMHTTP, transportEventsMap.get(Transport.TransportType.RAMHTTP).getStats(intervals));
            }
        };

    }

    public enum EventType {
        RecvdPacket, SentPacket, AuthError, IPError;
      //  public EventType[] valuesList = values();
    }

    private static class TransportEvents {
        EventCtr moPdus = new EventCtr();
        EventCtr mtPdus = new EventCtr();

        public void recordPacket(Transport.PacketType type) {
            if (type == Transport.PacketType.MO)
                moPdus.tick();
            else
                mtPdus.tick();
        }

        public Map<Transport.PacketType, double[]> getStats(int[] intervals) {
            return new HashMap<Transport.PacketType, double[]>() {
                {
                    put(Transport.PacketType.MO, moPdus.getFrequencies(intervals));
                    put(Transport.PacketType.MT, mtPdus.getFrequencies(intervals));
                }
            };
        }

    }

    public static class OSData {
        public long freeMem;

        public long physMem;
        public long processors;
        public String version;

        public OSData() {
            version = System.getProperty("os.name");

            processors = Runtime.getRuntime().availableProcessors();

            freeMem = Runtime.getRuntime().freeMemory();
            physMem = Runtime.getRuntime().totalMemory();

        }
    }


    // Now counters for other entities, indexed by oid.

    private static class EventCtr {
        private EventSlot[] eventSlots; //!< The actual slots
        private long evtCount = 0;

        public EventCtr() {
            this.eventSlots = new EventSlot[maxEventSeconds];
            for (int i = 0; i < eventSlots.length; i++)
                eventSlots[i] = new EventSlot();
        }

        public void tick() {
            // Add a new event, or count it.
            long secs = Calendar.getInstance().getTimeInMillis() / 1000; // Convert to seconds
            int pos = (int) (secs % eventSlots.length);
            evtCount += eventSlots[pos].incrCount(secs);
        }

        public synchronized long eventCount() {
            return evtCount;
        }

        public synchronized double[] getFrequencies(int intervalSecs[]) {
            // Compute frequencies for events based on intervals (in secs) given
            double[] frequencies = new double[intervalSecs.length];
            long secs = Calendar.getInstance().getTimeInMillis() / 1000; // Convert to seconds
            // sum events, then divide by interval size
            for (int i = 0; i < eventSlots.length; i++)
                eventSlots[i].countIntervalEvents(intervalSecs, secs, frequencies);

            // Do averages
            for (int i = 0; i < intervalSecs.length; i++)
                if (intervalSecs[i] > 0)
                    frequencies[i] /= intervalSecs[i];
            return frequencies;
        }

        private class EventSlot {
            long seconds = 0;
            long count = 0;

            public synchronized long incrCount(long curSecs) {
                long ct;
                if (seconds > 0 && curSecs - seconds > maxEventSeconds) {
                    ct = -count + 1;
                    // We wrapped, so go back to zero.
                    count = 0;
                    seconds = curSecs;
                } else {
                    ct = 1;
                    if (seconds == 0)
                        seconds = curSecs;
                }
                count++;
                return ct;
            }

            public synchronized void countIntervalEvents(int intervals[], long curSecs, double[] counts) {
                long diff = curSecs - seconds;
                if (seconds == 0 || diff > maxEventSeconds)
                    return; // Nothing to do, nothing written in this slot, or the tick was outside period of
                // interest. Right?

                // Go over all intervals
                for (int i = 0; i < intervals.length; i++)
                    if (diff < intervals[i])
                        counts[i] += count;
            }

        }
    }

    // Now the counters
    private static class OurRPACounters {
        private EventCtr recvdPkts = new EventCtr();
        private EventCtr ipErrors = new EventCtr();
        private EventCtr authErrors = new EventCtr();

        public void recordEvent(EventType type) {
            EventCtr ctr;
            switch (type) {
                case RecvdPacket:
                    ctr = recvdPkts;
                    break;
                case IPError:
                    ctr = ipErrors;
                    break;
                case AuthError:
                    ctr = authErrors;
                    break;
                default:
                    return;
            }
            try {
                ctr.tick();
            } catch (Exception ex) {
                Utils.lg.error("Error recording event: " + ex.getMessage());
            }
        }

        public Map<EventType, double[]> getStats(int[] intervals) {
            return new ConcurrentHashMap<EventType, double[]>() {
                {
                    put(EventType.AuthError, authErrors.getFrequencies(intervals));
                    put(EventType.RecvdPacket, recvdPkts.getFrequencies(intervals));
                    put(EventType.IPError, ipErrors.getFrequencies(intervals));
                }
            };
        }
    }

    private static class OtherRPACounters {
        private EventCtr recvdPkts = new EventCtr();
        private EventCtr sendPkts = new EventCtr();
        private EventCtr ipErrors = new EventCtr();
        private EventCtr authErrors = new EventCtr();

        public Map<EventType, double[]> getStats(int[] intervals) {
            return new ConcurrentHashMap<EventType, double[]>() {
                {
                    put(EventType.SentPacket, sendPkts.getFrequencies(intervals));
                    put(EventType.AuthError, authErrors.getFrequencies(intervals));
                    put(EventType.RecvdPacket, recvdPkts.getFrequencies(intervals));
                    put(EventType.IPError, ipErrors.getFrequencies(intervals));
                }
            };
        }

        public void recordEvent(EventType type) {
            EventCtr ctr;
            switch (type) {
                case RecvdPacket:
                    ctr = recvdPkts;
                    break;
                case IPError:
                    ctr = ipErrors;
                    break;
                case AuthError:
                    ctr = authErrors;
                    break;
                case SentPacket:
                    ctr = sendPkts;
                    break;
                default:
                    return;
            }
            try {
                ctr.tick();
            } catch (Exception ex) {
                Utils.lg.error("Error recording event: " + ex.getMessage());
            }
        }
    }
}
