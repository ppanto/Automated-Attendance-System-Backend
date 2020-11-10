package com.panto.attendance.service;

import com.panto.attendance.dto.attendance.AttendanceActionSingularResponse;
import com.panto.attendance.dto.reporting.ChartReportResponse;
import com.panto.attendance.dto.reporting.TimeReportPerPersonnelForDateResponse;
import com.panto.attendance.dto.reporting.TimeReportPerPersonnelResponse;
import com.panto.attendance.dto.reporting.TimeReportResponse;
import com.panto.attendance.mapper.MyDateMapper;
import com.panto.attendance.mapper.PersonnelMapper;
import com.panto.attendance.model.AttendanceAction;
import com.panto.attendance.model.Leave;
import com.panto.attendance.repository.AttendanceActionRepository;
import com.panto.attendance.repository.LeaveRepository;
import com.panto.attendance.repository.MyDateRepository;
import com.panto.attendance.repository.PersonnelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AttendanceReportService {
    private final AttendanceActionRepository attendanceActionRepository;
    private final MyDateRepository myDateRepository;
    private final MyDateMapper myDateMapper;
    private final PersonnelRepository personnelRepository;
    private final PersonnelMapper personnelMapper;
    private final LeaveRepository leaveRepository;

    public TimeReportResponse getTimeReport(Date dateStart, Date dateEnd) {
        TimeReportResponse finalResponse = new TimeReportResponse();
        finalResponse.setPersonnelTimesList(
            personnelRepository
                    .findByActiveStatus(true)
                    .stream().map(personnelMapper::mapToTimeReportForPersonnel)
                    .collect(Collectors.toList())
        );
        List<TimeReportPerPersonnelForDateResponse> datesForPersonnel = myDateRepository
                .findDatesInRange(dateStart, dateEnd)
                .stream().map(myDateMapper::mapDateToTimeReportPerPersonnelForDate)
                .collect(Collectors.toList());
        List<AttendanceAction> allActionsInRange = attendanceActionRepository
                .findByBetweenDatesOrdered(dateStart, dateEnd);
        List<Leave> allLeavesInRange = leaveRepository.findByDateRange(dateStart, dateEnd);

        for(TimeReportPerPersonnelResponse perPersonnelReport : finalResponse.getPersonnelTimesList()){
            perPersonnelReport.setTimeReportPerPersonnelForDateResponseList(
                    cloneList(datesForPersonnel)
            );
            getReportDataForOnePersonnel(perPersonnelReport, allActionsInRange);
            perPersonnelReport.setTotalLeaves(
                    (int)(allLeavesInRange.stream().filter(
                            l -> l.getPersonnelId() == perPersonnelReport.getPersonnelId()
                    )
                    .count())
            );

            finalResponse.setTotalDaysWorked(finalResponse.getTotalDaysWorked() + perPersonnelReport.getTotalDaysWorked());
            finalResponse.setTotalHolidayTimeWorked(finalResponse.getTotalHolidayTimeWorked() + perPersonnelReport.getTotalHolidayTimeWorked());
            finalResponse.setTotalLeaves(finalResponse.getTotalLeaves() + perPersonnelReport.getTotalLeaves());
            finalResponse.setTotalRegularTimeWorked(finalResponse.getTotalRegularTimeWorked() + perPersonnelReport.getTotalRegularTimeWorked());
            finalResponse.setTotalTimeBreaks(finalResponse.getTotalTimeBreaks() + perPersonnelReport.getTotalTimeBreaks());
            finalResponse.setTotalTimeOfficial(finalResponse.getTotalTimeOfficial() + perPersonnelReport.getTotalTimeOfficial());
            finalResponse.setTotalTimeWorked(finalResponse.getTotalTimeWorked() + perPersonnelReport.getTotalTimeWorked());
            finalResponse.setTotalWeekendTimeWorked(finalResponse.getTotalWeekendTimeWorked() + perPersonnelReport.getTotalWeekendTimeWorked());
        }
        return finalResponse;
    }

    private List<TimeReportPerPersonnelForDateResponse> cloneList(List<TimeReportPerPersonnelForDateResponse> datesForPersonnel) {
        List<TimeReportPerPersonnelForDateResponse> returnList = new ArrayList<>();
        for(TimeReportPerPersonnelForDateResponse r : datesForPersonnel){
            TimeReportPerPersonnelForDateResponse addMe = new TimeReportPerPersonnelForDateResponse(
                    r.getFullDate(),
                    r.isHoliday(),
                    r.isWeekend()
            );
            returnList.add(addMe);
        }
        return returnList;
    }

    private void getReportDataForOnePersonnel(TimeReportPerPersonnelResponse personnel, List<AttendanceAction> allActionsInRange){
        for(TimeReportPerPersonnelForDateResponse oneDay : personnel.getTimeReportPerPersonnelForDateResponseList()){
            List<AttendanceAction> actionsInDay = allActionsInRange.stream()
                    .filter(a -> a.getDate().compareTo(oneDay.getFullDate()) == 0
                        && a.getPersonnelId() == personnel.getPersonnelId())
                    .sorted(Comparator.comparing(AttendanceAction::getDateTime).reversed())
                    .collect(Collectors.toList());

            for(int i=0;i<actionsInDay.size();i++){
                Long eventId = actionsInDay.get(i).getAttendanceEvent().getId();
                Long pairEventId = null;
                if(eventId == 4) pairEventId = 1L;
                else if(eventId == 5) pairEventId = 2L;
                else if(eventId == 6) pairEventId = 3L;
                Long time = null;
                for(int j=i+1;j<actionsInDay.size();j++){
                    AttendanceAction theAction = actionsInDay.get(j);
                    if(theAction.getPersonnelId() == actionsInDay.get(i).getPersonnelId()
                            && theAction.getAttendanceEvent().getId() == pairEventId){
                        time = actionsInDay.get(i).getDateTime().getTime() - theAction.getDateTime().getTime();
                        break;
                    }
                }
                if(time == null){
                    time = actionsInDay.get(i).getDateTime().getTime() - oneDay.getFullDate().getTime();
                }
                if(eventId == 4){
                    oneDay.setTotalTimeWorked(oneDay.getTotalTimeWorked() + time);
                    personnel.setTotalDaysWorked(personnel.getTotalDaysWorked() + 1);
                    personnel.setTotalTimeWorked(personnel.getTotalTimeWorked() + time);
                    if(oneDay.isHoliday()){
                        oneDay.setTotalHolidayTimeWorked(oneDay.getTotalHolidayTimeWorked() + time);
                        personnel.setTotalHolidayTimeWorked(personnel.getTotalHolidayTimeWorked() + time);
                    }
                    else if(oneDay.isWeekend()){
                        oneDay.setTotalWeekendTimeWorked(oneDay.getTotalWeekendTimeWorked() + time);
                        personnel.setTotalWeekendTimeWorked(personnel.getTotalWeekendTimeWorked() + time);
                    }
                    else{
                        oneDay.setTotalRegularTimeWorked(oneDay.getTotalRegularTimeWorked() + time);
                        personnel.setTotalRegularTimeWorked(personnel.getTotalRegularTimeWorked() + time);
                    }
                }
                else if(eventId == 5){
                    oneDay.setTotalTimeBreaks(oneDay.getTotalTimeBreaks() + time);
                    personnel.setTotalTimeBreaks(personnel.getTotalTimeBreaks() + time);
                }
                else if(eventId == 6){
                    oneDay.setTotalTimeOfficial(oneDay.getTotalTimeOfficial() + time);
                    personnel.setTotalTimeOfficial(personnel.getTotalTimeOfficial() + time);
                }
            }
        }
    }

    public List<ChartReportResponse> getChartReport(Date dateStart, Date dateEnd) {
        List<ChartReportResponse> finalResponse = myDateRepository.findDatesInRange(dateStart,dateEnd)
                .stream().map(myDateMapper::mapDateToChartResponse)
                .collect(Collectors.toList());
        List<AttendanceAction> allActionsInDateRange = attendanceActionRepository.findByBetweenDatesOrdered(dateStart,dateEnd);

        for(ChartReportResponse oneDay : finalResponse){
            HashSet<Long> uniqueEmployeeId = new HashSet<>();

            List<AttendanceAction> actionsInDay = allActionsInDateRange.stream()
                    .filter(a -> a.getDate().compareTo(oneDay.getFullDate()) == 0)
                    .sorted(Comparator.comparing(AttendanceAction::getDateTime).reversed())
                    .collect(Collectors.toList());

            for(int i=0;i<actionsInDay.size();i++){
                if(!uniqueEmployeeId.contains(actionsInDay.get(i).getPersonnelId())){
                    uniqueEmployeeId.add(actionsInDay.get(i).getPersonnelId());
                    oneDay.setPersonnelAtWork(oneDay.getPersonnelAtWork() + 1);
                }

                Long eventId = actionsInDay.get(i).getAttendanceEvent().getId();
                Long pairEventId = null;
                if(eventId == 4) pairEventId = 1L;
                else if(eventId == 5) pairEventId = 2L;
                else if(eventId == 6) pairEventId = 3L;

                //if(eventId == 4 ||eventId == 5 ||eventId==6){
                    Long time = null;
                    for(int j=i+1;j<actionsInDay.size();j++){
                        AttendanceAction theAction = actionsInDay.get(j);
                        if(theAction.getPersonnelId() == actionsInDay.get(i).getPersonnelId()
                            && theAction.getAttendanceEvent().getId() == pairEventId){
                            time = actionsInDay.get(i).getDateTime().getTime() - theAction.getDateTime().getTime();
                            break;
                        }
                    }
                    if(time == null){
                        time = actionsInDay.get(i).getDateTime().getTime() - oneDay.getFullDate().getTime();
                    }
                    if(eventId == 4) oneDay.setTotalTimeWorked(oneDay.getTotalTimeWorked() + time);
                    else if(eventId == 5) oneDay.setTotalTimeBreaks(oneDay.getTotalTimeBreaks() + time);
                    else if(eventId == 6) oneDay.setTotalTimeOfficial(oneDay.getTotalTimeOfficial() + time);
                //}
            }
        }

        return finalResponse;
    }
}
