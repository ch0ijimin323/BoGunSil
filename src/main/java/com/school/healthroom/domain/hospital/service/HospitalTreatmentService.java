package com.school.healthroom.domain.hospital.service;

import com.school.healthroom.domain.hospital.dto.HospitalTreatmentDto;
import com.school.healthroom.domain.hospital.entity.HospitalTreatment;
import com.school.healthroom.domain.hospital.repository.HospitalTreatmentRepository;
import com.school.healthroom.global.error.CustomException;
import com.school.healthroom.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HospitalTreatmentService {

    private final HospitalTreatmentRepository hospitalTreatmentRepository;

    public List<HospitalTreatmentDto.Response> getHospitalTreatments(LocalDate startDate, LocalDate endDate, Integer grade, String keyword) {
        return hospitalTreatmentRepository.search(startDate, endDate, grade, keyword)
                .stream()
                .map(HospitalTreatmentDto.Response::from)
                .toList();
    }

    public HospitalTreatmentDto.Response getHospitalTreatment(Long id) {
        HospitalTreatment entity = hospitalTreatmentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "병원치료 내역을 찾을 수 없습니다."));
        return HospitalTreatmentDto.Response.from(entity);
    }

    @Transactional
    public HospitalTreatmentDto.Response createHospitalTreatment(HospitalTreatmentDto.SaveRequest request) {
        HospitalTreatment entity = HospitalTreatment.builder()
                .visitDate(request.getVisitDate())
                .grade(request.getGrade())
                .classNo(request.getClassNo())
                .studentNo(request.getStudentNo())
                .studentName(request.getStudentName())
                .symptom(request.getSymptom())
                .hospitalName(request.getHospitalName())
                .companion(request.getCompanion())
                .treatmentResult(request.getTreatmentResult())
                .build();

        return HospitalTreatmentDto.Response.from(hospitalTreatmentRepository.save(entity));
    }

    @Transactional
    public HospitalTreatmentDto.Response updateHospitalTreatment(Long id, HospitalTreatmentDto.SaveRequest request) {
        HospitalTreatment entity = hospitalTreatmentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "병원치료 내역을 찾을 수 없습니다."));

        entity.update(
                request.getVisitDate(),
                request.getGrade(),
                request.getClassNo(),
                request.getStudentNo(),
                request.getStudentName(),
                request.getSymptom(),
                request.getHospitalName(),
                request.getCompanion(),
                request.getTreatmentResult()
        );
        return HospitalTreatmentDto.Response.from(entity);
    }

    @Transactional
    public void deleteHospitalTreatment(Long id) {
        HospitalTreatment entity = hospitalTreatmentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "병원치료 내역을 찾을 수 없습니다."));
        hospitalTreatmentRepository.delete(entity);
    }
}
