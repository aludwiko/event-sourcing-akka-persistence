package info.ludwikowski.es

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import info.ludwikowski.es.base.BaseSpec
import org.scalatest.FlatSpecLike

class PackageStructureValidationSpec extends BaseSpec with FlatSpecLike {

  private val classes: JavaClasses = new ClassFileImporter().importPackages("info.ludwikowski.es")

  "PackageStructureValidation" should "check no dependency from domain to application" in {

    // given
    val rule = noClasses().that().resideInAPackage("..domain..").should().accessClassesThat().resideInAPackage("..application..")

    // when // then
    rule.check(classes)
  }

  it should "check no dependency from domain to akka framework" in {

    // given
    val rule = noClasses().that().resideInAPackage("..domain..").should().accessClassesThat().resideInAPackage("..akka..")

    // when // then
    rule.check(classes)
  }
}
