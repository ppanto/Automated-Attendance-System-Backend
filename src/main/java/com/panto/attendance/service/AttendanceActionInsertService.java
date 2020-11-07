package com.panto.attendance.service;

import com.panto.attendance.dto.attendance.AttendanceActionSimpleResponse;
import com.panto.attendance.helpers.AttendanceInsertRequest;
import com.panto.attendance.model.AttendanceAction;
import com.panto.attendance.repository.AttendanceActionRepository;
import com.panto.attendance.repository.AttendanceEventRepository;
import com.panto.attendance.repository.PersonnelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;


@Service
@AllArgsConstructor
public class AttendanceActionInsertService {
    private final AttendanceActionRepository attendanceActionRepository;
    private final AttendanceEventRepository attendanceEventRepository;
    private final PersonnelRepository personnelRepository;

    public AttendanceActionSimpleResponse insert(AttendanceInsertRequest request){
        AttendanceActionSimpleResponse response = new AttendanceActionSimpleResponse();
        response.setId(null);
        //response.setPersonnelId(request.personnelId);
        //Personnel p = personnelRepository.getOne(response.getPersonnelId());
        //response.setPersonnelName(p.getFirstName() + " " + p.getLastName());

        List<AttendanceAction> lastEntries = attendanceActionRepository.findLastNByPersonnel(15, request.personnelId);
        AttendanceAction action = new AttendanceAction(
                new Timestamp(System.currentTimeMillis()),//null,
                personnelRepository.getOne(request.personnelId),
                new Date(System.currentTimeMillis())
        );
        //if(request.timeStamp != null) action.setDateTime(request.timeStamp); // disabled because of last n personnel from x point
        //else action.setDateTime(new Timestamp(System.currentTimeMillis())); //<- care for this. does it play with time zones?
        if(request.buttonId == 1){ // Work Start or End Button
            //add check for no cuts
            //if(lastEntries.isEmpty() || lastEntries.get(0).getAttendanceEvent().getId() == 4) { //Work End
            //    action.setAttendanceEvent(attendanceEventRepository.getOne(1L));
            //    attendanceActionRepository.save(action);
            //}
            boolean startFound = false;
            for(int i=0; i<lastEntries.size(); i++){
                if(lastEntries.get(i).getAttendanceEvent().getId() == 1){ //Work Start
                    startFound = true;
                    action.setAttendanceEvent(attendanceEventRepository.getOne(4L)); //Work End
                    break;
                }
                else if(lastEntries.get(i).getAttendanceEvent().getId() == 4){ //Work End
                    startFound = true;
                    action.setAttendanceEvent(attendanceEventRepository.getOne(1L)); //Work Start
                    break;
                }
            }
            if(!startFound){
                action.setAttendanceEvent(attendanceEventRepository.getOne(1L));
            }
            attendanceActionRepository.save(action);
        }
        else if(request.buttonId == 2){ // Break Start or End Button
            //add check for work start and no work end
            boolean pauseFound = false;
            for(int i=0; i<lastEntries.size(); i++){
                if(lastEntries.get(i).getAttendanceEvent().getId() == 2){ //Break Start
                    pauseFound = true;
                    action.setAttendanceEvent(attendanceEventRepository.getOne(5L)); //Break End
                    break;
                }
                else if(lastEntries.get(i).getAttendanceEvent().getId() == 5){ //Break End
                    pauseFound = true;
                    action.setAttendanceEvent(attendanceEventRepository.getOne(2L)); //Break Start
                    break;
                }
            }
            if(!pauseFound){
                action.setAttendanceEvent(attendanceEventRepository.getOne(2L));
            }
            attendanceActionRepository.save(action);
        }
        else if(request.buttonId == 3){ // Official Start or End Button
            //add check for work start and no work end
            boolean leaveFound = false;
            for(int i=0; i<lastEntries.size(); i++){
                if(lastEntries.get(i).getAttendanceEvent().getId() == 3){ //Leave Start
                    leaveFound = true;
                    action.setAttendanceEvent(attendanceEventRepository.getOne(6L)); //Leave End
                    break;
                }
                else if(lastEntries.get(i).getAttendanceEvent().getId() == 6){ //Leave End
                    leaveFound = true;
                    action.setAttendanceEvent(attendanceEventRepository.getOne(3L)); //Leave Start
                    break;
                }
            }
            if(!leaveFound){
                action.setAttendanceEvent(attendanceEventRepository.getOne(3L));
            }
            attendanceActionRepository.save(action);
        }
        //else if(request.buttonId == 4){ // Work End Button
        //}

        if(!action.getId().equals(null)){
            response.setId(action.getId());
            response.setPersonnelId(action.getPersonnelId());
            response.setPersonnelName(action.getPersonnel().getFirstName() + " " + action.getPersonnel().getLastName());
            response.setEvent(action.getAttendanceEvent().getName());
            response.setEventId(action.getAttendanceEvent().getId());
            response.setDateTime(action.getDateTime().toLocalDateTime());
        }
        return response;
    }
}
