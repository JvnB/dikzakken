package com.jim.spring.controller;

import com.jim.spring.domain.Deelnemer;
import com.jim.spring.domain.Meeting;
import com.jim.spring.repository.DeelnemerRepository;
import com.jim.spring.service.DeelnemerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Created by jim on 17-11-17.
 */
@Controller
public class ChartController {

    private DeelnemerService deelnemerService;

    public ChartController(DeelnemerService deelnemerService) {
        this.deelnemerService = deelnemerService;
    }

    @ResponseBody
    @GetMapping(value = "/chart", produces = "application/json")
    public Object[][] getAllData() {

        List<Deelnemer> deelnemerList = deelnemerService.getAllDeelnemers();


        Object[][] array = new Object[5][deelnemerList.size() + 1];
        array[0][0] = "YEAR";
        for (int i = 0; i < deelnemerList.size(); i++) {
            Deelnemer deelnemer = deelnemerList.get(i);
            array[0][i + 1] = deelnemer.getName();
        }

        vulMeetingen(array, deelnemerList);
        return array;
    }

    private void vulMeetingen(Object[][] array, List<Deelnemer> deelnemerList) {
        Map<String, Double[]> metingen = new HashMap<>();

        for (int i = 0; i < deelnemerList.size(); i++) {
            Deelnemer deelnemer = deelnemerList.get(i);
            List<Meeting> deelnemerMeetingen = deelnemer.getMetingen();
            for (Meeting meeting : deelnemerMeetingen) {
                String meetDatum = meeting.getTime().getDayOfMonth() + "-" + meeting.getTime().getMonthValue();
                if (metingen.containsKey(meetDatum)) {
                    Double[] metingenOpDatum = metingen.get(meetDatum);
                    metingenOpDatum[i] = meeting.getGewicht();
                } else {
                    Double[] metingenOpDatum = new Double[deelnemerList.size()];
                    metingenOpDatum[i] = meeting.getGewicht();
                    metingen.put(meetDatum, metingenOpDatum);
                }
            }
        }

        SortedSet<String> keys = new TreeSet<String>(metingen.keySet());
        int counter = 1;
        for (String key : keys) {
            Double[] mtng = metingen.get(key);
            array[counter][0] = key;
            array[counter][1] = mtng[0];
            array[counter][2] = mtng[1];
            counter++;
        }
    }

}
