package io.vrap.rmf.codegen.common.generator.extensions;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.squareup.javapoet.ClassName;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.vrap.rmf.codegen.common.generator.core.GenerationResult;
import io.vrap.rmf.codegen.common.generator.core.GeneratorConfig;
import io.vrap.rmf.codegen.common.generator.util.TypeNameSwitch;
import io.vrap.rmf.raml.model.types.AnyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.misc.ErrorBuffer;

import javax.inject.Named;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;


public class STCodeGenerator {

    private final JavaSTFileSwitch javaSTFileSwitch = new JavaSTFileSwitch();
    private static Logger LOGGER = LoggerFactory.getLogger(STCodeGenerator.class);

    private Injector injector;

    private Flowable<AnyType> alltypes;

    private Path outputDir;

    private TypeNameSwitch typeNameSwitch;

    private Map<String, String> customMapping;

    @Inject
    public STCodeGenerator(Injector injector, Flowable<AnyType> alltypes,
                           @Named(GeneratorConfig.OUTPUT_FOLDER) Path outputDir,
                           TypeNameSwitch typeNameSwitch,
                           Map<String, String> customMapping) {

        this.injector = injector;
        this.alltypes = alltypes;
        this.outputDir = outputDir;
        this.typeNameSwitch = typeNameSwitch;
        this.customMapping = customMapping;
        injector.injectMembers(javaSTFileSwitch);

    }

    public Single<GenerationResult> generateClasses() {

        return alltypes.filter(anyType -> isNotMapped(anyType))
                .flatMap(anyType ->
                        Flowable.just(anyType).map(javaSTFileSwitch::doSwitch)
                                .map(o -> o.getInstanceOf("template"))
                                .doOnNext(st -> st.add("input", anyType))
                                .map(st -> generateFile(anyType, st)))
                .toList()
                .map(GenerationResult::of);
    }


    private Path generateFile(AnyType anyType, ST st) throws Exception {
        ClassName className = (ClassName) typeNameSwitch.doSwitch(anyType);
        String packagePath = className.reflectionName().replaceAll("\\.", "/") + ".java";
        Path outputPath = Paths.get(outputDir.toAbsolutePath().toString(), packagePath);
        outputPath.getParent().toFile().mkdirs();
        PrintWriter printWriter = new PrintWriter(outputPath.toFile());
        ErrorBuffer errorBuffer = new ErrorBuffer();
        st.write(new AutoIndentWriter(printWriter), errorBuffer);
        printWriter.close();
        if(!errorBuffer.errors.isEmpty()){
            LOGGER.error(errorBuffer.toString());
        }
        return outputPath;

    }


    boolean isNotMapped(AnyType anyType) {
        return customMapping.get(anyType.getName()) == null;
    }
}