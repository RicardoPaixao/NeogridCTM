package neogridctm.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import neogridctm.model.Event;
import neogridctm.model.Track;

/**
 *
 * @author Ricardo Paixão
 */
public class Planner {

    private final SimpleDateFormat df = new SimpleDateFormat("hh:mma");
    private static final String START = "09:00AM";
    private static final String NOON = "12:00PM";
    private static final String LUNCH_END = "01:00PM";
    private static final String NETWORKING_MIN = "04:00PM";
    private static final String NETWORKING_MAX = "05:00PM";

    /**
     *
     * @param talkList a list of all talks
     * @return the list of planned tracks, which are themselves a list of talks
     */
    public List<Track<Event>> plan(List<Event> talkList) {
        List<Track<Event>> plannedList = new ArrayList<>();
        try {

            //helpers
            Calendar noon = Calendar.getInstance();
            noon.setTime(df.parse(NOON));
            Calendar lunchEnd = Calendar.getInstance();
            lunchEnd.setTime(df.parse(LUNCH_END));
            Calendar networkingMin = Calendar.getInstance();
            networkingMin.setTime(df.parse(NETWORKING_MIN));
            Calendar networkingMax = Calendar.getInstance();
            networkingMax.setTime(df.parse(NETWORKING_MAX));

            //first of all we will sort the event list by its duration, from longest to shortest
            Collections.sort(talkList, (Event s1, Event s2) -> s2.duration.compareTo(s1.duration));

            for (; talkList.size() > 0;) {
                Track<Event> track = new Track();

                //day start
                Calendar now = Calendar.getInstance();
                now.setTime(df.parse(START));
                Boolean am = true; //ante meridiem

                //start by planing the longest events
                Event networking = null;
                for (int c1 = 0; c1 < talkList.size();) {

                    //decision matrix BEGIN
                    Event e = talkList.get(c1);
                    Calendar then = (Calendar) now.clone();
                    then.add(Calendar.MINUTE, e.duration);
                    if (am && !then.after(noon)) {
                        //event accepted. include in schedule and remove from original
                        e.start = now.getTime();
                        track.add(e);
                        talkList.remove(e);
                        c1 = 0;
                        now = then;
                    } else if (am && then.after(noon)) {
                        //event would end after lunch begins. try to fit smaller candidate event
                        if (c1 + 1 < talkList.size() && !now.equals(noon)) {
                            c1++;
                            continue;
                        } else {
                            //no more candidate events. skip to lunch
                            Event lunch = new Event("Lunch", "60min", 60, noon.getTime());
                            track.add(lunch);
                            now = lunchEnd;
                            am = false;
                            c1 = 0;
                        }
                    } else if (!am && !then.after(networkingMax)) {
                        //event accepted. include in schedule and remove from original
                        e.start = now.getTime();
                        track.add(e);
                        talkList.remove(e);
                        c1 = 0;
                        now = then;
                    } else //!am && then.after(networkingMax)
                    //event would end after networking begins. try to fit smaller candidate event
                    if (c1 + 1 < talkList.size() && !now.equals(networkingMax)) {
                        c1++;
                        continue;
                    } else {
                        //no more candidate events. skip to networking event and end track
                        break;
                    }

                }
                
                //finalizing track
                Calendar finalEventStart = Calendar.getInstance();
                finalEventStart.setTime(track.get(track.size() - 1).start);
                finalEventStart.add(Calendar.MINUTE, track.get(track.size() - 1).duration);
                
                if(!finalEventStart.after(noon)){
                    //the track might have ended before lunch
                     Event lunch = new Event("Lunch", "60min", 60, noon.getTime());
                     track.add(lunch);
                     finalEventStart=noon;
                }

                //add networking event
                if (!finalEventStart.after(networkingMin)) {
                    finalEventStart = networkingMin;//must begin after 4pm
                }
                networking = new Event("Networking Event", "60min", 60, finalEventStart.getTime());
                track.add(networking);

                plannedList.add(track);
            }
            //decision matrix END

        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return plannedList;
    }
}
