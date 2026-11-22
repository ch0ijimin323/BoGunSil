package com.school.healthroom.global.init;

import com.school.healthroom.domain.auth.entity.Admin;
import com.school.healthroom.domain.auth.repository.AdminRepository;
import com.school.healthroom.domain.commoncode.entity.CommonCode;
import com.school.healthroom.domain.commoncode.repository.CommonCodeRepository;
import com.school.healthroom.domain.gradeclass.entity.GradeClass;
import com.school.healthroom.domain.gradeclass.repository.GradeClassRepository;
import com.school.healthroom.domain.setting.entity.SystemSetting;
import com.school.healthroom.domain.setting.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final AdminRepository adminRepository;
    private final SystemSettingRepository systemSettingRepository;
    private final CommonCodeRepository commonCodeRepository;
    private final GradeClassRepository gradeClassRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        initAdmin();
        initSystemSetting();
        initCommonCodes();
        initGradeClasses();
    }

    private void initAdmin() {
        if (adminRepository.count() == 0) {
            Admin defaultAdmin = Admin.builder()
                    .loginId("admin")
                    .password(passwordEncoder.encode("admin1234"))
                    .name("보건교사")
                    .build();
            adminRepository.save(defaultAdmin);
            log.info("[DataInitializer] 기본 관리자 계정(admin) 생성 완료");
        }
    }

    private void initSystemSetting() {
        if (systemSettingRepository.count() == 0) {
            systemSettingRepository.save(SystemSetting.createDefault());
            log.info("[DataInitializer] 기본 환경설정 생성 완료");
        }
    }

    private void initCommonCodes() {
        if (commonCodeRepository.count() == 0) {
            List<CommonCode> codes = List.of(
                    // SYMPTOM
                    createCode("SYMPTOM", "SYM_01", "두통", null, 1, "두통 및 편두통 증상"),
                    createCode("SYMPTOM", "SYM_02", "복통/소화불량", null, 2, "복통, 위경련 및 소화불량 증상"),
                    createCode("SYMPTOM", "SYM_03", "외상/찰과상", null, 3, "찰과상, 타박상 및 열상"),
                    createCode("SYMPTOM", "SYM_04", "감기/발열", null, 4, "감기 몸살, 인후통 및 발열 증상"),
                    createCode("SYMPTOM", "SYM_05", "안정/휴식", null, 5, "피로 및 컨디션 저하로 인한 안정 필요"),

                    // TREATMENT
                    createCode("TREATMENT", "TRT_01", "진통제 투여", "SYM_01", 1, "두통 완화를 위한 진통제 투여"),
                    createCode("TREATMENT", "TRT_02", "소화제 투여", "SYM_02", 2, "소화불량 및 복통 완화를 위한 소화제 투여"),
                    createCode("TREATMENT", "TRT_03", "소독 및 밴드 처치", "SYM_03", 3, "상처 부위 소독 및 드레싱/밴드 부착"),
                    createCode("TREATMENT", "TRT_04", "체온 측정 및 해열제 투여", "SYM_04", 4, "체온 측정 후 필요 시 해열제 투여"),
                    createCode("TREATMENT", "TRT_05", "침상 안정", "SYM_05", 5, "보건실 침상에서 일정 시간 안정 및 경과 관찰"),

                    // MEDICINE
                    createCode("MEDICINE", "MED_01", "타이레놀정 500mg", null, 1, "해열진통제"),
                    createCode("MEDICINE", "MED_02", "베아제정", null, 2, "종합소화제"),
                    createCode("MEDICINE", "MED_03", "포비돈 스틱", null, 3, "외상 소독약"),
                    createCode("MEDICINE", "MED_04", "부루펜정", null, 4, "소염진통해열제")
            );
            commonCodeRepository.saveAll(codes);
            log.info("[DataInitializer] 기본 공통코드 {}건 생성 완료", codes.size());
        }
    }

    private CommonCode createCode(String groupCode, String code, String codeName,
                                  String parentCode, int sortOrder, String description) {
        return CommonCode.builder()
                .groupCode(groupCode)
                .code(code)
                .codeName(codeName)
                .parentCode(parentCode)
                .sortOrder(sortOrder)
                .useYn(true)
                .description(description)
                .build();
    }

    private void initGradeClasses() {
        if (gradeClassRepository.count() == 0) {
            int currentYear = LocalDate.now().getYear();
            List<GradeClass> sampleClasses = new ArrayList<>();

            for (int grade = 1; grade <= 3; grade++) {
                for (int classNo = 1; classNo <= 3; classNo++) {
                    sampleClasses.add(GradeClass.builder()
                            .schoolYear(currentYear)
                            .grade(grade)
                            .classNo(classNo)
                            .teacherName(grade + "학년 " + classNo + "반 담임")
                            .studentCount(25)
                            .build());
                }
            }
            gradeClassRepository.saveAll(sampleClasses);
            log.info("[DataInitializer] {}학년도 기본 학년반 {}건 생성 완료", currentYear, sampleClasses.size());
        }
    }
}
