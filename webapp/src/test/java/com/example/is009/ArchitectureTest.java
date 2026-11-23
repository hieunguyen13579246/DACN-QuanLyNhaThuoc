package com.example.is009;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.example.is009", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    private static final String BASE_PKG = "com.example.is009.";

    private static final Map<String, Integer> MODULE_TIERS = Map.ofEntries(
            // Tầng 0 (nền)
            Map.entry("config", 0),
            Map.entry("common", 0),
            Map.entry("exception", 0),
            Map.entry("security", 0),
            // Tầng 1 (danh mục & tiện ích)
            Map.entry("auth", 1),
            Map.entry("khuvuc", 1),
            Map.entry("nhathuoc", 1),
            Map.entry("nguoidung", 1),
            Map.entry("nhacungcap", 1),
            Map.entry("thuoc", 1),
            Map.entry("cauhinh", 1),
            Map.entry("barcode", 1),
            // Tầng 2
            Map.entry("khachhang", 2),
            Map.entry("donthuoc", 2),
            Map.entry("kho", 2),
            // Tầng 3
            Map.entry("banhang", 3),
            Map.entry("canhbao", 3),
            // Tầng 4
            Map.entry("thongke", 4)
    );

    @ArchTest
    static final ArchRule khongCoVongPhuThuocGiuaCacModule =
            slices().matching("com.example.is009.(*)..")
                    .should().beFreeOfCycles();

    @ArchTest
    static final ArchRule controllerKhongPhuThuocRepository =
            noClasses().that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().resideInAPackage("..repository..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule serviceKhongPhuThuocController =
            noClasses().that().resideInAPackage("..service..")
                    .should().dependOnClassesThat().resideInAPackage("..controller..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule entityKhongPhuThuocServiceControllerDto =
            noClasses().that().resideInAPackage("..entity..")
                    .should().dependOnClassesThat().resideInAnyPackage("..service..", "..controller..", "..dto..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule moiModuleChiPhuThuocModuleTangThapHon = classes().that().resideInAPackage("com.example.is009..*")
            .should(new ArchCondition<>("chỉ phụ thuộc module ở tầng thấp hơn và không gọi trực tiếp repository/entity của module khác") {
                @Override
                public void check(JavaClass sourceClass, ConditionEvents events) {
                    String sourceModule = extractModule(sourceClass.getPackageName());
                    if (sourceModule == null || !MODULE_TIERS.containsKey(sourceModule)) {
                        return;
                    }
                    int sourceTier = MODULE_TIERS.get(sourceModule);

                    sourceClass.getDirectDependenciesFromSelf().forEach(dep -> {
                        JavaClass targetClass = dep.getTargetClass();
                        String targetPkg = targetClass.getPackageName();
                        if (!targetPkg.startsWith(BASE_PKG)) {
                            return;
                        }
                        String targetModule = extractModule(targetPkg);
                        if (targetModule == null || targetModule.equals(sourceModule)) {
                            return;
                        }
                        Integer targetTier = MODULE_TIERS.get(targetModule);
                        if (targetTier == null) {
                            return;
                        }
                        if (sourceTier == 0 && targetTier == 0) {
                            return;
                        }
                        if (targetTier >= sourceTier) {
                            String msg = String.format(
                                    "Module '%s' (Tầng %d, lớp %s) không được phụ thuộc module '%s' (Tầng %d, lớp %s)",
                                    sourceModule, sourceTier, sourceClass.getSimpleName(),
                                    targetModule, targetTier, targetClass.getSimpleName());
                            events.add(SimpleConditionEvent.violated(sourceClass, msg));
                        }
                        if (targetPkg.contains(".repository") || targetPkg.contains(".entity")) {
                            String msg = String.format(
                                    "Lớp %s (module '%s') không được truy cập trực tiếp repository/entity %s của module '%s'",
                                    sourceClass.getName(), sourceModule, targetClass.getName(), targetModule);
                            events.add(SimpleConditionEvent.violated(sourceClass, msg));
                        }
                    });
                }
            }).allowEmptyShould(true);

    @ArchTest
    static final ArchRule entityKhongDungManyToOneSangModuleKhac = classes().that().resideInAPackage("..entity..")
            .should(new ArchCondition<>("không dùng @ManyToOne/@OneToMany/@OneToOne/@ManyToMany sang module khác") {
                @Override
                public void check(JavaClass entityClass, ConditionEvents events) {
                    String entityModule = extractModule(entityClass.getPackageName());
                    for (JavaField field : entityClass.getAllFields()) {
                        boolean hasJpaRelation = field.isAnnotatedWith(ManyToOne.class)
                                || field.isAnnotatedWith(OneToMany.class)
                                || field.isAnnotatedWith(OneToOne.class)
                                || field.isAnnotatedWith(ManyToMany.class);
                        if (hasJpaRelation) {
                            String targetPkg = field.getRawType().getPackageName();
                            String targetModule = extractModule(targetPkg);
                            if (targetModule != null && !targetModule.equals(entityModule)) {
                                String msg = String.format(
                                        "Entity %s (module '%s') dùng quan hệ JPA trên trường '%s' sang module '%s'",
                                        entityClass.getSimpleName(), entityModule, field.getName(), targetModule);
                                events.add(SimpleConditionEvent.violated(entityClass, msg));
                            }
                        }
                    }
                }
            }).allowEmptyShould(true);

    private static String extractModule(String packageName) {
        if (packageName == null || !packageName.startsWith(BASE_PKG)) {
            return null;
        }
        String rest = packageName.substring(BASE_PKG.length());
        int dotIdx = rest.indexOf('.');
        return dotIdx >= 0 ? rest.substring(0, dotIdx) : rest;
    }
}
