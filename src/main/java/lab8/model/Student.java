package lab8.model;

public class Student {
    private final String studentID;
    private final double score1;
    private final double score2;
    private final double score3;

    public Student(String studentID, double score1,
                   double score2, double score3) {
        this.studentID = studentID;
        this.score1 = score1;
        this.score2 = score2;
        this.score3 = score3;
    }

    public String getStudentID() {
        return studentID;
    }

    public double getScore1() {
        return score1;
    }

    public double getScore2() {
        return score2;
    }

    public double getScore3() {
        return score3;
    }

    public double getAverageScore() {
        return (score1 + score2 + score3) / 3.0;
    }

    @Override
    public String toString() {
        return "StudentID: " + studentID
                + ", Score1: " + score1
                + ", Score2: " + score2
                + ", Score3: " + score3
                + ", Average: "
                + String.format("%.2f", getAverageScore());
    }
}