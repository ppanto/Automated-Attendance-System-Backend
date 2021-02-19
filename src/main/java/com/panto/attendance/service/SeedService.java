package com.panto.attendance.service;

import com.panto.attendance.model.*;
import com.panto.attendance.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
@AllArgsConstructor
public class SeedService {
    private final MyDateRepository myDateRepository;
    private AttendanceEventRepository eventRepository;
    private ApplicationUserRepository userRepository;
    private PersonnelRepository personnelRepository;
    private PersonnelImageRepository pImageRepository;
    private PasswordEncoder passwordEncoder;

    public void runAllSeeds(){
        List events = eventRepository.findAll();
        if(events.size() != 0) return;
        seedDateTable();
        seedAttendanceEventTable();
        seedPersonnelTable();
    }

    private void seedPersonnelTable(){ // also includes user account and image
        Personnel personnel = new Personnel();
        personnel.setFirstName("Admin");
        personnel.setLastName("Admin");
        personnelRepository.save(personnel);

        PersonnelImage pImage = new PersonnelImage(personnel.getId(), personnel,null);
        pImageRepository.save(pImage);

        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setPersonnel(personnel);
        applicationUser.setUsername("Admin");
        applicationUser.setAccountActive(true);
        applicationUser.setPasswordHash(passwordEncoder.encode("adminAdmin"));
        userRepository.save(applicationUser);
    }
    private void seedAttendanceEventTable(){
        AttendanceEvent workStart = new AttendanceEvent();
        workStart.setName("Work Start");
        AttendanceEvent breakStart = new AttendanceEvent();
        breakStart.setName("Break Start");
        AttendanceEvent officialStart = new AttendanceEvent();
        officialStart.setName("Official Absence Start");
        AttendanceEvent workEnd = new AttendanceEvent();
        workEnd.setName("Work End");
        AttendanceEvent breakEnd = new AttendanceEvent();
        breakEnd.setName("Break End");
        AttendanceEvent officialEnd = new AttendanceEvent();
        officialEnd.setName("Official Absence End");

        eventRepository.save(workStart);
        eventRepository.save(breakStart);
        eventRepository.save(officialStart);
        eventRepository.save(workEnd);
        eventRepository.save(breakEnd);
        eventRepository.save(officialEnd);
    }
    private void seedDateTable(){
        int howManyDaysAgoStart = 500;
        int goForThisManyDays = 1500;

        LocalDateTime ldt = LocalDateTime.now().minusDays(howManyDaysAgoStart);
        java.util.Date dateFrom = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateFrom);
        int count = calendar.get(Calendar.DAY_OF_WEEK);

        List<MyDate> myDateList = new ArrayList<MyDate>();
        Long milisecondsInDay = 86400000L;
        Long milisecondsForDateCreation = (System.currentTimeMillis() - (howManyDaysAgoStart * milisecondsInDay));
        Date dateAsDateObject = new Date(milisecondsForDateCreation);
        boolean isHoliday = false;
        boolean isWeekend = false;

        for(long i = 1; i<(goForThisManyDays); i++){
            switch (count){
                case 2: // Monday ...
                case 3:
                case 4:
                case 5:
                case 6:
                    isWeekend = false;
                    break;
                case 1: // Sunday
                case 7: // Saturday
                    isWeekend = true;
                    break;
            }

            MyDate currentDate = new MyDate();
            currentDate.setDayInWeekNumber((short)count);
            currentDate.setFullDate(dateAsDateObject);
            currentDate.setHoliday(isHoliday);
            currentDate.setWeekend(isWeekend);

            LocalDate localDate = dateAsDateObject.toLocalDate();
            currentDate.setYearNumber(((short) localDate.getMonthValue()));
            currentDate.setMonthNumber(((short)localDate.getMonthValue()));

            myDateList.add(currentDate);

            milisecondsForDateCreation+=milisecondsInDay;
            dateAsDateObject = new Date(milisecondsForDateCreation);

            count++;
            if(count>7) count = 1;
        }
        myDateRepository.saveAll(myDateList);
    }
}
