package com.panto.attendance.service;

import com.panto.attendance.dto.PersonnelExtraSimpleResponse;
import com.panto.attendance.dto.attendance.*;
import com.panto.attendance.dto.reporting.DailyReportResponse;
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
            finalResponse.add(
                    new AttendanceActionSimpleResponse(
                            action.getPersonnelId(),
                            action.getPersonnel().getFirstName() + " " + action.getPersonnel().getLastName(),
                            action.getDateTime().toLocalDateTime(),
                            action.getAttendanceEvent().getName(),
                            action.getAttendanceEvent().getId(),
                            action.getId()
                    )
            );
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
                        action.getDateTime().toLocalDateTime(),
                        action.getAttendanceEvent().getName(),
                        action.getAttendanceEvent().getId()
                );
                singularResponses.add(singularResponse);
            }
            singularResponses.sort(Comparator.comparing(AttendanceActionSingularResponse::getDateTime));
            forOnePersonnel.setAttendanceActionSingularResponseList(singularResponses);

            if(forOnePersonnel.getAttendanceActionLeavePartialResponse() == null
                && forOnePersonnel.getAttendanceActionShiftPartialResponse() != null){
                for(AttendanceActionSingularResponse action : forOnePersonnel.getAttendanceActionSingularResponseList()){
                    if(action.getEventId() == 1){ //Work Start
                        forOnePersonnel.setStartTimeRegular(getIsStartTimeRegular(
                                forOnePersonnel.getAttendanceActionShiftPartialResponse().getShiftStartTime(),
                                action.getDateTime()
                        ));
                    }
                    else if(action.getEventId() == 4){ //Work End
                        forOnePersonnel.setEndTimeRegular(getIsEndTimeRegular(
                                forOnePersonnel.getAttendanceActionShiftPartialResponse().getShiftEndTime(),
                                action.getDateTime()
                        ));
                    }
                }
            }

            listForFinalResponse.add(forOnePersonnel);
        }

        finalResponse.setAttendanceActionDatePersonnelResponses(listForFinalResponse);
        return finalResponse;
    }

    public DailyReportResponse getDailyReport(Date date){
        DailyReportResponse finalResponse = new DailyReportResponse();
        List<Personnel> allPersonnel = personnelRepository.findByActiveStatus(true);
        List<AttendanceAction> allActionsForGivenDate = attendanceActionRepository.findByDate(date);
        List<Leave> allLeavesForGivenDate = leaveRepository.findByDate(date);

        HashSet<Long> uniquePersonnelIdForPresent = new HashSet<>();
        HashSet<Long> uniquePersonnelIdForCheckOut = new HashSet<>();
        HashSet<Personnel> hasCheckInButNoCheckOut = new HashSet<>(); // = people currently at work

        for(AttendanceAction action : allActionsForGivenDate){
            if(action.getAttendanceEvent().getId() == 1L){
                if(!uniquePersonnelIdForPresent.contains(action.getPersonnelId())){
                    uniquePersonnelIdForPresent.add(action.getPersonnelId());
                    finalResponse.setPresent(finalResponse.getPresent()+1);
                }
            }
            else if(action.getAttendanceEvent().getId() == 4L){
                if(!uniquePersonnelIdForCheckOut.contains(action.getPersonnelId())){
                    uniquePersonnelIdForCheckOut.add(action.getPersonnelId());
                    finalResponse.setCheckedOut(finalResponse.getCheckedOut()+1);
                }
            }
        }

        for(Personnel p : allPersonnel){
            if(!uniquePersonnelIdForCheckOut.contains(p.getId()) && uniquePersonnelIdForPresent.contains(p.getId())){
                hasCheckInButNoCheckOut.add(p);
            }
        }
        List<PersonnelExtraSimpleResponse> currentlyNotAtWork = new ArrayList<>();
        for(Personnel p : allPersonnel){
            if(!customDoesContain(hasCheckInButNoCheckOut, p.getId())){
                currentlyNotAtWork.add(personnelMapper.mapToDtoExtraSimpleResponse(p));
            }
        }

        for(Leave l : allLeavesForGivenDate){
            // does not check for uniqueness
            if(l.isApproved()){
                finalResponse.setApprovedLeave(finalResponse.getApprovedLeave()+1);
            }
        }

        finalResponse.setUnapprovedLeave(allLeavesForGivenDate.size()-finalResponse.getApprovedLeave());
        finalResponse.setCurrentlyNotAtWork(currentlyNotAtWork);
        finalResponse.setCurrentlyAtWork(hasCheckInButNoCheckOut.stream().map(personnelMapper::mapToDtoExtraSimpleResponse).collect(Collectors.toList()));
        finalResponse.setCheckedIn(finalResponse.getPresent());

        finalResponse.setTotalPersonnel(allPersonnel.size());
        finalResponse.setAbsent(finalResponse.getTotalPersonnel() - finalResponse.getPresent());
        finalResponse.setYetToCheckIn(finalResponse.getTotalPersonnel() - allLeavesForGivenDate.size() - finalResponse.getPresent()); // does not check for uniqueness of leaves
        return finalResponse;
    }

    private boolean customDoesContain(HashSet<Personnel> hashset, Long id){
        for(Personnel p : hashset){
            if(p.getId() == id) return true;
        }
        return false;
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
