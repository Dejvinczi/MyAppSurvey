package pl.surveyapplication.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SURVEYS")
public class Survey{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SURVEY_ID")
    private int surveyId;
    @Column(name = "SURVEY_NAME")
    private String surveyName;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "SURVEY_ORIGIN")
    private List<Question> questions = new ArrayList<>();


    public Survey(){}

    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
