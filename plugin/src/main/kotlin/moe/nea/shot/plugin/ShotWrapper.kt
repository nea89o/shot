package moe.nea.shot.plugin

import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.AttributeContainer
import org.gradle.api.attributes.HasConfigurableAttributes

data class ShotWrapper(val attribute: Attribute<String>, val name: String) {
    operator fun invoke(attributes: AttributeContainer) {
        applyTo(attributes)
    }

    operator fun invoke(dep: HasConfigurableAttributes<*>) {
        applyTo(dep)
    }

    fun applyTo(attributes: AttributeContainer) {
        attributes.attribute(attribute, name)
    }

    fun applyTo(dep: HasConfigurableAttributes<*>) {
        applyTo(dep.attributes)
    }

}
