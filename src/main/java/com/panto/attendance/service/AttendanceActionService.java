package com.panto.attendance.service;

import com.panto.attendance.dto.PersonnelExtraSimpleResponse;
import com.panto.attendance.dto.attendance.*;
import com.panto.attendance.dto.reporting.DailyReportResponse;
import com.panto.attendance.helpers.enums.AttendanceActionEnum;
import com.panto.attendance.mapper.PersonnelMapper;
import com.panto.attendance.model.*;
import com.panto.attendance.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AttendanceActionService {
    private final AttendanceActionRepository attendanceActionRepository;
    private final MyDateRepository myDateRepository;
    private final PersonnelRepository personnelRepository;
    private final LeaveRepository leaveRepository;
    private final PersonnelShiftRepository personnelShiftRepository;
    private final PersonnelMapper personnelMapper;

    public List<AttendanceActionSimpleResponse> getSimpleByDateRange(Date dateStart, Date dateEnd){
        List<AttendanceAction> allActionsInDateRange = attendanceActionRepository.findByBetweenDates(dateStart, dateEnd);
        List<AttendanceActionSimpleResponse> finalResponse = new ArrayList<>();

        for(AttendanceAction action : allActionsInDateRange){
            AttendanceActionSimpleResponse oneResponse = new AttendanceActionSimpleResponse(
                    action.getPersonnelId(),
                    null,
                    action.getDateTime().toLocalDateTime(),//.plusHours(1), // temporary added +1 hour till I fix running on cloud
                    action.getAttendanceEvent().getName(),
                    action.getAttendanceEvent().getId(),
                    action.getId()
            );
            if(action.getPersonnelId() != null){
                oneResponse.setPersonnelName(action.getPersonnel().getFirstName() + " " + action.getPersonnel().getLastName());
            }
            finalResponse.add(oneResponse);
        }

        //finalResponse.sort(Comparator.comparing((AttendanceActionSimpleResponse aa) -> aa.getDateTime()).reversed());
        finalResponse.sort(Comparator.comparing(AttendanceActionSimpleResponse::getDateTime).reversed());
        return finalResponse;
    }

    public AttendanceActionDateResponse getByDate(Date date){
        AttendanceActionDateResponse finalResponse = new AttendanceActionDateResponse();

        MyDate myDate = myDateRepository.findByFullDate(date);
        finalResponse.setDate(date);
        finalResponse.setHoliday(myDate.isHoliday());
        finalResponse.setWeekend(myDate.isWeekend());

        List<AttendanceActionDatePersonnelResponse> listForFinalResponse = new ArrayList<>();
        List<AttendanceAction> allActionsInThatDate = attendanceActionRepository.findByDate(date);
        List<Personnel> allPersonnel = personnelRepository.findByActiveStatus(true);

        for(Personnel person : allPersonnel){
            AttendanceActionDatePersonnelResponse forOnePersonnel = new AttendanceActionDatePersonnelResponse();
            List<Leave> leave = leaveRepository.findByPersonnelIdAndDate(person.getId(), date);
            if(leave.isEmpty()){
                forOnePersonnel.setAttendanceActionLeavePartialResponse(null);
            }
            else{
                forOnePersonnel.setAttendanceActionLeavePartialResponse(
                        new AttendanceActionLeavePartialResponse(
                                leave.get(0).getId(),
                                leave.get(0).getDescription(),
                                leave.get(0).getLeaveType().getName(),
                                leave.get(0).getLeaveType().getId(),
                                leave.get(0).isApproved()
                        )
                );
            }
            List<PersonnelShift> personnelShifts = personnelShiftRepository.findByPersonnelId(person.getId());
            PersonnelShift correctShift = null;
            for(PersonnelShift ps : personnelShifts){
                if((ps.getStartDate().compareTo(date) == 0) ||
                        (ps.getEndDate().compareTo(date) == 0) ||
                        (ps.getStartDate().compareTo(date) < 0 && ps.getEndDate().compareTo(date) > 0)
                ){
                    correctShift = ps;
                    break;
                }
            }
            if(correctShift != null){
                forOnePersonnel.setAttendanceActionShiftPartialResponse(
                        new AttendanceActionShiftPartialResponse(
                            correctShift.getShiftType().getStartTime(),
                            correctShift.getShiftType().getEndTime(),
                            correctShift.getShiftType().getName()
                        )
                );
            }
            else{
                forOnePersonnel.setAttendanceActionShiftPartialResponse(null);
            }

            forOnePersonnel.setPersonnelId(person.getId());
            forOnePersonnel.setPersonnelFullName(person.getFirstName() + " " + person.getLastName());

            List<AttendanceAction> filteredForPersonnel = allActionsInThatDate
                    .stream()
                    .filter(p -> p.getPersonnelId() == person.getId())
                    .collect(Collectors.toList());

            List<AttendanceActionSingularResponse> singularResponses = new ArrayList<>();
            for(AttendanceAction action : filteredForPersonnel){
                AttendanceActionSingularResponse singularResponse = new AttendanceActionSingularResponse(
                        action.getId(),
                        action.getDateTime().toLocalDateTime(),//.plusHours(1), // temporary added +1 hour till I fix running on cloud,
                        action.getAttendanceEvent().getName(),
                        action.getAttendanceEvent().getId()
                );
                singularResponses.add(singularResponse);
            }
            singularResponses.sort(Comparator.comparing(AttendanceActionSingularResponse::getDateTime));
            forOnePersonnel.setAttendanceActionSingularResponseList(singularResponses);

            if(forOnePersonnel.getAttendanceActionLeavePartialResponse() == null
                && forOnePersonnel.getAttendanceActionShiftPartialResponse() != null)
            {
                boolean hasStart = false;
                for(AttendanceActionSingularResponse action : forOnePersonnel.getAttendanceActionSingularResponseList()){
                    if(action.getEventId() == 1){ //Work Start
                        forOnePersonnel.setStartTimeRegular(getIsStartTimeRegular(
                                forOnePersonnel.getAttendanceActionShiftPartialResponse().getShiftStartTime(),
                                action.getDateTime()
                        ));
                        hasStart = true;
                    }
                    else if(action.getEventId() == 4){ //Work End
                        forOnePersonnel.setEndTimeRegular(getIsEndTimeRegular(
                                forOnePersonnel.getAttendanceActionShiftPartialResponse().getShiftEndTime(),
                                action.getDateTime()
                        ));
                    }
                }
                if(!hasStart){
                    forOnePersonnel.setStartTimeRegular(getIsStartTimeRegular(
                            forOnePersonnel.getAttendanceActionShiftPartialResponse().getShiftStartTime(),
                            LocalDateTime.now()
                    ));
                }
            }


            listForFinalResponse.add(forOnePersonnel);
        }

        finalResponse.setAttendanceActionDatePersonnelResponses(listForFinalResponse);
        return finalResponse;
    }
    public DailyReportResponse getDailyReport(Date date){
        DailyReportResponse finalResponse = new DailyReportResponse();

        List<AttendanceAction> allActionsForGivenDate = attendanceActionRepository.findByDateOrdered(date);

        List<Personnel> allPersonnel = personnelRepository.findByActiveStatus(true);
        HashMap<Long, Personnel> allPersonnelHashMap = new HashMap<>();
        for(Personnel p : allPersonnel) allPersonnelHashMap.put(p.getId(), p);
        List<Leave> allLeavesForGivenDate = leaveRepository.findByDate(date);

        finalResponse.setTotalPersonnel(allPersonnel.size());

        HashSet<Long> uniquePersonnelIdForPresent = new HashSet<>();

        HashSet<Long> uniquePersonnelIdForCheckIn = new HashSet<>();
        HashSet<Long> uniquePersonnelIdForCheckOut = new HashSet<>();

        HashMap<Long, Integer> uniquePersonnelIdForCurrentlyAtWork = new HashMap<>();
        HashSet<Long> uniquePersonnelIdForCurrentlyOutside = new HashSet<>();

        for(AttendanceAction action : allActionsForGivenDate){
            Long actionEventId = action.getAttendanceEvent().getId();
            Long personnelId = action.getPersonnelId();
            if(actionEventId == AttendanceActionEnum.WORK_START.getValue()){
                uniquePersonnelIdForPresent.add(personnelId);

                uniquePersonnelIdForCheckIn.add(personnelId);

                uniquePersonnelIdForCurrentlyAtWork.put(personnelId, 1);
            }
            else if(actionEventId == AttendanceActionEnum.BREAK_START.getValue() ||
                    actionEventId == AttendanceActionEnum.OFFICIAL_START.getValue() ){
                var value = uniquePersonnelIdForCurrentlyAtWork.get(personnelId);
                if(value != null) {
                    uniquePersonnelIdForCurrentlyAtWork.put(personnelId, value - 1);
                }
            }
            else if(actionEventId == AttendanceActionEnum.BREAK_END.getValue() ||
                    actionEventId == AttendanceActionEnum.OFFICIAL_END.getValue() ){
                var value = uniquePersonnelIdForCurrentlyAtWork.get(personnelId);
                if(value != null) {
                    uniquePersonnelIdForCurrentlyAtWork.put(personnelId, value + 1);
                }
                else{
                    uniquePersonnelIdForCurrentlyAtWork.put(personnelId, 1);
                }
            }
            else if(actionEventId == AttendanceActionEnum.WORK_END.getValue()){
                uniquePersonnelIdForCheckOut.add(personnelId);
                uniquePersonnelIdForCheckIn.remove(personnelId);

                uniquePersonnelIdForCurrentlyAtWork.remove(personnelId);
            }
        }
        finalResponse.setPresent(uniquePersonnelIdForPresent.size());
        finalResponse.setCheckedIn(uniquePersonnelIdForCheckIn.size());
        finalResponse.setCheckedOut(uniquePersonnelIdForCheckOut.size());

        int haveLeaveButPersonnelAtWork = 0;
        for(Leave leave : allLeavesForGivenDate){
            if(leave.isApproved()) finalResponse.setApprovedLeave(finalResponse.getApprovedLeave() + 1);
            else finalResponse.setUnapprovedLeave(finalResponse.getUnapprovedLeave() + 1);
            if(uniquePersonnelIdForPresent.contains(leave.getPersonnelId())) haveLeaveButPersonnelAtWork++;
        }

        Collection<PersonnelExtraSimpleResponse> currentlyAtWork = new ArrayList<>();
        uniquePersonnelIdForCurrentlyAtWork.forEach((id, value) -> {
            if(value == 1) {
                currentlyAtWork.add(personnelMapper.mapToDtoExtraSimpleResponse(
                        allPersonnelHashMap.get(id)
                ));
            }
        });
        finalResponse.setCurrentlyAtWork(currentlyAtWork);

        for(Personnel p : allPersonnel){
            var value = uniquePersonnelIdForCurrentlyAtWork.get(p.getId());
            if(value == null || value != 1){
                uniquePersonnelIdForCurrentlyOutside.add(p.getId());
            }
        }
        List<PersonnelExtraSimpleResponse> currentlyNotAtWork = new ArrayList<>();
        uniquePersonnelIdForCurrentlyOutside.forEach((id) -> {
            currentlyNotAtWork.add(personnelMapper.mapToDtoExtraSimpleResponse(
                    allPersonnelHashMap.get(id)
            ));
        });
        finalResponse.setCurrentlyNotAtWork(currentlyNotAtWork);

        finalResponse.setYetToCheckIn(finalResponse.getTotalPersonnel() - finalResponse.getApprovedLeave() - finalResponse.getUnapprovedLeave() - finalResponse.getPresent() + haveLeaveButPersonnelAtWork);
        finalResponse.setAbsent(finalResponse.getTotalPersonnel() - finalResponse.getPresent());

        return finalResponse;
    }

    private boolean getIsEndTimeRegular(Time shiftEndTime, LocalDateTime dateTime) {
        Time workEnd = convertLocalDateTimeToTime(dateTime);
        if(shiftEndTime.getTime() > workEnd.getTime()){
            return false;
        }
        return true;
    }

    private boolean getIsStartTimeRegular(Time shiftStartTime, LocalDateTime dateTime) {
        Time workStart = convertLocalDateTimeToTime(dateTime);
        if(shiftStartTime.getTime() < workStart.getTime()){
            return false;
        }
        return true;
    }
    private Time convertLocalDateTimeToTime(LocalDateTime localDateTime){
        return Time.valueOf(
                localDateTime.getHour() + ":" +
                localDateTime.getMinute() + ":" +
                localDateTime.getSecond());
    }

    public boolean delete(Long id){
        Optional<AttendanceAction> database = attendanceActionRepository.findById(id);
        if(database.isEmpty()) return false;
        attendanceActionRepository.delete(database.get());
        return true;
    }
}
