package io.vrap.rmf.raml.generic.generator.php

import com.google.common.io.Resources
import io.vrap.raml.generic.generator.ResourceFixtures
import io.vrap.rmf.raml.model.modules.Api
import io.vrap.rmf.raml.model.types.AnyType
import io.vrap.rmf.raml.persistence.RamlResourceSet
import io.vrap.rmf.raml.persistence.antlr.RAMLCustomLexer
import io.vrap.rmf.raml.persistence.antlr.RAMLParser
import io.vrap.rmf.raml.persistence.constructor.ApiConstructor
import io.vrap.rmf.raml.persistence.constructor.Scope
import org.antlr.v4.runtime.CommonTokenFactory
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.TokenStream
import org.assertj.core.util.Files
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.URIConverter
import spock.lang.Shared
import spock.lang.Specification

import java.nio.charset.StandardCharsets

class TypeGeneratorTest extends Specification implements ResourceFixtures {
    @Shared
    ResourceSet resourceSet = new RamlResourceSet()
    @Shared
    URI uri = URI.createURI("test.raml");

    def "generate simple interface"() {
        when:
        Api api = constructApi(
                '''\
        types:
            Person:
                properties:
                    name: string
        ''')
        then:
        String result = generate(TypesGenerator.TYPE_INTERFACE, api.types.get(0));
        result == fileContent("simple-interface.php")
    }

    def "generate extended interface"() {
        when:
        Api api = constructApi(
                '''\
        types:
            Person:
                properties:
                    name: string
            User:
                type: Person
                properties:
                    role: string
        ''')
        then:
        String result = generate(TypesGenerator.TYPE_INTERFACE, api.types.get(1));
        result == fileContent("extended-interface.php")
    }

    def "generate simple model"() {
        when:
        Api api = constructApi(
                '''\
        types:
            Person:
                properties:
                    name: string
            User:
                type: Person
                properties:
                    role: string
        ''')
        then:
        String result = generate(TypesGenerator.TYPE_MODEL, api.types.get(0));
        result == fileContent("simple-model.php")
    }

    def "generate extended model"() {
        when:
        Api api = constructApi(
                '''\
        types:
            Person:
                properties:
                    name: string
            User:
                type: Person
                properties:
                    role: string
        ''')
        then:
        String result = generate(TypesGenerator.TYPE_MODEL, api.types.get(1));
        result == fileContent("extended-model.php")
    }

    def "generate interface getter"() {
        when:
        Api api = constructApi(
                '''\
        types:
            Person:
                properties:
                    address: Address
            Address:
                properties:
                    street: string
        ''')
        then:
        String result = generate(TypesGenerator.TYPE_INTERFACE, api.types.get(0));
        result == fileContent("interface-getter.php")
    }

    def "generate model getter"() {
        when:
        Api api = constructApi(
                '''\
        types:
            Person:
                properties:
                    address: Address
            Address:
                properties:
                    street: string
        ''')
        then:
        String result = generate(TypesGenerator.TYPE_MODEL, api.types.get(0));
        result == fileContent("model-getter.php")
    }

    def "generate simple discriminator"() {
        when:
        Api api = constructApi(
                '''\
        types:
            Person:
                discriminator: kind
                properties:
                    kind: string
            User:
                type: Person
                discriminatorValue: user
        ''')
        then:
        String baseClass = generate(TypesGenerator.TYPE_MODEL, api.types.get(0));
        baseClass == fileContent("simple-discriminator-base.php")

        String kindClass = generate(TypesGenerator.TYPE_MODEL, api.types.get(1));
        kindClass == fileContent("simple-discriminator-kind.php")
    }

    String generate(final String generateType, final AnyType type) {
        TypesGenerator generator = new TypesGenerator()
        return generator.generateType(generator.createVisitor(TypesGenerator.PACKAGE_NAME, generateType), type);
    }

    Api constructApi(final String input) {
        RAMLParser parser = parser(input)
        def apiConstructor = new ApiConstructor()
        Scope scope = Scope.of(resourceSet.createResource(uri))
        return (Api)apiConstructor.construct(parser, scope)
    }

    RAMLParser parser(final String input) {
        final URIConverter uriConverter = resourceSet.getURIConverter();
        def strippedInput = input.stripIndent()
        final RAMLCustomLexer lexer = new RAMLCustomLexer(strippedInput, uri, uriConverter);
        final TokenStream tokenStream = new CommonTokenStream(lexer);
        lexer.setTokenFactory(CommonTokenFactory.DEFAULT);
        return new RAMLParser(tokenStream)
    }

    String fileContent(final String fileName) {
        return Files.contentOf(new File(Resources.getResource(fileName).toURI()), StandardCharsets.UTF_8)
    }
}
