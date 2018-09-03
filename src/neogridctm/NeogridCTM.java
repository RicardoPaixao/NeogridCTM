package neogridctm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import neogridctm.controller.Planner;
import neogridctm.model.Event;
import neogridctm.model.Track;

/**
 *
 * @author Ricardo dos Santos Paixão
 */
public class NeogridCTM {

    private static final String DEFAULT_FILENAME = "input.txt";
    private ArrayList<Event> talks;
    private Planner planner;
    private final SimpleDateFormat format = new SimpleDateFormat("hh:mma");

    public void run(String filename) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        talks = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader("io/"+filename));
        String line;
        while ((line = br.readLine()) != null) {
            //line extracted. creating POJO
            talks.add(createTalk(line));
        }

        planner = new Planner();

        List<Track<Event>> schedule = planner.plan(talks);

        //creates output
        PrintWriter writer;

        writer = new PrintWriter("io/"+filename+"-output.txt", "UTF-8");

        for (int c = 0; c < schedule.size(); c++) { //iterates though the tracks
            Track<Event> track = schedule.get(c);
            writer.println("Track " + (c + 1) + ":");
            writer.println("");
            track.stream().forEach((event) -> { //iterates though the events
                writer.println(format.format(event.start) + " " + event.title + " " + event.durationText);
            });
            if (c + 1 < schedule.size()) {
                writer.println("");
            }

        }
        writer.close();
    }

    public Event createTalk(String line) throws IOException {
        String duration = line.substring(line.lastIndexOf(' ') + 1);
        Integer durationInteger;
        String title = line.substring(0, line.lastIndexOf(' '));
        try {
            durationInteger = Integer.parseInt(duration.replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            //duration is not in the XYZmin format, maybe lightning, maybe error
            if (duration.equals("lightning")) {
                durationInteger = 5;
            } else {
                throw new IOException("Malformed input file");
            }
        }
        return new Event(title, duration, durationInteger);
    }

    public static void main(String[] args) throws IOException {
        new NeogridCTM().run((args.length == 0) ? DEFAULT_FILENAME : args[0]);
    }
}