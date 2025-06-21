package timetable;

import model.Subject;

import java.util.ArrayList;
import java.util.List;

public class TimetableManager {
    private final List<Subject> subjects = new ArrayList<>();

    public boolean addSubject(Subject subject) {
        if (hasConflict(subject)) {
            return false; // 시간 겹침
        }
        subjects.add(subject);
        return true;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    private boolean hasConflict(Subject newSubj) {
        for (Subject subj : subjects) {
            if (subj.getDay().equals(newSubj.getDay()) &&
                subj.getStartHour() < newSubj.getEndHour() &&
                newSubj.getStartHour() < subj.getEndHour()) {
                return true;
            }
        }
        return false;
    }
}
