package neogridctm.model;

//Represents one conference event, that can be either a Talk, Lunch, or Networking event
import java.util.Date;

public class Event {

    //private atributes, getters and setters are not necessary for such simple POJO structure
    public String title;
    public String durationText;
    public Integer duration;
    public Date start;

    public Event(String title, String durationText, Integer duration) {
        this.title = title;
        this.durationText = durationText;
        this.duration = duration;
    }

    public Event(String title, String durationText, Integer duration, Date start) {
        this.title = title;
        this.durationText = durationText;
        this.duration = duration;
        this.start = start;
    }
    
}
