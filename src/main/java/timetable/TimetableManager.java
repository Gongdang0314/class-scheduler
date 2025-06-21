package timetable;

import main.java.common.model.Subject;
import java.util.ArrayList;
import java.util.List;

public class TimetableManager {
    private final List<Subject> subjects = new ArrayList<>();

    public boolean addSubject(Subject subject) {
        if (hasConflict(subject)) return false;
        subjects.add(subject);
        return true;
    }

    private boolean hasConflict(Subject newSubj) {
        for (Subject subj : subjects) {
            if (subj.getDayOfWeek().equals(newSubj.getDayOfWeek()) &&
                timeOverlap(subj.getStartTime(), subj.getEndTime(),
                            newSubj.getStartTime(), newSubj.getEndTime())) {
                return true;
            }
        }
        return false;
    }

    private boolean timeOverlap(String s1, String e1, String s2, String e2) {
        return s1.compareTo(e2) < 0 && s2.compareTo(e1) < 0;
    }
}