package com.school.healthroom.domain.gradeclass.service;

import com.school.healthroom.domain.gradeclass.dto.GradeClassDto;
import com.school.healthroom.domain.gradeclass.entity.GradeClass;
import com.school.healthroom.domain.gradeclass.repository.GradeClassRepository;
import com.school.healthroom.global.error.CustomException;
import com.school.healthroom.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GradeClassService {

    private final GradeClassRepository gradeClassRepository;

    public List<GradeClassDto.Response> getGradeClasses(Integer schoolYear, Integer grade) {
        return gradeClassRepository.search(schoolYear, grade)
                .stream()
                .map(GradeClassDto.Response::from)
                .toList();
    }

    @Transactional
    public GradeClassDto.Response createGradeClass(GradeClassDto.CreateRequest request) {
        if (gradeClassRepository.existsBySchoolYearAndGradeAndClassNo(
                request.getSchoolYear(), request.getGrade(), request.getClassNo())) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE, "해당 학년도/학년/반이 이미 존재합니다.");
        }

        GradeClass gradeClass = GradeClass.builder()
                .schoolYear(request.getSchoolYear())
                .grade(request.getGrade())
                .classNo(request.getClassNo())
                .teacherName(request.getTeacherName())
                .studentCount(request.getStudentCount() != null ? request.getStudentCount() : 0)
                .build();

        return GradeClassDto.Response.from(gradeClassRepository.save(gradeClass));
    }

    @Transactional
    public GradeClassDto.Response updateGradeClass(Long id, GradeClassDto.UpdateRequest request) {
        GradeClass gradeClass = gradeClassRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "학년반 정보를 찾을 수 없습니다."));

        if (gradeClassRepository.existsBySchoolYearAndGradeAndClassNoAndIdNot(
                gradeClass.getSchoolYear(), request.getGrade(), request.getClassNo(), id)) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE, "해당 학년도/학년/반이 이미 존재합니다.");
        }

        gradeClass.update(
                request.getGrade(),
                request.getClassNo(),
                request.getTeacherName(),
                request.getStudentCount()
        );
        return GradeClassDto.Response.from(gradeClass);
    }

    @Transactional
    public void deleteGradeClass(Long id) {
        GradeClass gradeClass = gradeClassRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "학년반 정보를 찾을 수 없습니다."));
        gradeClassRepository.delete(gradeClass);
    }

    @Transactional
    public GradeClassDto.ExcelUploadResponse uploadExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.EXCEL_PARSE_ERROR, "업로드할 엑셀 파일이 비어 있습니다.");
        }

        int insertedCount = 0;
        int updatedCount = 0;
        DataFormatter formatter = new DataFormatter();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();

            for (int rowIdx = 1; rowIdx <= lastRowNum; rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null || isRowEmpty(row, formatter)) {
                    continue;
                }

                Integer schoolYear = parseIntCell(row.getCell(0), formatter, "학년도", rowIdx);
                Integer grade = parseIntCell(row.getCell(1), formatter, "학년", rowIdx);
                Integer classNo = parseIntCell(row.getCell(2), formatter, "반", rowIdx);
                String teacherName = parseStringCell(row.getCell(3), formatter);
                Integer studentCount = parseOptionalIntCell(row.getCell(4), formatter, 0);

                Optional<GradeClass> existingOpt = gradeClassRepository
                        .findBySchoolYearAndGradeAndClassNo(schoolYear, grade, classNo);

                if (existingOpt.isPresent()) {
                    existingOpt.get().updateTeacherAndCount(teacherName, studentCount);
                    updatedCount++;
                } else {
                    GradeClass created = GradeClass.builder()
                            .schoolYear(schoolYear)
                            .grade(grade)
                            .classNo(classNo)
                            .teacherName(teacherName)
                            .studentCount(studentCount)
                            .build();
                    gradeClassRepository.save(created);
                    insertedCount++;
                }
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.EXCEL_PARSE_ERROR, "엑셀 파싱 실패: " + e.getMessage());
        }

        return GradeClassDto.ExcelUploadResponse.builder()
                .totalProcessed(insertedCount + updatedCount)
                .insertedCount(insertedCount)
                .updatedCount(updatedCount)
                .build();
    }

    private boolean isRowEmpty(Row row, DataFormatter formatter) {
        for (int c = 0; c < 5; c++) {
            Cell cell = row.getCell(c);
            if (cell != null && !formatter.formatCellValue(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private Integer parseIntCell(Cell cell, DataFormatter formatter, String fieldName, int rowIdx) {
        if (cell == null) {
            throw new CustomException(ErrorCode.EXCEL_PARSE_ERROR, (rowIdx + 1) + "행의 " + fieldName + " 값이 비어 있습니다.");
        }
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            }
            String value = formatter.formatCellValue(cell).trim().replaceAll("[^0-9-]", "");
            return Integer.parseInt(value);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.EXCEL_PARSE_ERROR, (rowIdx + 1) + "행의 " + fieldName + " 숫자 형식이 올바르지 않습니다.");
        }
    }

    private Integer parseOptionalIntCell(Cell cell, DataFormatter formatter, int defaultValue) {
        if (cell == null) {
            return defaultValue;
        }
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            }
            String value = formatter.formatCellValue(cell).trim().replaceAll("[^0-9-]", "");
            return value.isEmpty() ? defaultValue : Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String parseStringCell(Cell cell, DataFormatter formatter) {
        if (cell == null) {
            return null;
        }
        String value = formatter.formatCellValue(cell).trim();
        return value.isEmpty() ? null : value;
    }
}
