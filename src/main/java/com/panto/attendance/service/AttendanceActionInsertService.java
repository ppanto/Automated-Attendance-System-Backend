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

        // user id is null case (unknown user)
        if(request.personnelId == null){
            AttendanceAction action = new AttendanceAction(
                    new Timestamp(System.currentTimeMillis()),
                    null,
                    new Date(System.currentTimeMillis())
            );
            if(request.buttonId == 1) action.setAttendanceEvent(attendanceEventRepository.getOne(1L));
            else if(request.buttonId == 2) action.setAttendanceEvent(attendanceEventRepository.getOne(2L));
            else if(request.buttonId == 3) action.setAttendanceEvent(attendanceEventRepository.getOne(3L));
            else if(request.buttonId == 4) action.setAttendanceEvent(attendanceEventRepository.getOne(4L));

            attendanceActionRepository.save(action);

            response.setId(action.getId());
            response.setPersonnelId(null);
            response.setPersonnelName(null);
            response.setEvent(action.getAttendanceEvent().getName());
            response.setEventId(action.getAttendanceEvent().getId());
            response.setDateTime(action.getDateTime().toLocalDateTime());
            response.setMessageCode(10);

            return response;
        }

        AttendanceAction action = new AttendanceAction(
                null,
                personnelRepository.getOne(request.personnelId),
                null
        );

        response.setPersonnelName(action.getPersonnel().getFirstName() + " " + action.getPersonnel().getLastName());

        if(request.timeStamp != null){
            action.setDateTime(request.timeStamp);
            action.setDate(new Date(request.timeStamp.getTime()));
        }
        else{
            action.setDateTime(new Timestamp(System.currentTimeMillis()));
            action.setDate(new Date(System.currentTimeMillis()));
        }

        if(request.buttonId == 1){ // Work Start
            List<AttendanceAction> lastEntries = attendanceActionRepository.findLastNByPersonnel(1, 0, request.personnelId);
            if(lastEntries.size() == 0 || lastEntries.get(0).getAttendanceEvent().getId() == 4) {
                action.setAttendanceEvent(attendanceEventRepository.getOne(1L)); //Work Start
                response.setMessageCode(4);
                attendanceActionRepository.save(action);
            }
            else{
                response.setMessageCode(1);
            }
        }
        else if(request.buttonId == 2){ // Break Start or End Button
            int startIndex = 0;
            List<AttendanceAction> lastEntries = attendanceActionRepository.findLastNByPersonnel(4, startIndex, request.personnelId);
            if (lastEntries.size() == 0) {
                response.setMessageCode(2);
            }
            else {
                boolean hitBreak = false;
                while (true) {

                    for (int i = 0; i < lastEntries.size(); i++) {
                        if (lastEntries.get(i).getAttendanceEvent().getId() == 2) { //Break Start
                            response.setMessageCode(8);
                            action.setAttendanceEvent(attendanceEventRepository.getOne(5L)); //Break End
                            attendanceActionRepository.save(action);
                            hitBreak = true;
                            break;
                        } else if (lastEntries.get(i).getAttendanceEvent().getId() == 1 ||
                                lastEntries.get(i).getAttendanceEvent().getId() == 5) { //Work Start
                            response.setMessageCode(5);
                            action.setAttendanceEvent(attendanceEventRepository.getOne(2L));
                            attendanceActionRepository.save(action);
                            hitBreak = true;
                            break;
                        } else if (lastEntries.get(i).getAttendanceEvent().getId() == 4) { //Work End
                            response.setMessageCode(2);
                            hitBreak = true;
                            break;
                        }
                    }

                    if (hitBreak) break;
                    else {
                        startIndex+=4;
                        lastEntries = attendanceActionRepository.findLastNByPersonnel(4, startIndex, request.personnelId);
                    }
                }
            }
        }
        else if(request.buttonId == 3){ // Official Start or End Button
            int startIndex = 0;
            List<AttendanceAction> lastEntries = attendanceActionRepository.findLastNByPersonnel(4, startIndex, request.personnelId);
            if (lastEntries.size() == 0) {
                response.setMessageCode(2);
            }
            else {
                boolean hitBreak = false;
                while (true) {
                    for (int i = 0; i < lastEntries.size(); i++) {
                        if (lastEntries.get(i).getAttendanceEvent().getId() == 3) { //Official Leave Start
                            response.setMessageCode(8);
                            action.setAttendanceEvent(attendanceEventRepository.getOne(6L)); //Leave End
                            attendanceActionRepository.save(action);
                            hitBreak = true;
                            break;
                        } else if (lastEntries.get(i).getAttendanceEvent().getId() == 1 ||
                                lastEntries.get(i).getAttendanceEvent().getId() == 6) { //Work Start
                            action.setAttendanceEvent(attendanceEventRepository.getOne(3L));
                            response.setMessageCode(6);
                            attendanceActionRepository.save(action);
                            hitBreak = true;
                            break;
                        } else if (lastEntries.get(i).getAttendanceEvent().getId() == 4) { //Work End
                            response.setMessageCode(2);
                            hitBreak = true;
                            break;
                        }
                    }

                    if (hitBreak) break;
                    else {
                        startIndex+=4;
                        lastEntries = attendanceActionRepository.findLastNByPersonnel(4, startIndex, request.personnelId);
                    }
                }
            }
        }
        else if(request.buttonId >= 4){ // End Work Button
            int startIndex = 0;

            List<AttendanceAction> lastEntries = attendanceActionRepository.findLastNByPersonnel(4, startIndex, request.personnelId);
            if (lastEntries.size() == 0) {
                response.setMessageCode(2);
            }
            else {
                boolean hitBreak = false;

                boolean hasOpenBreak = false;
                boolean hasOpenOfficial = false;
                while (true) {
                    for (int i = 0; i < lastEntries.size(); i++) {
                        if (lastEntries.get(i).getAttendanceEvent().getId() == 3 ||
                                lastEntries.get(i).getAttendanceEvent().getId() == 6) {
                            hasOpenOfficial = !hasOpenOfficial;
                        } else if (lastEntries.get(i).getAttendanceEvent().getId() == 2 ||
                                lastEntries.get(i).getAttendanceEvent().getId() == 5) {
                            hasOpenBreak = !hasOpenBreak;
                        } else if (lastEntries.get(i).getAttendanceEvent().getId() == 1) { //Work Start
                            if(!hasOpenBreak && !hasOpenOfficial) {
                                action.setAttendanceEvent(attendanceEventRepository.getOne(4L));
                                response.setMessageCode(7);
                                attendanceActionRepository.save(action);
                            }
                            hitBreak = true;
                            break;
                        } else if (lastEntries.get(i).getAttendanceEvent().getId() == 4) { //Work End
                            response.setMessageCode(2);
                            hitBreak = true;
                            break;
                        }
                    }

                    if (hitBreak) {
                        if (hasOpenBreak) {
                            response.setMessageCode(3);
                        }
                        else if (hasOpenOfficial) {
                            response.setMessageCode(9);
                        }
                        break;
                    }
                    else {
                        startIndex+=4;
                        lastEntries = attendanceActionRepository.findLastNByPersonnel(4, startIndex, request.personnelId);
                    }
                }
            }
        }

        if(action.getId() != null){
            response.setId(action.getId());
            response.setPersonnelId(action.getPersonnelId());
            response.setEvent(action.getAttendanceEvent().getName());
            response.setEventId(action.getAttendanceEvent().getId());
            response.setDateTime(action.getDateTime().toLocalDateTime());
        }
        return response;
    }
}
