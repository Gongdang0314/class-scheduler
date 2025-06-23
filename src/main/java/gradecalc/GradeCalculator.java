package gradecalc;

import java.util.List;

import common.model.Grade;

public class GradeCalculator {

    public double calculateGPA(List<Grade> grades) {
        double totalPoints = 0;
        int totalCredits = 0;

        for (Grade g : grades) {
            totalPoints += g.getGpa() * g.getCredit();
            totalCredits += g.getCredit();
        }

        return totalCredits == 0 ? 0.0 : totalPoints / totalCredits;
    }

    public int calculateTotalCredits(List<Grade> grades) {
        int total = 0;
        for (Grade g : grades) {
            total += g.getCredit();
        }
        return total;
    }

    public int calculateMajorCredits(List<Grade> grades) {
        int major = 0;
        for (Grade g : grades) {
            if (g.isMajor()) major += g.getCredit();
        }
        return major;
    }

    public boolean meetsGraduationRequirement(List<Grade> grades) {
        return calculateTotalCredits(grades) >= 130 &&
               calculateMajorCredits(grades) >= 60 &&
               calculateGPA(grades) >= 2.5;
    }
}
