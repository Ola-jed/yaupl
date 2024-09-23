package config

import net.bytebuddy.NamingStrategy
import net.bytebuddy.description.type.TypeDescription

object YNamingStrategyBuilder {
    fun build(packageName: String? = null): NamingStrategy.AbstractBase {
        return object : NamingStrategy.PrefixingRandom("") {
            override fun name(typeDescription: TypeDescription): String {
                return "${packageName ?: "yaupl.generated"}.${typeDescription.simpleName}"
            }
        }
    }
}