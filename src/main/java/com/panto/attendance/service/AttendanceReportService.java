package com.panto.attendance.service;

import com.panto.attendance.dto.attendance.AttendanceActionSingularResponse;
import com.panto.attendance.dto.reporting.ChartReportResponse;
import com.panto.attendance.dto.reporting.TimeReportResponse;
import com.panto.attendance.mapper.MyDateMapper;
import com.panto.attendance.model.AttendanceAction;
import com.panto.attendance.repository.AttendanceActionRepository;
import com.panto.attendance.repository.MyDateRepository;
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

    public TimeReportResponse getTimeReport(Date dateStart, Date dateEnd) {
        TimeReportResponse finalResponse = new TimeReportResponse();
        return finalResponse;
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
