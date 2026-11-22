package com.school.healthroom.domain.carestudent.service;

import com.school.healthroom.domain.carestudent.dto.CareStudentDto;
import com.school.healthroom.domain.carestudent.entity.CareStudent;
import com.school.healthroom.domain.carestudent.repository.CareStudentRepository;
import com.school.healthroom.global.error.CustomException;
import com.school.healthroom.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareStudentService {

    private final CareStudentRepository careStudentRepository;

    public List<CareStudentDto.Response> getCareStudents(Integer schoolYear, Integer grade, Integer classNo, String keyword) {
        return careStudentRepository.search(schoolYear, grade, classNo, keyword)
                .stream()
                .map(CareStudentDto.Response::from)
                .toList();
    }

    public CareStudentDto.Response getCareStudent(Long id) {
        CareStudent careStudent = careStudentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "요양호자 정보를 찾을 수 없습니다."));
        return CareStudentDto.Response.from(careStudent);
    }

    @Transactional
    public CareStudentDto.Response createCareStudent(CareStudentDto.SaveRequest request) {
        CareStudent careStudent = CareStudent.builder()
                .schoolYear(request.getSchoolYear())
                .grade(request.getGrade())
                .classNo(request.getClassNo())
                .studentNo(request.getStudentNo())
                .studentName(request.getStudentName())
                .diseaseName(request.getDiseaseName())
                .emergencyContact(request.getEmergencyContact())
                .careGuide(request.getCareGuide())
                .build();

        return CareStudentDto.Response.from(careStudentRepository.save(careStudent));
    }

    @Transactional
    public CareStudentDto.Response updateCareStudent(Long id, CareStudentDto.SaveRequest request) {
        CareStudent careStudent = careStudentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "요양호자 정보를 찾을 수 없습니다."));

        careStudent.update(
                request.getSchoolYear(),
                request.getGrade(),
                request.getClassNo(),
                request.getStudentNo(),
                request.getStudentName(),
                request.getDiseaseName(),
                request.getEmergencyContact(),
                request.getCareGuide()
        );
        return CareStudentDto.Response.from(careStudent);
    }

    @Transactional
    public void deleteCareStudent(Long id) {
        CareStudent careStudent = careStudentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "요양호자 정보를 찾을 수 없습니다."));
        careStudentRepository.delete(careStudent);
    }
}
