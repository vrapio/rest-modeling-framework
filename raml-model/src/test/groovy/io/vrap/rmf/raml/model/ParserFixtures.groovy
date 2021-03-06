package io.vrap.rmf.raml.model

import io.vrap.rmf.raml.persistence.antlr.RAMLParser
import io.vrap.rmf.raml.persistence.antlr.RamlNodeTokenSource
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.TokenStream
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.URIConverter

trait ParserFixtures {
    RAMLParser parser(String input, URI uri, URIConverter uriConverter) {
        def strippedInput = input.stripIndent()
        final RamlNodeTokenSource lexer = new RamlNodeTokenSource(strippedInput, uri, uriConverter);
        final TokenStream tokenStream = new CommonTokenStream(lexer);
        new RAMLParser(tokenStream)
    }
}