package pl.surveyapplication.controller.user;

import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.surveyapplication.model.*;
import pl.surveyapplication.service.ConnectionService;
import pl.surveyapplication.service.SurveyMagazinService;
import pl.surveyapplication.service.UserService;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

/**
 * @author Dawid
 * @version 1.0
 * Klasa kontrollera w której można zarządzać przypisanymi ankietami danego użytkownika.
 */
@Controller
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/user/surveys")
public class CompleteSurveyController {

    private final Logger logger = LoggerFactory.getLogger(CompleteSurveyController.class);
    @Autowired
    ConnectionService connectionService;
    /**
     * Zmienna odwolujaca się do serwisów zakończonych ankiet
     */
    @Autowired
    SurveyMagazinService surveyMagazinService;

    @Autowired
    UserService userService;

    @RequestMapping
    public String showMySurveys(Model model, Authentication authentication) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        List<Connection> mySurveys = connectionService.getConnectionsByUserId(userService.getUserById(myUserDetails.getId()));

        model.addAttribute("mySurveys", mySurveys);
        return "completing/my-surveys";
    }

    @RequestMapping(path = {"/try", "/try/{id}"})
    public String tryCompletingSurvey(Model model, @PathVariable("id") Long id) {
        Connection connection = connectionService.getConnection(id);
        FilledSurvey filledSurvey = connection.getSurvey().getTemplate();

        for(FilledQuestion fq : filledSurvey.getFilledQuestions()){
            System.out.println(fq.getQuestion());
            for(FilledAnswer fa : fq.getFilledAnswers()){
                System.out.print(fa.getAnswer());
                if (fa.isCheck() == true)
                    System.out.println("*");
                else
                    System.out.println(" ");
            }
        }

        model.addAttribute("filledSurvey", filledSurvey);
        return "completing/completing";
    }

    // GET /user/surveys/try/{id}
    @GetMapping("/try/{id}")
    public ResponseEntity<?> getDataForAngular(@PathVariable Long id) {
        Connection connection = connectionService.getConnection(id);
        FilledSurvey filledSurvey = connection.getSurvey().getTemplate();

        for(FilledQuestion fq : filledSurvey.getFilledQuestions()){
//            System.out.println(fq.getQuestion());
            for(FilledAnswer fa : fq.getFilledAnswers()){
//                System.out.print(fa.getAnswer());
                if (fa.isCheck() == true)
                    System.out.println("*");
                else
                    System.out.println(" ");
            }
        }
        return ResponseEntity.ok(filledSurvey);
    }

    @PostMapping("/finish-from-angular")
    public ResponseEntity<String> receiveDataFromAngular(@RequestBody FilledSurvey filledSurvey) {
        StringBuilder sb = new StringBuilder();
        for (FilledQuestion question : filledSurvey.getFilledQuestions()) {
            for (FilledAnswer answer : question.getFilledAnswers()) {
                if (answer.isCheck()) sb.append(answer.getAnswer());
            }
        }
        LocalDateTime date = LocalDateTime.now();
        sb.append(date);

        String hash = sb.toString();
        hash = Base64.getEncoder().encodeToString(sb.toString().getBytes());
        filledSurvey.setHash(hash);

        System.out.println(hash);

        return new ResponseEntity<>(hash, HttpStatus.CREATED);
    }

    /**
     * Metoda pozwala na wyszukanie nam ankiety o wprowadzonym tokenie ze strony.
     *
     * @param filledSurvey Model do tworzenia obiektów w html
     * @return String html, strone mozliwością wprowadzenia tokena.
     */
    @RequestMapping(path = "/finish{token}", method = RequestMethod.POST)
    public String getHash(@ModelAttribute("filledSurvey") FilledSurvey filledSurvey, Model model, @PathVariable String token) {

        StringBuilder sb = new StringBuilder();
        for (FilledQuestion question : filledSurvey.getFilledQuestions()) {
            for (FilledAnswer answer : question.getFilledAnswers()) {
                if (answer.isCheck()) sb.append(answer.getAnswer());
            }
        }
        LocalDateTime date = LocalDateTime.now();
        sb.append(date);

        String hash = sb.toString();
        hash = Base64.getEncoder().encodeToString(sb.toString().getBytes());
        filledSurvey.setHash(hash);

        model.addAttribute("hash", hash);
//      surveyMagazinService.addSurveyToMagazin(filledSurvey);

        return "completing/finish";
    }


    /**
     * Metoda zwraca ankiete o danym tokenie.
     *
     * @param model Model do tworzenia obiektów w html
     * @return String html, strone do podglądu uzupełnionej ankiety.
     */
    @RequestMapping(path = "/showMySurvey")
    public String showMySurvey(String hash, Model model) {
        model.addAttribute("hash", "");
        return "completing/show-my-survey";
    }

    @RequestMapping(path = "/survey")
    public String showSurvey(String hash, Model model) {
        FilledSurvey filledSurvey = surveyMagazinService.getSurveyByHash(hash);
        model.addAttribute("survey", filledSurvey);

        return "completing/show-my-survey";
    }
}



